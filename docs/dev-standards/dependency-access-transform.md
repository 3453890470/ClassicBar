# 对依赖的 Mod 应用 Access Transformer

## 背景

在 NeoForge 模组开发中，当你需要用 Mixin 访问某个**第三方模组依赖**的私有/保护成员时，通常的做法是用 `@Accessor` + `@Reflect` 走反射。这样代码繁琐且有性能开销。

**Access Transformer (AT)** 可以在编译期直接修改目标 jar 的字节码访问权限，让你像访问自己代码一样访问第三方模组的私有成员，无需反射。

## 为什么可以在 NeoForge 项目中使用 Forge 的 AT 插件

- `net.minecraftforge.accesstransformers` 插件是一个**纯 Gradle 层面的 Artifact Transform**
- 它作用于 Gradle 的依赖解析阶段，在字节码层面修改访问修饰符
- 不依赖 Forge 运行时，NeoForge 运行时对访问修饰符也没有额外校验
- 因此完全可以在 NeoForge 项目中使用

## 配置步骤

### 1. settings.gradle 添加仓库

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        // ... 其他仓库
    }
}
```

### 2. build.gradle 声明插件

```groovy
plugins {
    id 'net.minecraftforge.accesstransformers' version '5.0.3'
}
```

### 3. 创建 AT 配置文件

在 `src/main/resources/META-INF/accesstransformer.cfg` 中编写规则。

### 4. 在 dependencies 中声明对哪个依赖应用 AT

```groovy
dependencies {
    implementation('com.example:some-mod:1.0.0') {
        accessTransformers.configure(it) {
            config = project.file('accesstransformer.cfg')
        }
    }
}
```

## AT 语法参考

| 修饰符 | 效果 |
|---------|------|
| `public` | 将目标改为 public |
| `protected` | 将目标改为 protected |
| `private` | 将目标改为 private |
| 无修饰符 | 改为包级私有 (default) |

格式：`完整类名 成员名 访问级别`

示例：
```
net.minecraft.world.item.Item  maxStackSize  public
com.somemod.SomeClass  secretMethod()  public
```

## 注意事项

1. **只修改字节码，不修改源码**——IDE 看到的源代码可能还是原本的访问级别，不要因此困惑，只要编译通过即可运行
2. **仅开发环境生效**——这个插件只在 Gradle 构建的编译阶段修改依赖 jar；生产环境（玩家运行）需要自行确保目标 mod 的 jar 也打过 AT，或走 Mixin 反射兜底
3. **需要 Gradle 9+**——该插件 v5.x 依赖 Gradle 9+ 的 Artifact Transform API
4. **和 NeoForge 自己的 Access Widener 不冲突**——AW 用于你对自己的 mod 做 AT，这个插件用于对别人的依赖做 AT

## 参考

- [CoremodTips - Access Transform for Mod](https://github.com/vfyjxf/CoremodTips/blob/master/AccessTransformForMod.md)
- [Gradle Plugin Portal - net.minecraftforge.accesstransformers](https://plugins.gradle.org/plugin/net.minecraftforge.accesstransformers)
