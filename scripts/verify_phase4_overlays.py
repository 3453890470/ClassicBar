from __future__ import annotations

import re
import zipfile
from pathlib import Path, PurePosixPath


ROOT = Path(__file__).resolve().parent.parent
EXPECTED_QUARANTINE_EXCLUDES = [
    "tfar/classicbar/compat/**",
    "tfar/classicbar/impl/overlays/mod/**",
]
ACTIVE_VANILLA_OVERLAYS = [
    "Health",
    "Armor",
    "Absorption",
    "Hunger",
    "ArmorToughness",
    "MountHealth",
    "Air",
]
QUARANTINED_OVERLAY_CONSTRUCTORS = [
    "new Blood(",
    "new Thirst(",
    "new StaminaB(",
    "new Feathers(",
]
ACTIVE_SOURCE_FORBIDDEN_TOKENS = [
    "import tfar.classicbar.compat",
    "import tfar.classicbar.impl.overlays.mod",
    "ModCompat",
    "VampirismHelper",
    *QUARANTINED_OVERLAY_CONSTRUCTORS,
]
THIRD_PARTY_MOD_IDS = ["toughasnails", "vampirism", "parcool", "feathers"]
QUARANTINED_OUTPUT_PREFIXES = [
    "tfar/classicbar/compat/",
    "tfar/classicbar/impl/overlays/mod/",
]
TARGET_MOD_JAR_PREFIX = "classicbar-"
IGNORED_JAR_SUFFIXES = ("-sources.jar", "-javadoc.jar")
RESERVED_COMPAT_MESSAGE_EN = "Reserved for future compatibility work. No active effect in this build."
RESERVED_COMPAT_MESSAGE_ZH = "为未来兼容功能预留。本版本中没有实际效果。"


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


def extract_source_set_excludes(build_gradle: str) -> list[str]:
    return re.findall(r"exclude\s+'([^']+)'", build_gradle)


def path_matches_any(path: str, patterns: list[str]) -> bool:
    posix_path = PurePosixPath(path)
    return any(posix_path.match(pattern) for pattern in patterns)


def collect_active_source_token_hits(patterns: list[str], ctx: CheckContext) -> None:
    source_root = ROOT / "src" / "main" / "java"
    for file in source_root.rglob("*.java"):
        relative_path = file.relative_to(ROOT).as_posix()
        source_set_path = relative_path.removeprefix("src/main/java/")
        if path_matches_any(source_set_path, EXPECTED_QUARANTINE_EXCLUDES):
            continue

        text = file.read_text(encoding="utf-8")
        for token in patterns:
            if token in text:
                ctx.fail(f"{relative_path}: active source should not contain {token!r}")


def validate_third_party_dependency_blocks(metadata: str, ctx: CheckContext) -> None:
    blocks = re.findall(r"\[\[dependencies\.classicbar\]\](.*?)(?=\n\[\[dependencies\.classicbar\]\]|\Z)", metadata, flags=re.DOTALL)
    for mod_id in THIRD_PARTY_MOD_IDS:
        matching_blocks = [block for block in blocks if f'modId="{mod_id}"' in block]
        if matching_blocks:
            ctx.fail(
                "neoforge.mods.toml: "
                f"{mod_id} dependency blocks must stay absent during the first-pass compat quarantine"
            )


def is_target_mod_jar(jar_file: Path) -> bool:
    return (
        jar_file.name.startswith(TARGET_MOD_JAR_PREFIX)
        and jar_file.suffix == ".jar"
        and not any(jar_file.name.endswith(suffix) for suffix in IGNORED_JAR_SUFFIXES)
    )


def collect_target_mod_jars(ctx: CheckContext) -> list[Path]:
    libs_root = ROOT / "build" / "libs"
    if not libs_root.exists():
        ctx.fail("build/libs is missing; run .\\gradlew.bat clean build before verify_phase4_overlays.py")
        return []

    jar_files = sorted(path for path in libs_root.glob("*.jar") if is_target_mod_jar(path))
    if not jar_files:
        ctx.fail(
            "build/libs: no target ClassicBar mod jar was found; "
            "run .\\gradlew.bat clean build and verify the produced mod jar before declaring GREEN"
        )
        return []

    ctx.note(
        "Scanned target mod jar(s): "
        + ", ".join(jar_file.relative_to(ROOT).as_posix() for jar_file in jar_files)
    )
    return jar_files


def validate_built_outputs(ctx: CheckContext) -> None:
    classes_root = ROOT / "build" / "classes" / "java" / "main"
    if classes_root.exists():
        for prefix in QUARANTINED_OUTPUT_PREFIXES:
            output_dir = classes_root / Path(prefix)
            if output_dir.exists():
                quarantined_classes = sorted(path.relative_to(classes_root).as_posix() for path in output_dir.rglob("*.class"))
                if quarantined_classes:
                    ctx.fail("build/classes/java/main: quarantined compat classes leaked into active compile output: " + ", ".join(quarantined_classes))

    for jar_file in collect_target_mod_jars(ctx):
        with zipfile.ZipFile(jar_file) as jar:
            leaked_entries = sorted(
                name for name in jar.namelist()
                if name.endswith(".class") and any(name.startswith(prefix) for prefix in QUARANTINED_OUTPUT_PREFIXES)
            )
        if leaked_entries:
            ctx.fail(f"{jar_file.relative_to(ROOT).as_posix()}: quarantined compat classes leaked into built jar: {', '.join(leaked_entries)}")


