package com.bixuebihui.tablegear.highlight

import com.bixuebihui.tablegear.parser.TableGearLexer
import com.bixuebihui.tablegear.parser.TableGearTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class TableGearSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = TableGearLexer()
    
    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            TableGearTypes.KEY -> arrayOf(TableGearColors.KEY)
            TableGearTypes.EQUALS -> arrayOf(TableGearColors.ASSIGNMENT)
            TableGearTypes.VALUE -> arrayOf(TableGearColors.VALUE)
            TableGearTypes.COMMENT -> arrayOf(TableGearColors.COMMENT)
            else -> emptyArray()
        }
    }
}

