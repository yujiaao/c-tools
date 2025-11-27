# 构建优化说明

## 为什么首次构建很慢？

`./gradlew buildPlugin` 首次执行慢的主要原因：

1. **下载 Gradle Wrapper**（~100MB）
   - 首次运行会下载 Gradle 8.4
   - 下载位置：`~/.gradle/wrapper/dists/`

2. **下载 IntelliJ IDEA SDK**（~500MB+）
   - 需要下载 IntelliJ IDEA Community Edition 2023.2
   - 下载位置：`~/.gradle/caches/modules-2/files-2.1/com.jetbrains.intellij.idea/ideaIC/`
   - 这是最耗时的步骤

3. **下载依赖库**
   - Kotlin 标准库
   - IntelliJ Platform API

4. **编译和打包**
   - Kotlin 代码编译
   - 插件资源打包

## 优化建议

### 1. 使用本地 IDEA（推荐）

如果本地已安装 IntelliJ IDEA 2023.2，可以在 `build.gradle.kts` 中指定本地路径：

```kotlin
intellij {
    localPath.set("C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2023.2")
    // 或者
    // localPath.set("/Applications/IntelliJ IDEA CE.app/Contents")
}
```

这样可以跳过 SDK 下载，大幅加速首次构建。

### 2. 使用 Gradle 守护进程

已启用 `org.gradle.daemon=true`，Gradle 守护进程会缓存构建信息，后续构建会更快。

### 3. 启用构建缓存

已启用 `org.gradle.caching=true`，Gradle 会缓存构建产物。

### 4. 并行构建

已启用 `org.gradle.parallel=true`，多模块项目会并行构建。

### 5. 使用国内镜像（可选）

如果需要，可以取消注释 `build.gradle.kts` 中的阿里云镜像：

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://maven.aliyun.com/repository/public") }
}
```

## 构建时间参考

- **首次构建**：5-15 分钟（取决于网络速度）
- **后续构建**：10-30 秒（如果代码未改动）
- **增量构建**：5-10 秒

## 检查构建状态

```bash
# 查看 Gradle 下载进度
./gradlew buildPlugin --info

# 查看详细日志
./gradlew buildPlugin --debug
```

## 常见问题

**Q: 构建卡在 "Downloading IDEA" 怎么办？**
A: 检查网络连接，或使用本地 IDEA 路径。

**Q: 如何清理缓存重新下载？**
A: 删除 `~/.gradle/caches/` 目录（Windows: `%USERPROFILE%\.gradle\caches\`）

**Q: 构建失败怎么办？**
A: 检查 Java 版本（需要 JDK 17+），查看错误日志。

