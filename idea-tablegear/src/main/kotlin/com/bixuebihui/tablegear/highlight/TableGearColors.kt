package com.bixuebihui.tablegear.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import java.awt.Color
import java.awt.Font

object TableGearColors {
    private fun textAttr(hex: String, bold: Boolean = false): TextAttributes {
        val color = Color.decode(hex)
        val fontStyle = if (bold) Font.BOLD else Font.PLAIN
        return TextAttributes(color, null, null, null, fontStyle)
    }

    val KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_KEY",
        textAttr("#F8F8F2")
    )
    val DATABASE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_DATABASE_KEY",
        textAttr("#FFD866")
    )
    val PATH_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_PATH_KEY",
        textAttr("#A9DC76")
    )
    val BOOLEAN_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_BOOLEAN_KEY",
        textAttr("#FF6188", bold = true)
    )
    val LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_LIST_KEY",
        textAttr("#78DCE8")
    )
    val PACKAGE_NAME_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_PACKAGE_NAME_KEY",
        textAttr("#7BD88F")
    )
    val VIEW_LIST_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_VIEW_LIST_KEY",
        textAttr("#55CFE3", bold = true)
    )
    val VIEW_SEPARATOR: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_VIEW_SEPARATOR",
        textAttr("#AB9DF2", bold = true)
    )
    val ASSIGNMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_ASSIGNMENT",
        textAttr("#AB9DF2", bold = true)
    )
    val VIEW_NAME: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_VIEW_NAME",
        textAttr("#00FFFF", bold = true)
    )
    val SQL: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_SQL",
        textAttr("#A6E22E")
    )
    val SENSITIVE_KEY: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_SENSITIVE_KEY",
        textAttr("#FF9B5E")
    )
    val SENSITIVE_FIELD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_SENSITIVE_FIELD",
        textAttr("#FFB347")
    )
    val VALUE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_VALUE",
        textAttr("#A6E22E")
    )
    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "TABLEGEAR_COMMENT",
        textAttr("#035813")
    )
}

