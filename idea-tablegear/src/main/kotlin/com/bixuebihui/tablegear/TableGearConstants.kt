package com.bixuebihui.tablegear

/**
 * 配置项定义
 */
data class PropertyDefinition(
    val key: String,
    val description: String,
    val valueType: ValueType,
    val enumValues: List<String>? = null,
    val mode: List<ConfigMode>? = null
)

enum class ValueType {
    STRING, BOOLEAN, ENUM, LIST, PATH, SQL
}

enum class ConfigMode {
    DATABASE, ELASTICSEARCH, JSON, COMMON
}

/**
 * 所有配置项定义
 */
object TableGearProperties {
    val ALL_PROPERTIES = listOf(
        // 通用配置项
        PropertyDefinition("alias", "数据源或项目别名，会被当做 Spring Bean qualifier, 用于区分不同的数据库，以及主从读写分离的不同数据源", ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("package_name", "生成代码的根包名", ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("src_dir", "主代码输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("test_dir", "测试代码输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("resource_dir", "资源文件输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("service_dir", "Service 代码输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("controller_dir", "Controller 代码输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("dto_dir", "DTO 代码输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("cmd_dir", "Command/DTO 代码目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("extra_setting", "额外配置文件路径（如 pojos.xml）", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("generate_all", "是否全量生成，no 时进入增量模式", ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("over_write_all", "是否覆盖现有文件", ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("use_annotation", "是否启用 Jakarta Validation/Swagger 注解", ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("use_swagger", "Controller 是否生成 Swagger 注解", ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("swagger_version", "Swagger/OpenAPI 版本", ValueType.ENUM, enumValues = listOf("2", "3"), mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("ignore_fields", "生成时忽略的字段，格式 table:field1,field2 或 field1,field2", ValueType.LIST, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("collection_fields", "需要生成集合类型的字段", ValueType.LIST, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("add_prefix", "生成类名、文件名前统一追加的前缀", ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("remove_prefix", "解析表名时需要去掉的前缀", ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        
        // 数据库模式专用
        PropertyDefinition("projectname", "生成项目名称，用于生成代码中的展示", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("className", "JDBC 驱动类，例如 com.mysql.cj.jdbc.Driver", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("dburl", "数据库 JDBC URL", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("username", "数据库账号", ValueType.STRING, mode = listOf(ConfigMode.DATABASE, ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("password", "数据库密码", ValueType.STRING, mode = listOf(ConfigMode.DATABASE, ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("schema", "数据库 schema / catalog，用于过滤表", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("table_owner", "表所属用户（Oracle/DB2 等）", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("catalog", "JDBC catalog，用于限定元信息", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("read_write_separate", "是否按 alias 生成读写分离注入", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("keys", "是否生成主键信息", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("indexes", "是否生成索引相关方法", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("types_are_strings", "是否将所有字段解析为字符串", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("use_custom_meta_table", "是否使用自定义元信息表", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("table_list", "参与生成的表，可为逗号或正则", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("exclude_table_list", "排除的表，优先级高于 table_list", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("view_list", "虚拟视图定义，支持 “view: select ...;” 或 “view: @relative/path.sql;” 两种写法（@ 表示引用外部 SQL 文件）", ValueType.SQL, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("r2dbc", "是否生成 R2DBC 代码", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("generate_baselist", "是否生成 BaseList / 列表基础类", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("use_autoincrement", "是否使用数据库自增，不再生成主键插入", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("foreign_keys", "是否读取外键信息", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("kuozhanbiao", "是否启用扩展表生成", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("modifydate_col", "修改时间列名", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_modifydate_interface", "实现修改时间接口的全限定名", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("encrypted_fields", "需要加解密的字段，格式 table1:field1,field2;table2:field3,field4;", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_node_interface", "POJO 需要实现的接口", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_node_interface_list", "按表控制实现接口的清单", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_version_interface", "POJO 版本接口全限定名", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_version_interface_list", "按表控制实现版本接口的清单", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_state_interface", "POJO 状态接口全限定名", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_state_interface_list", "按表控制实现状态接口的清单", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_uuid_interface", "POJO UUID 接口全限定名", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_uuid_interface_list", "按表控制实现 UUID 接口的清单", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_modifydate_interface_list", "按表控制实现修改时间接口的清单", ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("del_col_name", "逻辑删除列名，默认 is_del", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("version_col_name", "版本列名，默认 version", ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("jsp_dir", "JSP 文件输出目录", ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("shared_model_group", "共享模型组名", ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("generate_procedures", "是否生成存储过程相关代码", ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        
        // Elasticsearch/JSON 模式专用
        PropertyDefinition("type", "生成模式：database（默认，从数据库生成）、elasticsearch_mapping（从 ES mapping 生成）、elasticsearch_sample（从 ES 样本生成）、json（从 JSON 文件生成）", ValueType.ENUM, enumValues = listOf("database", "elasticsearch_mapping", "elasticsearch_sample", "json"), mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("url", "ElasticSearch 访问地址（如 http://localhost:9200）或 JSON 文件路径", ValueType.STRING, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("index_list", "ElasticSearch 索引列表，格式 indexName:aliasName 或 indexName", ValueType.LIST, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("primary_key_fields", "ES 主键字段（映射到 _id），多个用逗号分隔", ValueType.LIST, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON))
    )
    
    val PROPERTY_MAP = ALL_PROPERTIES.associateBy { it.key }
    
    fun getPropertiesForMode(mode: ConfigMode): List<PropertyDefinition> {
        return ALL_PROPERTIES.filter { prop ->
            prop.mode == null || prop.mode.contains(mode) || prop.mode.contains(ConfigMode.COMMON)
        }
    }
    
    fun getConfigMode(typeValue: String?): ConfigMode {
        if (typeValue == null) return ConfigMode.DATABASE
        val typeLower = typeValue.lowercase()
        return when {
            typeLower == "json" -> ConfigMode.JSON
            typeLower.contains("elasticsearch") -> ConfigMode.ELASTICSEARCH
            else -> ConfigMode.DATABASE
        }
    }
}

