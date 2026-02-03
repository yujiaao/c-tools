package com.bixuebihui.tablegear.parser

import com.bixuebihui.tablegear.TableGearLanguage
import com.intellij.psi.tree.IElementType

class TableGearTokenType(debugName: String) : IElementType(debugName, TableGearLanguage) {
    companion object {
        val KEY = TableGearTokenType("KEY")
        val EQUALS = TableGearTokenType("EQUALS")
        val VALUE = TableGearTokenType("VALUE")
        val COMMENT = TableGearTokenType("COMMENT")
        val NEWLINE = TableGearTokenType("NEWLINE")
    }
}

object TableGearTypes {
    val KEY = TableGearTokenType.KEY
    val EQUALS = TableGearTokenType.EQUALS
    val VALUE = TableGearTokenType.VALUE
    val COMMENT = TableGearTokenType.COMMENT
    val NEWLINE = TableGearTokenType.NEWLINE
    
    object Factory {
        fun createElement(node: com.intellij.lang.ASTNode): com.intellij.psi.PsiElement {
            return TableGearPsiElement(node)
        }
    }
}

class TableGearPsiElement(node: com.intellij.lang.ASTNode) : com.intellij.extapi.psi.ASTWrapperPsiElement(node) {
    override fun toString(): String = "TableGear:${node.elementType}"
}

