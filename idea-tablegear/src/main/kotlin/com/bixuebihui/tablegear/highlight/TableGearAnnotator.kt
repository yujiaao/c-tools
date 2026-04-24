package com.bixuebihui.tablegear.highlight

import com.bixuebihui.tablegear.TableGearBundle
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegear.ValueType
import com.bixuebihui.tablegear.extractPropertyKey
import com.bixuebihui.tablegear.isInTableGearFile
import com.bixuebihui.tablegear.parser.TableGearTypes
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType

class TableGearAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!element.isInTableGearFile()) {
            return
        }
        when (element.elementType) {
            TableGearTypes.KEY -> {
                val keyText = element.extractPropertyKey()
                if (keyText.isNullOrEmpty()) {
                    return
                }
                val prop = TableGearProperties.PROPERTY_MAP[keyText]
                if (prop == null) {
                    val display = element.text.trim()
                    holder.newAnnotation(HighlightSeverity.WARNING, TableGearBundle.message("inspection.unknownKey", display))
                        .create()
                } else {
                    // 根据配置项类型应用不同的颜色
                    val colorKey = when {
                        keyText == "package_name" -> TableGearColors.PACKAGE_NAME_KEY
                        keyText == "view_list" -> TableGearColors.VIEW_LIST_KEY
                        prop.valueType == ValueType.PATH -> TableGearColors.PATH_KEY
                        prop.valueType == ValueType.BOOLEAN -> TableGearColors.BOOLEAN_KEY
                        prop.valueType == ValueType.LIST -> TableGearColors.LIST_KEY
                        keyText in DATABASE_KEYS -> TableGearColors.DATABASE_KEY
                        keyText in SENSITIVE_KEYS -> TableGearColors.SENSITIVE_KEY
                        else -> TableGearColors.KEY
                    }
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .textAttributes(colorKey)
                        .create()
                }
            }
            TableGearTypes.EQUALS -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(TableGearColors.ASSIGNMENT)
                    .create()
            }
            TableGearTypes.COMMENT -> {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(TableGearColors.COMMENT)
                    .create()
            }
            TableGearTypes.VALUE -> {
                val document = element.containingFile.viewProvider.document ?: return
                val key = element.extractPropertyKey() ?: return
                val lineNumber = document.getLineNumber(element.textOffset)
                if (lineNumber < 0) return
                val lineStart = document.getLineStartOffset(lineNumber)
                val lineEnd = document.getLineEndOffset(lineNumber)
                val line = element.containingFile.text.substring(lineStart, lineEnd)
                val equalsIndex = line.indexOf('=')
                if (equalsIndex <= 0) return
                val valuePortion = line.substring(equalsIndex + 1)
                val leadingSpaces = leadingWhitespaceLength(valuePortion)
                val value = valuePortion.trim()
                val valueStartOffset = lineStart + equalsIndex + 1 + leadingSpaces
                when (key) {
                    "view_list" -> highlightViewList(value, valueStartOffset, element.textRange, holder)
                    "encrypted_fields", "ignore_fields" -> highlightEncryptedFields(value, valueStartOffset, element.textRange, holder)
                    else -> {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .textAttributes(TableGearColors.VALUE)
                            .create()
                    }
                }
            }
        }
    }

    private fun leadingWhitespaceLength(text: String): Int {
        var index = 0
        while (index < text.length && text[index].isWhitespace()) {
            index++
        }
        return index
    }
    
    private fun highlightViewList(value: String, baseOffset: Int, limitRange: TextRange, holder: AnnotationHolder) {
        // view_list 格式: view_name: select ...; 或 view_name: @file.sql;
        val parts = value.split(";")
        var offset = 0
        for (part in parts) {
            val trimmed = part.trim()
            if (trimmed.isEmpty()) continue
            
            val colonIndex = trimmed.indexOf(':')
            if (colonIndex > 0) {
                val viewName = trimmed.substring(0, colonIndex).trim()
                val sqlPart = trimmed.substring(colonIndex + 1).trim()
                
                // 高亮 view name (cyan)
                val viewNameStart = value.indexOf(viewName, offset)
                if (viewNameStart >= 0) {
                    val viewNameEnd = viewNameStart + viewName.length
                    holder.highlightRange(baseOffset + viewNameStart, baseOffset + viewNameEnd, limitRange, TableGearColors.VIEW_NAME)
                }
                
                // 高亮分隔符 (purple, bold)
                val colonPos = value.indexOf(':', offset)
                if (colonPos >= 0) {
                    holder.highlightRange(baseOffset + colonPos, baseOffset + colonPos + 1, limitRange, TableGearColors.VIEW_SEPARATOR)
                }
                
                // 高亮 SQL (如果不是 @ 开头)
                if (sqlPart.isNotEmpty() && !sqlPart.startsWith("@")) {
                    val sqlStart = value.indexOf(sqlPart, offset)
                    if (sqlStart >= 0) {
                        val sqlEnd = sqlStart + sqlPart.length
                        holder.highlightRange(baseOffset + sqlStart, baseOffset + sqlEnd, limitRange, TableGearColors.SQL)
                    }
                }
            }
            offset = value.indexOf(';', offset) + 1
            if (offset <= 0) break
        }
    }
    
    private fun highlightEncryptedFields(value: String, baseOffset: Int, limitRange: TextRange, holder: AnnotationHolder) {
        var cursor = 0
        val length = value.length
        while (cursor < length) {
            cursor = findNextNonWhitespace(value, cursor, length)
            if (cursor >= length) break

            val semicolonIndex = value.indexOf(';', cursor)
            val segmentEnd = if (semicolonIndex == -1) length else semicolonIndex
            val colonIndex = value.indexOf(':', cursor)

            if (colonIndex != -1 && colonIndex < segmentEnd) {
                val tableStart = findNextNonWhitespace(value, cursor, colonIndex)
                val tableEnd = findPrevNonWhitespace(value, colonIndex - 1) + 1
                if (tableStart < tableEnd) {
                    holder.highlightRange(
                        baseOffset + tableStart,
                        baseOffset + tableEnd,
                        limitRange,
                        TableGearColors.SENSITIVE_KEY
                    )
                }

                holder.highlightRange(
                    baseOffset + colonIndex,
                    baseOffset + colonIndex + 1,
                    limitRange,
                    TableGearColors.VIEW_SEPARATOR
                )

                highlightFieldList(value, colonIndex + 1, segmentEnd, baseOffset, limitRange, holder)
            } else {
                highlightFieldList(value, cursor, segmentEnd, baseOffset, limitRange, holder)
            }

            if (semicolonIndex != -1) {
                holder.highlightRange(
                    baseOffset + semicolonIndex,
                    baseOffset + semicolonIndex + 1,
                    limitRange,
                    TableGearColors.VIEW_SEPARATOR
                )
                cursor = semicolonIndex + 1
            } else {
                break
            }
        }
    }

    private fun highlightFieldList(
        value: String,
        start: Int,
        end: Int,
        baseOffset: Int,
        limitRange: TextRange,
        holder: AnnotationHolder
    ) {
        var cursor = findNextNonWhitespace(value, start, end)
        if (cursor >= end) return

        while (cursor < end) {
            val commaIndex = value.indexOf(',', cursor).let { if (it == -1 || it >= end) -1 else it }
            val segmentEnd = if (commaIndex == -1) end else commaIndex
            val fieldStart = findNextNonWhitespace(value, cursor, segmentEnd)
            val fieldEnd = findPrevNonWhitespace(value, segmentEnd - 1) + 1
            if (fieldStart < fieldEnd) {
                holder.highlightRange(
                    baseOffset + fieldStart,
                    baseOffset + fieldEnd,
                    limitRange,
                    TableGearColors.SENSITIVE_FIELD
                )
            }

            if (commaIndex != -1) {
                holder.highlightRange(
                    baseOffset + commaIndex,
                    baseOffset + commaIndex + 1,
                    limitRange,
                    TableGearColors.VIEW_SEPARATOR
                )
                cursor = commaIndex + 1
            } else {
                break
            }
        }
    }
    
    companion object {
        private val DATABASE_KEYS = setOf(
            "className", "dburl", "username", "password", "schema", "table_owner", 
            "catalog", "projectname", "alias", "add_prefix", "remove_prefix"
        )
        private val SENSITIVE_KEYS = setOf("password", "username")
    }

    private fun AnnotationHolder.highlightRange(start: Int, end: Int, limit: TextRange, key: TextAttributesKey) {
        if (start >= end) return
        if (start < limit.startOffset || end > limit.endOffset) return
        newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(TextRange(start, end))
            .textAttributes(key)
            .create()
    }

    private fun findNextNonWhitespace(text: String, start: Int, end: Int): Int {
        var index = start.coerceAtLeast(0)
        val limit = end.coerceAtMost(text.length)
        while (index < limit && text[index].isWhitespace()) {
            index++
        }
        return index
    }

    private fun findPrevNonWhitespace(text: String, start: Int): Int {
        var index = start.coerceAtMost(text.length - 1)
        while (index >= 0 && text[index].isWhitespace()) {
            index--
        }
        return index
    }
}

