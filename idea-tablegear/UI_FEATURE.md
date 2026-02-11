# TableGen 配置编辑器 UI 功能

## 概述

为 IntelliJ IDEA 插件 `idea-tablegear` 添加了图形化配置编辑器，用于编辑 `tablegen*.properties` 和 `es*.properties` 配置文件。

## 功能特性

- 📝 **可视化编辑**: 通过友好的图形界面编辑配置文件
- 📂 **文件管理**: 支持打开、保存和另存为配置文件
- 🗂️ **分类管理**: 配置项按功能分类，包括：
  - 数据库配置
  - 代码生成配置
  - 表配置
  - 高级配置
- 💾 **配置保存**: 可以将编辑后的配置保存为新的配置文件
- ✅ **配置验证**: 自动处理配置项的格式和类型

## 使用方法

### 打开配置编辑器

1. **通过菜单**: 点击菜单 `Tools` -> `打开 TableGen 配置编辑器`
2. **通过快捷键**: 按 `Ctrl+Alt+T`
3. **通过右键菜单**: 右键点击 `tablegen*.properties` 文件，选择相应操作（如果已实现）

### 编辑配置

1. **打开文件**: 点击对话框顶部的"打开文件..."按钮，选择要编辑的配置文件
2. **编辑配置**: 在各个标签页中编辑相应的配置项：
   - **数据库配置**: 数据库连接信息（驱动、URL、用户名、密码等）
   - **代码生成配置**: 包名、目录、生成选项等
   - **表配置**: 表列表、排除表列表、视图列表
   - **高级配置**: 前缀、字段配置、扩展接口等
3. **保存配置**: 
   - 点击"确定"按钮保存到当前打开的文件
   - 点击"另存为..."按钮保存为新的配置文件

## 文件结构

```
idea-tablegear/src/main/kotlin/com/bixuebihui/tablegear/ui/
├── TableGenConfigModel.kt          # 配置模型类
├── TableGenConfigDialog.kt         # 主配置对话框
├── DatabaseConfigPanel.kt          # 数据库配置面板
├── CodeGenConfigPanel.kt           # 代码生成配置面板
├── TableConfigPanel.kt             # 表配置面板
├── AdvancedConfigPanel.kt          # 高级配置面板
└── OpenTableGenConfigAction.kt     # Action类，用于打开对话框
```

## 技术实现

### 配置模型 (TableGenConfigModel)

- 负责加载和保存 Properties 配置
- 提供类型安全的配置访问方法
- 支持布尔值、字符串、列表等配置类型

### UI 组件

使用 IntelliJ Platform 的 UI 组件：
- `DialogWrapper`: 对话框基类
- `FormBuilder`: 表单构建器
- `JBTabbedPane`: 标签页组件
- `JBTextField`, `JBTextArea`: 文本输入组件
- `JBCheckBox`: 复选框组件

### Action 注册

在 `plugin.xml` 中注册了 Action：
```xml
<action id="TableGear.OpenConfigEditor" 
        class="com.bixuebihui.tablegear.ui.OpenTableGenConfigAction"
        text="打开 TableGen 配置编辑器"
        description="打开 TableGen 配置文件编辑器">
    <add-to-group group-id="ToolsMenu" anchor="last"/>
    <keyboard-shortcut keymap="$default" first-keystroke="ctrl alt T"/>
</action>
```

## 配置项说明

### 数据库配置

- **alias**: 数据源别名，用于Spring的@Qualifier注解
- **className**: JDBC驱动类名，如 `com.mysql.cj.jdbc.Driver`
- **dburl**: 数据库连接URL
- **username/password**: 数据库用户名和密码
- **catalog/schema**: 数据库目录和模式
- **table_owner**: 表的所有者

### 代码生成配置

- **package_name**: 生成代码的根包名
- **src_dir**: 主代码输出目录
- **test_dir**: 测试代码输出目录
- **service_dir**: Service代码输出目录
- **controller_dir**: Controller代码输出目录
- **dto_dir**: DTO代码输出目录
- **cmd_dir**: Command/DTO代码目录

**功能选项**:
- **indexes**: 是否生成索引方法
- **foreign_keys**: 是否生成外键方法
- **r2dbc**: 是否使用R2DBC
- **use_annotation**: 是否使用注解
- **use_swagger**: 是否生成Swagger注解
- **generate_all**: 是否全量生成
- **over_write_all**: 是否覆盖所有文件
- **enable_merge**: 是否启用智能合并

### 表配置

- **table_list**: 要生成的表列表，多个表用逗号分隔，支持正则表达式
- **exclude_table_list**: 要排除的表列表，多个表用逗号分隔
- **view_list**: 视图列表，用分号(;)分隔，格式为 `view_name: select sql`

### 高级配置

- **add_prefix/remove_prefix**: 添加/移除前缀
- **version_col_name**: 版本列名（默认: version）
- **del_col_name**: 删除列名（默认: is_del）
- **encrypted_fields**: 加密字段配置，格式为 `table1:field1,field2;table2:field3`
- **ignore_fields**: 忽略字段配置，格式为 `table1:field1,field2;table2:field3`
- **extra_setting**: 额外配置文件路径（如 pojos.xml）

## 开发说明

### 编译和运行

1. 使用 Gradle 构建项目：
```bash
./gradlew build
```

2. 运行插件：
```bash
./gradlew runIde
```

### 注意事项

- 确保使用 IntelliJ Platform SDK 2022.1 或更高版本
- UI 组件使用 IntelliJ Platform 的标准组件，确保兼容性
- 配置文件使用 UTF-8 编码保存

## 未来改进

- [ ] 添加配置验证和错误提示
- [ ] 支持从现有项目自动检测配置
- [ ] 添加配置模板功能
- [ ] 支持配置导入/导出
- [ ] 添加配置历史记录功能

