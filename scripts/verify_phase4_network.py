from __future__ import annotations

import re
import sys
from pathlib import Path, PurePosixPath


ROOT = Path(__file__).resolve().parent.parent
NETWORK_FILES = [
    "src/main/java/tfar/classicbar/network/ClassicBarNetwork.java",
    "src/main/java/tfar/classicbar/network/SyncHandler.java",
    "src/main/java/tfar/classicbar/network/VanillaFoodDataPayload.java",
    "src/main/java/tfar/classicbar/network/Message.java",
    "src/main/java/tfar/classicbar/network/MessageExhaustionSync.java",
    "src/main/java/tfar/classicbar/network/MessageHydrationSync.java",
    "src/main/java/tfar/classicbar/network/MessageSaturationSync.java",
    "src/main/java/tfar/classicbar/network/MessageThirstExhaustionSync.java",
    "src/main/java/tfar/classicbar/network/NetworkHelper.java",
]
QUARANTINE_EXCLUDES = [
    "tfar/classicbar/compat/**",
    "tfar/classicbar/impl/overlays/mod/**",
]
FORBIDDEN_ACTIVE_NETWORK_TOKENS = [
    "ClassicBarNetwork",
    "SyncHandler",
    "VanillaFoodDataPayload",
    "RegisterPayloadHandlersEvent",
    "PlayerTickEvent",
    "PlayerLoggedOutEvent",
    "PacketDistributor",
    "NetworkRegistry",
    "SimpleChannel",
    "presentOnServer",
]


class CheckContext:
    def __init__(self) -> None:
        self.failures: list[str] = []
        self.notes: list[str] = []

    def fail(self, message: str) -> None:
        self.failures.append(message)

    def note(self, message: str) -> None:
        self.notes.append(message)


def read_text(relative_path: str, ctx: CheckContext) -> str:
    path = ROOT / relative_path
    if not path.exists():
        ctx.fail(f"Missing required file: {relative_path}")
        return ""
    return path.read_text(encoding="utf-8")


def require_contains(text: str, needle: str, label: str, ctx: CheckContext) -> None:
    if needle not in text:
        ctx.fail(f"{label}: expected to contain {needle!r}")


def require_absent(text: str, needle: str, label: str, ctx: CheckContext) -> None:
    if needle in text:
        ctx.fail(f"{label}: should not contain {needle!r}")


def path_matches_any(path: str, patterns: list[str]) -> bool:
    posix_path = PurePosixPath(path)
    return any(posix_path.match(pattern) for pattern in patterns)


def collect_source_token_hits(ctx: CheckContext) -> None:
    source_root = ROOT / "src" / "main" / "java"
    active_hits: list[str] = []
    quarantined_hits: list[str] = []

    for file in source_root.rglob("*.java"):
        relative_path = file.relative_to(ROOT).as_posix()
        source_set_path = relative_path.removeprefix("src/main/java/")
        text = file.read_text(encoding="utf-8")
        for token in FORBIDDEN_ACTIVE_NETWORK_TOKENS:
            if token in text:
                hit = f"{relative_path} -> {token}"
                if path_matches_any(source_set_path, QUARANTINE_EXCLUDES):
                    quarantined_hits.append(hit)
                else:
                    active_hits.append(hit)

    if active_hits:
        for hit in active_hits:
            ctx.fail(f"Active source should not contain network restore token: {hit}")
    else:
        ctx.note("Active source contains no network restore tokens.")

    if quarantined_hits:
        ctx.note(
            "Quarantined build-excluded sources still contain network tokens and are accepted only because they remain excluded from active compile: "
            + ", ".join(quarantined_hits)
        )
    else:
        ctx.note("No build-excluded quarantined source currently contains network restore tokens.")


def main() -> int:
    ctx = CheckContext()

    build_gradle = read_text("build.gradle", ctx)
    classic_bar = read_text("src/main/java/tfar/classicbar/ClassicBar.java", ctx)
    classic_bar_client = read_text("src/main/java/tfar/classicbar/client/ClassicBarClient.java", ctx)
    metadata = read_text("src/main/resources/META-INF/neoforge.mods.toml", ctx)

    require_absent(build_gradle, "exclude 'tfar/classicbar/network/**'", "build.gradle network quarantine", ctx)
    require_contains(classic_bar, "@Mod(value = ClassicBar.MODID, dist = Dist.CLIENT)", "ClassicBar client-only root", ctx)
    require_contains(classic_bar, "ClassicBarClient.init(modBus);", "ClassicBar client bootstrap", ctx)
    require_absent(classic_bar, "ClassicBarNetwork", "ClassicBar payload registration removal", ctx)
    require_absent(classic_bar, "SyncHandler", "ClassicBar server tick sync removal", ctx)
    require_absent(classic_bar_client, "RegisterPayloadHandlersEvent", "ClassicBarClient payload listener removal", ctx)
    require_absent(classic_bar_client, "PlayerTickEvent", "ClassicBarClient server tick listener removal", ctx)
    require_absent(classic_bar_client, "PlayerLoggedOutEvent", "ClassicBarClient logout sync listener removal", ctx)

    client_side_declarations = len(re.findall(r'side="CLIENT"', metadata))
    if client_side_declarations != 2:
        ctx.fail("neoforge.mods.toml: expected exactly two client-side dependency declarations for NeoForge and Minecraft")
    require_absent(metadata, 'side="BOTH"', "neoforge.mods.toml server-optional dependency removal", ctx)

    for file_path in NETWORK_FILES:
        if (ROOT / file_path).exists():
            ctx.fail(f"Legacy network file should stay removed for the pure-client route: {file_path}")

    collect_source_token_hits(ctx)

    if ctx.failures:
        print("Phase 4 pure-client network verification: RED")
        for note in ctx.notes:
            print(f"- {note}")
        for failure in ctx.failures:
            print(f"- {failure}")
        return 1

    print("Phase 4 pure-client network verification: GREEN")
    for note in ctx.notes:
        print(f"- {note}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
