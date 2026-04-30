# ClassicBar

将 HUD 中传统的图标行替换为进度条。血量条会随最大生命值动态缩放，不再像原版那样固定显示。

**平台：NeoForge 1.21.1**（已从 Forge 迁移）

纯客户端模组，无需服务端安装。

---

## 支持的原版条

- Health（生命值）
- Armor（护甲值）
- Food（饱食度）
- Saturation（饱和度）
- Air（氧气值）
- Armor Toughness（护甲韧性）
- Mount Health（坐骑生命值）
- Absorption（吸收值）

---

## 配置选项

- 禁用图标
- 绘制完整吸收条
- 更改护甲条层数
- 禁用护甲韧性条
- 禁用数字显示
- 指定动态血量条颜色与范围
- 更改各条颜色

计划中的配置选项：单独禁用每一条。

---

## 构建

```bash
./gradlew build
```

构建产物位于 `build/libs/`。

---

## Credits

原作者 [Tfarecnim](https://github.com/Tfarecnim) — 原始 Forge 版本的创建者。

---

## 许可证

[Unlicense](https://unlicense.org/) — 公有领域，自由使用。
