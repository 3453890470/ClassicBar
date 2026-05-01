# Classic Bar Unofficial v1.4.0

**Classic Bar Unofficial** is a community-maintained fork of [Tfarcenim's Classic Bar](https://github.com/Tfarcenim/ClassicBar), a mod that replaces the vanilla HUD icon rows (health, hunger, armor, air) with classic-style progress bars. This fork extends the original with new overlay features and expanded mod compatibility.

## Compatibility

| Mod | Feature |
|---|---|
| NeoForge 21.1.228+ (Required) | Base mod |
| [Thirst Was Taken](https://www.curseforge.com/minecraft/mc-mods/thirst-was-taken) | Thirst bar + hydration sub-bar |
| [Vampirism](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) | Vampire blood bar |
| [Farmer's Delight](https://www.curseforge.com/minecraft/mc-mods/farmers-delight) | Nourishment overlay |
| [Kaleidoscope Cookery](https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-cookery) | Satiated Shield overlay |
| [Enigmatic Legacy+](https://www.curseforge.com/minecraft/mc-mods/enigmatic-legacy-plus) | Forbidden Curse hunger bar |

## Differences from the Original Classic Bar

### New Overlays

- **Thirst Bar** — When [Thirst Was Taken](https://www.curseforge.com/minecraft/mc-mods/thirst-was-taken) is installed, a thirst level bar replaces the food bar position. Includes a hydration sub-bar and a drink preview that shows expected thirst restoration when holding drinkable items.
- **Vampirism Blood Bar** — Displays the vampire level bar when [Vampirism](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) is present.
- **Forbidden Curse Hunger Bar** — Renders the forbidden curse hunger bar overlay for [Enigmatic Legacy+](https://www.curseforge.com/minecraft/mc-mods/enigmatic-legacy-plus).
- **Nourishment & Satiated Shield** — Visual effects for [Farmer's Delight](https://www.curseforge.com/minecraft/mc-mods/farmers-delight) and [Kaleidoscope Cookery](https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-cookery) status effects.

### Debug Tools

Eight built-in debug configuration toggles for testing bar rendering effects (Wither, Poison, Frozen, Hunger, Nourishment, Satiated Shield, Vampire level, Forbidden Curse). Only active in a development environment.

### Localization

Full Chinese translation (完整中文本地化) for all added features, maintaining consistent terminology throughout the UI.

### Code Quality

- Preview rendering logic encapsulated for maintainability
- Dependency format unified across the project
- Bug fixes for drink preview display, TWT native HUD hiding, and effect retrieval

## Credits

This mod is a fork of **Classic Bar** by **[Tfarcenim](https://github.com/Tfarcenim)**.

- **GitHub:** [github.com/Tfarcenim/ClassicBar](https://github.com/Tfarcenim/ClassicBar)
- **CurseForge:** [curseforge.com/minecraft/mc-mods/classic-bar](https://www.curseforge.com/minecraft/mc-mods/classic-bar)
- **Modrinth:** [modrinth.com/mod/classic-bar](https://modrinth.com/mod/classic-bar)

## File

- `classicbar-1.21.1-1.4.0.jar` — NeoForge 1.21.1
