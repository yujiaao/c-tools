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
    private fun desc(key: String): String = TableGearBundle.message("prop.$key.desc")

    val ALL_PROPERTIES = listOf(
        // 通用配置项
        PropertyDefinition("alias", desc("alias"), ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("package_name", desc("package_name"), ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("src_dir", desc("src_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("test_dir", desc("test_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("resource_dir", desc("resource_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("service_dir", desc("service_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("controller_dir", desc("controller_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("dto_dir", desc("dto_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("cmd_dir", desc("cmd_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("extra_setting", desc("extra_setting"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("generate_all", desc("generate_all"), ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("over_write_all", desc("over_write_all"), ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("enable_merge", desc("enable_merge"), ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("use_annotation", desc("use_annotation"), ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("use_swagger", desc("use_swagger"), ValueType.BOOLEAN, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("api_doc_provider", desc("api_doc_provider"), ValueType.ENUM, enumValues = listOf("swagger", "springdoc"), mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("swagger_version", desc("swagger_version"), ValueType.ENUM, enumValues = listOf("2", "3"), mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("ignore_fields", desc("ignore_fields"), ValueType.LIST, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("collection_fields", desc("collection_fields"), ValueType.LIST, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("add_prefix", desc("add_prefix"), ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("remove_prefix", desc("remove_prefix"), ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        
        // 数据库模式专用
        PropertyDefinition("projectname", desc("projectname"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("className", desc("className"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("dburl", desc("dburl"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("username", desc("username"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE, ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("password", desc("password"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE, ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("schema", desc("schema"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("table_owner", desc("table_owner"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("catalog", desc("catalog"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("read_write_separate", desc("read_write_separate"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("keys", desc("keys"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("indexes", desc("indexes"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("types_are_strings", desc("types_are_strings"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("use_custom_meta_table", desc("use_custom_meta_table"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("table_list", desc("table_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("exclude_table_list", desc("exclude_table_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("view_list", desc("view_list"), ValueType.SQL, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("r2dbc", desc("r2dbc"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("generate_baselist", desc("generate_baselist"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("use_autoincrement", desc("use_autoincrement"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("foreign_keys", desc("foreign_keys"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("kuozhanbiao", desc("kuozhanbiao"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("modifydate_col", desc("modifydate_col"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_modifydate_interface", desc("pojo_modifydate_interface"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("encrypted_fields", desc("encrypted_fields"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_node_interface", desc("pojo_node_interface"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_node_interface_list", desc("pojo_node_interface_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_version_interface", desc("pojo_version_interface"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_version_interface_list", desc("pojo_version_interface_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_state_interface", desc("pojo_state_interface"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_state_interface_list", desc("pojo_state_interface_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_uuid_interface", desc("pojo_uuid_interface"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_uuid_interface_list", desc("pojo_uuid_interface_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("pojo_modifydate_interface_list", desc("pojo_modifydate_interface_list"), ValueType.LIST, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("del_col_name", desc("del_col_name"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("version_col_name", desc("version_col_name"), ValueType.STRING, mode = listOf(ConfigMode.DATABASE)),
        PropertyDefinition("jsp_dir", desc("jsp_dir"), ValueType.PATH, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("shared_model_group", desc("shared_model_group"), ValueType.STRING, mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("generate_procedures", desc("generate_procedures"), ValueType.BOOLEAN, mode = listOf(ConfigMode.DATABASE)),
        
        // Elasticsearch/JSON 模式专用
        PropertyDefinition("type", desc("type"), ValueType.ENUM, enumValues = listOf("database", "elasticsearch_mapping", "elasticsearch_sample", "json"), mode = listOf(ConfigMode.COMMON)),
        PropertyDefinition("url", desc("url"), ValueType.STRING, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("index_list", desc("index_list"), ValueType.LIST, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON)),
        PropertyDefinition("primary_key_fields", desc("primary_key_fields"), ValueType.LIST, mode = listOf(ConfigMode.ELASTICSEARCH, ConfigMode.JSON))
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

