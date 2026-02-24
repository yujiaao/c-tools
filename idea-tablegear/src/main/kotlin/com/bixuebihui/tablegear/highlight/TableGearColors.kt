package com.bixuebihui.tablegear.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font

object TableGearColors {
    // 使用 fallback 继承 IDE 语言默认色，未配置时也有合理显示；同时通过 additionalTextAttributes 提供好看默认值
    val KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val DATABASE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_DATABASE_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val PATH_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_PATH_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val BOOLEAN_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_BOOLEAN_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_LIST_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val PACKAGE_NAME_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_PACKAGE_NAME_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val VIEW_LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_LIST_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val VIEW_SEPARATOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val ASSIGNMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_ASSIGNMENT", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val VIEW_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VIEW_NAME", DefaultLanguageHighlighterColors.IDENTIFIER)
    val SQL: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SQL", DefaultLanguageHighlighterColors.STRING)
    val SENSITIVE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SENSITIVE_KEY", DefaultLanguageHighlighterColors.KEYWORD)
    val SENSITIVE_FIELD: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_SENSITIVE_FIELD", DefaultLanguageHighlighterColors.STRING)
    val VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_VALUE", DefaultLanguageHighlighterColors.STRING)
    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey("TABLEGEAR_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

    @Suppress("unused")
    internal fun textAttr(hex: String, bold: Boolean = false): TextAttributes {
        val color = Color.decode(hex)
        val fontStyle = if (bold) Font.BOLD else Font.PLAIN
        return TextAttributes(color, null, null, null, fontStyle)
    }
}