def main() -> int:
    ctx = CheckContext()

    build_gradle = read_text("build.gradle", ctx)
    event_handler = read_text("src/main/java/tfar/classicbar/client/EventHandler.java", ctx)
    metadata = read_text("src/main/resources/META-INF/neoforge.mods.toml", ctx)
    en_us = read_text("src/main/resources/assets/classicbar/lang/en_us.json", ctx)
    zh_cn = read_text("src/main/resources/assets/classicbar/lang/zh_cn.json", ctx)

    quarantine_excludes = extract_source_set_excludes(build_gradle)
    if sorted(quarantine_excludes) != sorted(EXPECTED_QUARANTINE_EXCLUDES):
        ctx.fail(
            "build.gradle quarantine excludes changed unexpectedly: "
            f"expected {EXPECTED_QUARANTINE_EXCLUDES}, got {quarantine_excludes}"
        )

    require_contains(build_gradle, "TASK-04 compat quarantine", "build.gradle quarantine comment", ctx)
    require_contains(build_gradle, "separate recovery task", "build.gradle quarantine comment", ctx)

    for overlay_class in ACTIVE_VANILLA_OVERLAYS:
        require_contains(event_handler, f"new {overlay_class}()", "EventHandler active overlay registration", ctx)

    for compat_constructor in QUARANTINED_OVERLAY_CONSTRUCTORS:
        require_absent(event_handler, compat_constructor, "EventHandler compat overlay registration", ctx)

    require_absent(event_handler, "import tfar.classicbar.compat", "EventHandler compat import guard", ctx)
    require_absent(event_handler, "import tfar.classicbar.impl.overlays.mod", "EventHandler compat overlay import guard", ctx)
    collect_active_source_token_hits(ACTIVE_SOURCE_FORBIDDEN_TOKENS, ctx)
    validate_built_outputs(ctx)

    validate_third_party_dependency_blocks(metadata, ctx)
    for mod_id in THIRD_PARTY_MOD_IDS:
        require_absent(metadata, f'modId="{mod_id}"', f"neoforge.mods.toml {mod_id} dependency absence guard", ctx)

    require_absent(en_us, "TASK-03", "en_us internal task label leak", ctx)
    require_absent(en_us, "TASK-04", "en_us internal task label leak", ctx)
    require_absent(zh_cn, "TASK-03", "zh_cn internal task label leak", ctx)
    require_absent(zh_cn, "TASK-04", "zh_cn internal task label leak", ctx)

    require_contains(
        en_us,
        RESERVED_COMPAT_MESSAGE_EN,
        "en_us mod_support section tooltip",
        ctx,
    )
    require_contains(
        zh_cn,
        RESERVED_COMPAT_MESSAGE_ZH,
        "zh_cn mod_support section tooltip",
        ctx,
    )
    require_contains(en_us, "Reserved compatibility section for Tough As Nails. No active effect in this build.", "en_us Tough As Nails quarantine messaging", ctx)
    require_contains(en_us, "Reserved compatibility section for Vampirism. No active effect in this build.", "en_us Vampirism quarantine messaging", ctx)
    require_contains(en_us, "Reserved compatibility section for ParCool. No active effect in this build.", "en_us ParCool quarantine messaging", ctx)
    require_contains(en_us, "no compatible NeoForge 1.21.1 build is currently available", "en_us Feathers quarantine messaging", ctx)
    require_contains(en_us, "Enabling this toggle does not restore Tough As Nails support in this build.", "en_us compat restore strategy messaging", ctx)
    require_contains(zh_cn, "为 Tough As Nails 兼容功能预留。本版本中没有实际效果。", "zh_cn Tough As Nails quarantine messaging", ctx)
    require_contains(zh_cn, "为 Vampirism 兼容功能预留。本版本中没有实际效果。", "zh_cn Vampirism quarantine messaging", ctx)
    require_contains(zh_cn, "为 ParCool 兼容功能预留。本版本中没有实际效果。", "zh_cn ParCool quarantine messaging", ctx)
    require_contains(zh_cn, "目前没有兼容的 NeoForge 1.21.1 构件", "zh_cn Feathers quarantine messaging", ctx)
    require_contains(zh_cn, "启用此开关也不会在当前版本中恢复 Tough As Nails 支持。", "zh_cn compat restore strategy messaging", ctx)

    if ctx.failures:
        print("Phase 4 compat quarantine verification: RED")
        for note in ctx.notes:
            print(f"- {note}")
        for failure in ctx.failures:
            print(f"- {failure}")
        return 1

    print("Phase 4 compat quarantine verification: GREEN")
    for note in ctx.notes:
        print(f"- {note}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
