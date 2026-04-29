from __future__ import annotations

"""Generate ClassicBar placeholder icon PNGs for TASK-02.

All icons in this file are authored by this repository's generator and are not
copied or extracted from Minecraft, NeoForge, or third-party mods.

The compat-themed outputs (blood/thirst/stamina/feathers) are reserved
placeholder replacement targets only. They keep stable file paths available for
future compat work, but do not mean compat support has been restored.
"""

import struct
import zlib
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
ICON_DIR = ROOT / "src" / "main" / "resources" / "assets" / "classicbar" / "textures" / "gui" / "icons"

PALETTE = {
    ".": (0, 0, 0, 0),
    "R": (220, 70, 90, 255),
    "D": (130, 28, 44, 255),
    "G": (120, 140, 152, 255),
    "S": (80, 96, 112, 255),
    "Y": (224, 190, 64, 255),
    "O": (214, 128, 55, 255),
    "B": (90, 180, 222, 255),
    "C": (44, 118, 170, 255),
    "L": (104, 196, 92, 255),
    "N": (58, 128, 60, 255),
    "P": (168, 112, 220, 255),
    "M": (205, 86, 176, 255),
    "W": (246, 246, 246, 255),
    "K": (42, 48, 58, 255),
}

ICONS = {
    "fallback": [
        "....P....",
        "...PPP...",
        "..PPWPP..",
        ".PPWWWPP.",
        ".PWWWWWP.",
        ".PPWWWPP.",
        "..PPWPP..",
        "...PPP...",
        "....P....",
    ],
    "health": [
        "...RRR...",
        "...RRR...",
        "...RRR...",
        "RRRRRRRRR",
        "RRRRRRRRR",
        "RRRRRRRRR",
        "...RRR...",
        "...RRR...",
        "...RRR...",
    ],
    "armor": [
        "....G....",
        "...GGG...",
        "..GGGGG..",
        ".GGGGGGG.",
        ".GGGGGGG.",
        ".GGGGGGG.",
        "..GGGGG..",
        "..GGSGG..",
        "...SSS...",
    ],
    "food": [
        "....O....",
        "...OOO...",
        "..OOOOO..",
        ".OOYYYYO.",
        ".OYYYYYY.",
        ".OYYYYYY.",
        "..OYYYY..",
        "...LNL...",
        "....N....",
    ],
    "air": [
        "...BBB...",
        "..B...B..",
        ".B.....B.",
        ".B..BB.B.",
        ".B.B..BB.",
        ".B.....B.",
        "..B...B..",
        "...BBB...",
        ".........",
    ],
    "absorption": [
        "....Y....",
        "Y...Y...Y",
        ".Y..Y..Y.",
        "..YYYYY..",
        "YYYYYYYYY",
        "..YYYYY..",
        ".Y..Y..Y.",
        "Y...Y...Y",
        "....Y....",
    ],
    "armor_toughness": [
        "....S....",
        "...SSS...",
        "..SSGSS..",
        ".SSGGGSS.",
        ".SGGGGGS.",
        ".SSGGGSS.",
        "..SSGSS..",
        "...SSS...",
        "....S....",
    ],
    "mount_health": [
        ".........",
        ".M.....M.",
        ".MM...MM.",
        ".M.M.M.M.",
        ".M..M..M.",
        ".M.....M.",
        ".MM...MM.",
        "..MMMMM..",
        "...MMM...",
    ],
    # Reserved compat placeholders only; not proof of restored compat support.
    "blood": [
        "....D....",
        "...DDD...",
        "..DDDDD..",
        "..DDDDD..",
        "...DDD...",
        "...DDD...",
        "...DDD...",
        "....D....",
        ".........",
    ],
    "thirst": [
        "....B....",
        "...BBB...",
        "..BBBBB..",
        "..BBBBB..",
        "...BBB...",
        "...BBB...",
        "...BBB...",
        "....B....",
        ".........",
    ],
    "stamina": [
        "....L....",
        "...LL....",
        "..LL.....",
        ".LLLLLL..",
        "....LLL..",
        "...LLL...",
        "..LLL....",
        ".LLL.....",
        ".N.......",
    ],
    "feathers": [
        "......W..",
        ".....WWW.",
        "....WWWW.",
        "...WWWW..",
        "..WWWWW..",
        ".WWWWW...",
        ".WWW.....",
        ".WW......",
        "..W......",
    ],
}


def png_chunk(tag: bytes, data: bytes) -> bytes:
    return (
        struct.pack(">I", len(data))
        + tag
        + data
        + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
    )


def write_png(path: Path, rows: list[str]) -> None:
    width = len(rows[0])
    height = len(rows)
    raw = bytearray()
    for row in rows:
        raw.append(0)
        for cell in row:
            raw.extend(PALETTE[cell])

    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    png = b"\x89PNG\r\n\x1a\n"
    png += png_chunk(b"IHDR", ihdr)
    png += png_chunk(b"IDAT", zlib.compress(bytes(raw), level=9))
    png += png_chunk(b"IEND", b"")
    path.write_bytes(png)


def main() -> int:
    ICON_DIR.mkdir(parents=True, exist_ok=True)
    for name, rows in ICONS.items():
        if any(len(row) != len(rows[0]) for row in rows):
            raise ValueError(f"Icon {name} has inconsistent row widths")
        write_png(ICON_DIR / f"{name}.png", rows)
    print(f"Generated {len(ICONS)} ClassicBar icon PNG files in {ICON_DIR}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
