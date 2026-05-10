# Classic Bar Unofficial v1.4.0

A **NeoForge 1.21.1** community port of Tfarcenim's Classic Bar. Continuous progress bars replace Minecraft's default icon HUD — hearts, armor, food, air, and more — for a cleaner, more readable display.

---

## What It Does

Classic Bar replaces the traditional icon rows — hearts, armor, food, air, and other status symbols — with **continuous progress bars**. The health bar scales dynamically with your max health instead of capping at 20, giving you a denser, more informative HUD at a glance.

Originally created by Tfarcenim for Minecraft Forge, this fork updates the mod to **NeoForge 1.21.1**, adds new mod integrations, and includes ongoing maintenance and bug fixes.

---

## Why Download

- **Dynamic health scaling** — adjusts to your actual max HP, not a fixed 20
- **Deep customization** — every bar can be positioned, colored, and toggled through an in-game GUI
- **Extended mod support** — five additional mods now have dedicated bar integrations (see below)
- **Better readability** — continuous bars are easier to parse at a glance than scattered icon rows
- **Cleaner look** — reduces visual clutter without sacrificing information

---

## Features

### Native Status Bars (8)

- **Health** — Scales dynamically with max health; poison/wither/frozen overlay effects
- **Armor** — Configurable number of armor layers
- **Food** — Hunger icon variant; food preview when holding edible items
- **Saturation** — Sub-bar below the food bar
- **Air** — Underwater breath indicator with flashing warning
- **Armor Toughness** — Secondary damage reduction stat
- **Mount Health** — Health of horses, striders, llamas, and other mounts
- **Absorption** — Absorption hearts shown as a progress bar

### Mod Compatibility

- [**Thirst Was Taken**](https://www.curseforge.com/minecraft/mc-mods/thirst-was-taken) — thirst bar + hydration sub-bar + drink preview
- [**Vampirism**](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) — blood level bar (replaces the food bar in vampire form)
- [**Farmer's Delight**](https://www.curseforge.com/minecraft/mc-mods/farmers-delight) — golden nourishment overlay on the food bar
- [**Kaleidoscope Cookery**](https://www.curseforge.com/minecraft/mc-mods/kaleidoscope-cookery) — red satiation-compensation overlay on the food bar
- [**Enigmatic Legacy+**](https://www.curseforge.com/minecraft/mc-mods/enigmatic-legacy-plus) — Forbidden Curse hunger overlay (mutually exclusive with blood/food)

### Multi-Effect Icon Stacking

Poison, Wither, and Freezing stack side-by-side on the health bar. Similarly, Hunger, Nourishment, and Satiation-Compensation align themselves on the food bar — no overlapping mess.

### Food & Drink Preview

Hold a food or drink item to see exactly how much hunger, saturation, or thirst it restores, shown as a preview overlay on the bars.

### Custom Pixel Number Fonts

Choose between **3x5** and **3x5_tiny** bitmap fonts for a compact, retro numeric display on the HUD.

### Full Chinese Translation

Complete **zh_CN** localization of the entire UI and configuration menu.

### Debug Toggles (8)

Developer-only switches for testing bar rendering effects — only active in a development environment.

### Code Quality Improvements

This fork brings several improvements over the original Forge version:
- Centralized registration system (ModOverlays, ModIcons, ModConfigs)
- Spotless automated code formatting
- Gradle version catalog for unified dependency management
- Access Transformer infrastructure
- Data generation framework
- Encapsulated preview rendering logic for maintainability
- Bug fixes for drink preview display, TWT native HUD hiding, and effect retrieval

---

## Configuration Highlights

- Every status bar: **LEFT** / **RIGHT** / **HIDDEN**
- Custom color picker for each bar
- Number display modes: **Current value only** / **Current + Max** / **Percentage**
- Per-bar icon toggle
- Per-bar overlay effect toggle
- Full configuration GUI — no manual file editing required

---

## Requirements

- **Minecraft:** 1.21.1
- **NeoForge:** 21.1.228+
- **Side:** Client-side only (no server installation needed)

---

## Credits

- **Original author:** [Tfarcenim](https://github.com/Tfarcenim)
- **Original project:** [Classic Bar](https://www.curseforge.com/minecraft/mc-mods/classic-bar) — [GitHub](https://github.com/Tfarcenim/ClassicBar) · [Modrinth](https://modrinth.com/mod/classic-bar)

## License

**GPL-3.0-only** — This fork is released under the GNU General Public License v3.0. Includes 3x5 bitmap fonts from [Reliable-Recount](https://github.com/evanbones/Reliable-Recount) by catboybinary, also GPL-3.0.

## File

- `classicbar-1.21.1-1.4.0.jar` — NeoForge 1.21.1
