# ClassicBar 健康值状态栏变种纹理补充计划

## 元数据

| 字段 | 值 |
|---|---|
| plan_id | health_variant_textures |
| workflow_mode | standard |
| repo_path | E:\GitHub\ClassicBar |
| plan_path | E:\GitHub\ClassicBar\plans\plan_health_variant_textures.md |
| parent_plan | plan_classicbar_neoforge_1_21_1_migration.md |
| parent_task_ref | 建议作为 TASK-05（收口）的前置或组成部分 |
| status | 已完成 |
| next_hop | 计划审查 |

## 目标

为 ClassicBar 模组的健康值（Health）状态栏补充中毒（POISON）、凋零（WITHER）、冻结（FROZEN）三种状态下的变种图标纹理引用和颜色覆盖层渲染，使状态切换时 HUD 图标和状态栏颜色覆盖层均有对应视觉表现。

## 问题陈述（用户验证发现）

| # | 缺失项 | 状态 |
|---|---|---|
| 1 | 生命值图标的变种纹理（目前仅 `health.png`，无中毒/凋零/冻结变种） | 无 → 需补充 |
| 2 | 生命值状态栏颜色覆盖层（POISON 已有半透明绿，WITHER 和 FROZEN 无任何覆盖层） | 部分 → 需补全 |
| 3 | 原版参考路径 `.../hud/heart/` 有 47 个纹理文件，按状态分层 | 仅作参考，不照搬 |
| 4 | 部分可能多状态的 icon 没有状态切换的额外纹理 | 本次仅覆盖 health |

## 范围

### 做

1. **扩充 `BarIcons.java`** —— 添加 3 个新的 `ResourceLocation` 常量：
   - `HEALTH_POISON` → `textures/gui/icons/health_poison.png`
   - `HEALTH_WITHER` → `textures/gui/icons/health_wither.png`
   - `HEALTH_FROZEN` → `textures/gui/icons/health_frozen.png`
   - 保持 `HEALTH` 指向 `health.png` 不变（正常状态）

2. **修改 `Health.java` — `renderIcon()`** —— 根据玩家当前 HealthEffect 状态选择对应图标纹理：
   - 获取 `getHealthEffect(player)` 返回值
   - `NONE` → 使用 `BarIcons.HEALTH`（现有行为，不变）
   - `POISON` → 临时切换 `ModUtils.CURRENT_TEXTURE` 为 `BarIcons.HEALTH_POISON`，绘制后恢复
   - `WITHER` → 临时切换 `ModUtils.CURRENT_TEXTURE` 为 `BarIcons.HEALTH_WITHER`，绘制后恢复
   - `FROZEN` → 临时切换 `ModUtils.CURRENT_TEXTURE` 为 `BarIcons.HEALTH_FROZEN`，绘制后恢复

3. **修改 `Health.java` — `renderBar()`** —— 补充 WITHER 和 FROZEN 的颜色覆盖层：
   - 现有 POISON 覆盖层（绿色 `setShaderColor(0, .5f, 0, .5f)`）保持不变
   - **WITHER**：新增 `else if` 分支，使用灰色/暗色调覆盖层，如 `setShaderColor(.35f, .35f, .35f, .5f)`（精确值需实测调整）
   - **FROZEN**：新增 `else if` 分支，使用蓝色调覆盖层，如 `setShaderColor(.3f, .5f, 1f, .5f)`（精确值需实测调整）
   - 覆盖层绘制逻辑沿用现有模式：`ModUtils.drawTexturedModalRect(graphics, f + 1, yStart + 1, 1, 36, barWidth, 7)`

4. **检查 `ModUtils.java` — `drawStandaloneIcon()`** —— 当前签名为 `(GuiGraphics, int, int, int)`，固定使用 `CURRENT_TEXTURE`，不支持传入外部 `ResourceLocation`。
   - **方案 A（推荐）**：不修改 `drawStandaloneIcon`，在 `renderIcon()` 中临时修改 `ModUtils.CURRENT_TEXTURE` 再恢复。最小改动、最低风险。
   - **方案 B（备选）**：为 `drawStandaloneIcon` 增加重载 `drawStandaloneIcon(GuiGraphics, int, int, int, ResourceLocation)`，新重载使用传入的纹理而非 `CURRENT_TEXTURE`。改造成本略高但接口更清晰。
   - 最终选择在实施时确定，以最小改动为优先。

5. **纹理命名规范约定** —— 定义用户准备素材时应遵循的规则：
   - 纹理文件放入 `src/main/resources/assets/classicbar/textures/gui/icons/`
   - 命名规则：`health_{状态}.png`
     - `health_poison.png` — 中毒
     - `health_wither.png` — 凋零
     - `health_frozen.png` — 冻结
   - 尺寸建议：与现有 `health.png` 一致的尺寸（原 `health.png` 为 142 bytes，推测为 9×9 像素小图标）
   - 格式：PNG 带透明度（argb）

### 不做

