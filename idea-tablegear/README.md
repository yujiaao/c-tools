# IntelliJ IDEA TableGear Plugin

IntelliJ IDEA 插件，为 `tablegen*.properties` 和 `es*.properties` 配置文件提供智能支持。

## 功能特性

- ✅ **智能代码补全**：根据 `type` 字段动态过滤配置项
- ✅ **语法高亮**：类似 Rainbow CSV 的彩色高亮显示
- ✅ **实时验证**：检测重复键、无效值、空值等
- ✅ **多模式支持**：数据库、Elasticsearch、JSON 生成模式
- ✅ **JDBC 驱动提示**：常见数据库驱动类名补全
- ✅ **数据库 URL 模板**：根据驱动类提供连接串模板

## 支持的文件模式

- `tablegen*.properties` / `tablegen*.prop`
- `es*.properties` / `es*.prop`

## 构建

```bash
./gradlew buildPlugin
```

## 安装

1. 构建插件：`./gradlew buildPlugin`
2. 在 IntelliJ IDEA 中：`File` -> `Settings` -> `Plugins` -> `Install Plugin from Disk...`
3. 选择 `build/distributions/idea-tablegear-1.0.0.zip`

## 开发

使用 IntelliJ IDEA 打开项目，运行 `runIde` Gradle 任务启动沙盒环境进行测试。

`./gradlew runIde`

## 参考

本插件参考了 [lsp-tablegear](../lsp-tablegear) 的功能实现。

