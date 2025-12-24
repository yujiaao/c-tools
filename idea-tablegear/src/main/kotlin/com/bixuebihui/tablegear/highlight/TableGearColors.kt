package com.bixuebihui.tablegear.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font

object TableGearColors {
    // 使用新的 API：只创建 key，默认属性通过颜色方案设置
    val KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_KEY")
    val DATABASE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_DATABASE_KEY")
    val PATH_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_PATH_KEY")
    val BOOLEAN_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_BOOLEAN_KEY")
    val LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_LIST_KEY")
    val PACKAGE_NAME_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_PACKAGE_NAME_KEY")
    val VIEW_LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_LIST_KEY")
    val VIEW_SEPARATOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_SEPARATOR")
    val ASSIGNMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_ASSIGNMENT")
    val VIEW_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_NAME")
    val SQL: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SQL")
    val SENSITIVE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SENSITIVE_KEY")
    val SENSITIVE_FIELD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SENSITIVE_FIELD")
    val VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VALUE")
    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_COMMENT")
    
    // 保留用于设置默认属性的辅助方法（如果需要）
    @Suppress("unused")
    private fun textAttr(hex: String, bold: Boolean = false): TextAttributes {
        val color = Color.decode(hex)
        val fontStyle = if (bold) Font.BOLD else Font.PLAIN
        return TextAttributes(color, null, null, null, fontStyle)
    }
}