- ❌ 不生成/创建任何 PNG 纹理文件（用户自行准备）
- ❌ 不修改除 health 之外的其他 overlay（armor/food/air 等的多状态扩展不在本次范围内）
- ❌ 不改变现有颜色渐变逻辑（`ColorUtils.calculateScaledColor` 已正确工作，覆盖层仅叠加在其之上）
- ❌ 不涉及硬核模式（hardcore）纹理变种
- ❌ 不涉及吸收（absorption）纹理变种
- ❌ 不改写 `BarIcons.forOverlay()` switch 表达式（新增常量不要求进入该路由）
- ❌ 不修改 `renderText()`（文本颜色已由 `getPrimaryBarColor` + `calculateScaledColor` 正确处理）

> **变种 icon 常量说明**：`HEALTH_POISON`/`HEALTH_WITHER`/`HEALTH_FROZEN` 不在现有 `verify_independent_icons.py` 的 `ACTIVE_OVERLAYS` 检查范围内，属于预期行为——这些是运行时按状态切换的变种 icon，不需要 always-present 守卫。

## 执行顺序

```
Step 1: 扩充 BarIcons.java
  └─ 添加 HEALTH_POISON, HEALTH_WITHER, HEALTH_FROZEN 常量
  └─ 验证：编译通过

Step 2: 检查并确定 ModUtils.drawStandaloneIcon 策略
  └─ 决定采用方案 A（临时切换 CURRENT_TEXTURE）或方案 B（新增重载）
  └─ 实施对应改动（若有）
  └─ 验证：编译通过

Step 3: 修改 Health.java.renderIcon() 实现状态感知图标切换
  └─ 引入 getHealthEffect(player) 判断
  └─ 根据状态切换 CURRENT_TEXTURE → 绘制 → 恢复
  └─ 验证：编译通过
  └─ try/finally 保存-恢复模板参考：

    ```java
    ResourceLocation saved = ModUtils.CURRENT_TEXTURE;
    try {
        ModUtils.CURRENT_TEXTURE = (effect == POISON) ? BarIcons.HEALTH_POISON :
                                   (effect == WITHER) ? BarIcons.HEALTH_WITHER :
                                   (effect == FROZEN) ? BarIcons.HEALTH_FROZEN :
                                   saved;  // NONE 使用已有的配置值
        ModUtils.drawStandaloneIcon(graphics, xStart, yStart, 9);
    } finally {
        ModUtils.CURRENT_TEXTURE = saved;
    }
    ```

Step 4: 修改 Health.java.renderBar() 补充 WITHER / FROZEN 覆盖层
  └─ WITHER：新增 `else if` 分支，使用灰色/暗色调覆盖层（不破坏现有 POISON 分支）
    └─ 注意：覆盖层分支绘制后应调用 `Color.reset()` 重置 shader color，避免后续维护者在覆盖层后误加绘制逻辑导致颜色泄漏
  └─ FROZEN：新增 `else if` 分支，使用蓝色调覆盖层（不破坏现有 POISON 分支）
    └─ 注意：覆盖层分支绘制后应调用 `Color.reset()` 重置 shader color，避免后续维护者在覆盖层后误加绘制逻辑导致颜色泄漏
  └─ 验证：编译通过

Step 5: 编译验证
  └─ gradlew.bat clean compileJava

Step 6: 运行测试
  └─ gradlew.bat test
  └─ 特别复验 `IndependentIconResourcesTest` 确保修改后的 renderIcon() 仍满足其对方法体的约束检查

Step 7: 用户放置纹理文件后可选游戏内验证
  └─ gradlew.bat runClient（非强制，但推荐）
```

## DoD（完成标准）

- [ ] `BarIcons.java` 新增 3 个资源常量：`HEALTH_POISON`、`HEALTH_WITHER`、`HEALTH_FROZEN`
- [ ] `Health.java.renderIcon()` 根据 `getHealthEffect(player)` 渲染对应变种图标或保持默认
- [ ] `Health.java.renderIcon()` 中 `ModUtils.CURRENT_TEXTURE` 在变种图标绘制后恢复为原始值（使用 try/finally 保证）
- [ ] 现有 POISON 覆盖层在 renderBar() 中保持不变（`else if` 链不改变已有分支逻辑）
- [ ] `Health.java.renderBar()` WITHER 状态下渲染灰色/暗色覆盖层
- [ ] `Health.java.renderBar()` FROZEN 状态下渲染蓝色覆盖层
- [ ] `gradlew.bat clean compileJava` 编译通过
- [ ] `gradlew.bat test` 全部通过
- [ ] 纹理文件命名规范已明确定义，用户可按规范准备素材

## 风险与回退

