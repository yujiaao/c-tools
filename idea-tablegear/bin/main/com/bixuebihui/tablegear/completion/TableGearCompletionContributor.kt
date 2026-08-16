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
                                    result.addElement(LookupElementBuilder.create(value))
                                }
                            }
                            ValueType.ENUM -> {
                                prop.enumValues?.forEach { value ->
                                    result.addElement(LookupElementBuilder.create(value))
                                }
                            }
                            else -> {}
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
    }
}

