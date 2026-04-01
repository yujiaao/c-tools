package com.bixuebihui.tablegear.completion

import com.bixuebihui.tablegear.ConfigMode
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegear.ValueType
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class TableGearCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val file = parameters.originalFile
                    val document = file.viewProvider.document ?: return
                    
                    // 获取当前模式
                    val typeValue = extractPropertyValue(document.text, "type")
                    val mode = TableGearProperties.getConfigMode(typeValue)
                    
                    // 获取当前位置的行
                    val offset = parameters.offset
                    val lineNumber = document.getLineNumber(offset)
                    val lineStart = document.getLineStartOffset(lineNumber)
                    val lineText = document.getText().substring(lineStart, document.getLineEndOffset(lineNumber))
                    
                    val equalsIndex = lineText.indexOf('=')
                    if (equalsIndex == -1 || offset - lineStart <= equalsIndex) {
                        // 补全配置键
                        TableGearProperties.getPropertiesForMode(mode).forEach { prop ->
                            result.addElement(
                                LookupElementBuilder.create(prop.key)
                                    .withTypeText(prop.valueType.name.lowercase())
                                    .withTailText(" = ")
                                    .withInsertHandler { context, _ ->
                                        val tailOffset = context.tailOffset
                                        context.document.insertString(tailOffset, "=")
                                        context.editor.caretModel.moveToOffset(tailOffset + 1)
                                    }
                            )
                        }
                    } else {
                        // 等号后：补全 value，并给出格式/示例提示
                        val key = lineText.substring(0, equalsIndex).trim()
                        val prop = TableGearProperties.PROPERTY_MAP[key] ?: return

                        if (key == "className" && mode == ConfigMode.DATABASE) {
                            JDBC_DRIVER_SUGGESTIONS.forEach { suggestion ->
                                result.addElement(
                                    LookupElementBuilder.create(suggestion.className)
                                        .withTypeText("JDBC Driver", true)
                                        .withTailText(" — ${suggestion.description}", true)
                                )
                            }
                            return
                        }

                        when (prop.valueType) {
                            ValueType.BOOLEAN -> {
                                listOf("yes", "no", "true", "false").forEach { value ->
                                    result.addElement(
                                        LookupElementBuilder.create(value)
                                            .withTypeText("布尔值", true)
                                    )
                                }
                            }
                            ValueType.ENUM -> {
                                prop.enumValues?.forEach { value ->
                                    result.addElement(
                                        LookupElementBuilder.create(value)
                                            .withTypeText(prop.description.take(60).plus(if (prop.description.length > 60) "…" else ""), true)
                                    )
                                }
                            }
                            ValueType.STRING, ValueType.PATH -> {
                                VALUE_FORMAT_HINTS[key]?.forEach { (example, hint) ->
                                    result.addElement(
                                        LookupElementBuilder.create(example)
                                            .withTypeText(hint, true)
                                            .withTailText(" （格式示例）", true)
                                    )
                                }
                                if (VALUE_FORMAT_HINTS[key].isNullOrEmpty()) {
                                    result.addElement(
                                        LookupElementBuilder.create("<${prop.key}>")
                                            .withTypeText(prop.description, true)
                                            .withTailText(" （占位示例，可替换）", true)
                                    )
                                }
                            }
                            ValueType.LIST -> {
                                LIST_FORMAT_HINTS[key]?.forEach { (example, hint) ->
                                    result.addElement(
                                        LookupElementBuilder.create(example)
                                            .withTypeText(hint, true)
                                            .withTailText(" （格式示例）", true)
                                    )
                                }
                                if (LIST_FORMAT_HINTS[key].isNullOrEmpty()) {
                                    result.addElement(
                                        LookupElementBuilder.create("value1,value2")
                                            .withTypeText("多个用逗号分隔", true)
                                            .withTailText(" （格式示例）", true)
                                    )
                                }
                            }
                            ValueType.SQL -> {
                                result.addElement(
                                    LookupElementBuilder.create("view_name: select id, name from t;")
                                        .withTypeText("视图名: SQL 或 @path.sql", true)
                                        .withTailText(" （格式示例）", true)
                                )
                            }
                        }
                    }
                }
            }
        )
    }
    
    private fun extractPropertyValue(text: String, key: String): String? {
        val lines = text.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#")) continue
            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex > 0) {
                val lineKey = trimmed.substring(0, equalsIndex).trim()
                if (lineKey == key) {
                    return trimmed.substring(equalsIndex + 1).trim()
                }
            }
        }
        return null
    }

    private data class DriverSuggestion(val className: String, val description: String)

    companion object {
        private val JDBC_DRIVER_SUGGESTIONS = listOf(
            DriverSuggestion("com.mysql.cj.jdbc.Driver", "MySQL Connector/J 8.0+ (推荐)"),
            DriverSuggestion("com.mysql.jdbc.Driver", "MySQL Connector/J 5.x (已弃用)"),
            DriverSuggestion("org.mariadb.jdbc.Driver", "MariaDB JDBC Driver"),
            DriverSuggestion("org.postgresql.Driver", "PostgreSQL JDBC Driver"),
            DriverSuggestion("com.microsoft.sqlserver.jdbc.SQLServerDriver", "Microsoft SQL Server JDBC Driver"),
            DriverSuggestion("oracle.jdbc.OracleDriver", "Oracle JDBC Driver"),
            DriverSuggestion("oracle.jdbc.driver.OracleDriver", "Oracle JDBC Driver (旧版)"),
            DriverSuggestion("org.h2.Driver", "H2 Database Driver"),
            DriverSuggestion("com.ibm.db2.jcc.DB2Driver", "IBM DB2 JDBC Driver"),
            DriverSuggestion("com.sap.db.jdbc.Driver", "SAP HANA JDBC Driver"),
            DriverSuggestion("org.apache.derby.jdbc.EmbeddedDriver", "Apache Derby Embedded Driver"),
            DriverSuggestion("org.apache.derby.jdbc.ClientDriver", "Apache Derby Client Driver"),
            DriverSuggestion("com.sybase.jdbc4.jdbc.SybDriver", "Sybase JDBC Driver"),
            DriverSuggestion("com.informix.jdbc.IfxDriver", "IBM Informix JDBC Driver")
        )

        /** key -> list of (示例值, 格式说明)，type 由 ENUM 分支处理 */
        private val VALUE_FORMAT_HINTS: Map<String, List<Pair<String, String>>> = mapOf(
            "url" to listOf(
                "http://localhost:9200" to "ES 地址示例",
                "https://es.example.com:9200" to "ES 地址（HTTPS）"
            ),
            "dburl" to listOf(
                "jdbc:mysql://localhost:3306/your_db?useUnicode=true&characterEncoding=UTF-8" to "MySQL URL",
                "jdbc:postgresql://localhost:5432/your_db" to "PostgreSQL URL"
            ),
            "package_name" to listOf("com.example" to "包名示例", "com.mycompany.project" to "多级包名"),
            "alias" to listOf("default" to "数据源别名", "master" to "主库别名"),
            "src_dir" to listOf("src/main/java" to "Maven 源码目录"),
            "projectname" to listOf("MyProject" to "项目展示名"),
            "swagger_version" to listOf("2" to "Swagger 2", "3" to "OpenAPI 3")
        )

        /** key -> list of (示例值, 格式说明) */
        private val LIST_FORMAT_HINTS: Map<String, List<Pair<String, String>>> = mapOf(
            "index_list" to listOf(
                "my_index" to "单个索引",
                "index1,index2" to "多个索引",
                "indexName:aliasName" to "索引:别名"
            ),
            "primary_key_fields" to listOf("id" to "单主键", "id,tenant_id" to "多字段主键"),
            "table_list" to listOf("t_user,t_order" to "表名逗号分隔", "t_(\\w+)" to "正则匹配"),
            "exclude_table_list" to listOf("T_LOG,T_SESSION" to "排除表列表")
        )
    }
}