| 风险 | 影响 | 缓解/回退 |
|---|---|---|
| `ModUtils.drawStandaloneIcon` 签名不支持传参，需评估改造方案 | 需额外改动 ModUtils | 优先选方案 A（临时改 CURRENT_TEXTURE），最小影响；不改 ModUtils 接口 |
| 纹理文件由用户准备，尚不存在于资源目录 | 编译通过但游戏内显示 missing texture（紫黑块） | 代码逻辑先完成并编译通过；用户放置文件前显示 missing texture 属预期行为，不做特殊降级处理 |
| 覆盖层 `setShaderColor` 的颜色值需要实测调优 | 视觉可能不理想 | 先使用合理估算值实现，标注 `// TODO: tune color`，用户可自行调整 |
| `renderIcon` 中临时修改 `ModUtils.CURRENT_TEXTURE` 后未恢复 | 后续 icon 渲染使用错误的纹理 | 使用 try/finally 保证恢复；恢复值为 `BarIcons.HEALTH` 或 `bindIconTexture()` 绑定的原始值 |
| 与现有 TASK-01~TASK-04 的改动冲突 | 合并时可能产生冲突 | 此计划基于当前 `migration/neoforge-1.21.1` HEAD 编写，合入前需确认无冲突 |
| `ModUtils.CURRENT_TEXTURE` 硬编码恢复为 `BarIcons.HEALTH` | 当用户通过 config 自定义了 health icon 时，硬编码恢复会覆盖用户配置值 | 使用保存-恢复模式：保存 `bindIconTexture()` 设置的原始 `CURRENT_TEXTURE`，绘制变种后恢复原始值 |

## 回退路径

若变种方案出现问题，回退策略：
1. **简单回退**：移除新增的 `else if` 分支和图标切换逻辑，仅保留 WITHER/FROZEN 覆盖层（步骤 4）
2. **完全回退**：恢复 `BarIcons.java`、`Health.java`、`ModUtils.java` 到改动前状态
3. **不影响其他 overlay**：所有改动局限在 health 相关文件，不影响 armor/food 等其他状态栏

## 与现有计划的关系

此任务作为 `plan_classicbar_neoforge_1_21_1_migration.md` 的补充垂直任务，建议安排在 **TASK-05（收口验证）之前** 执行，或作为 **TASK-05 的前置子任务**。

原因：
- 当前 TASK-01~TASK-04 均已完成/GREEN
- TASK-05 涉及收口验证、README 更新和最终构建
- 纹理变种是功能补齐，理应在收口前完成
- 若 TASK-05 已开始执行，建议暂停 TASK-05，先完成本计划内容，再回到 TASK-05

建议在父计划 `plan_classicbar_neoforge_1_21_1_migration.md` 的 TASK-04 与 TASK-05 之间注册本计划为前置任务（TASK-04.5），避免遗漏。

## 参考文件清单

| 文件 | 操作 | 说明 |
|---|---|---|
| `src/main/java/tfar/classicbar/resources/BarIcons.java` | 编辑 | 新增 3 个 ResourceLocation 常量 |
| `src/main/java/tfar/classicbar/impl/overlays/vanilla/Health.java` | 编辑 | `renderIcon()` 状态感知切换 + `renderBar()` 覆盖层补充 |
| `src/main/java/tfar/classicbar/util/ModUtils.java` | 可能编辑 | 如需扩展 `drawStandaloneIcon` |
| `src/main/java/tfar/classicbar/util/HealthEffect.java` | 不修改（仅参考） | 枚举定义，`i` 为 sprite sheet Y 偏移 |
| `src/main/java/tfar/classicbar/impl/BarOverlayImpl.java` | 不修改（仅参考） | `getHealthEffect()` 逻辑在此 |
| `src/main/resources/assets/classicbar/textures/gui/icons/health.png` | 参考（现有） | 现有正常状态图标，确定尺寸格式 |
| `src/main/resources/assets/classicbar/textures/gui/icons/health_poison.png` | 用户新建 | 中毒变种图标 |
| `src/main/resources/assets/classicbar/textures/gui/icons/health_wither.png` | 用户新建 | 凋零变种图标 |
| `src/main/resources/assets/classicbar/textures/gui/icons/health_frozen.png` | 用户新建 | 冻结变种图标 |

## 执行记录

- **执行日期**：2026-04-30
- **执行人**：猫娘编写官-米娅
- **审查人**：猫娘审查官-艾琳
- **编译结果**：✅ BUILD SUCCESSFUL（gradlew.bat clean compileJava）
- **测试结果**：✅ 41/41 tests passed（gradlew.bat test）
- **修改文件**：
  - `BarIcons.java` — +3 常量（HEALTH_POISON, HEALTH_WITHER, HEALTH_FROZEN）
  - `Health.java` — renderIcon() 状态感知切换 + renderBar() WITHER/FROZEN 覆盖层
  - `IndependentIconResourcesTest.java` — extractMethodBody 修复嵌套大括号支持
- **未完成项**：纹理文件（health_poison.png, health_wither.png, health_frozen.png）需用户自行放入 `src/main/resources/assets/classicbar/textures/gui/icons/`
- **残留风险**：覆盖层颜色值（WITHER 灰色、FROZEN 蓝色）标注了 `// TODO: tune color`，需用户实测调优
