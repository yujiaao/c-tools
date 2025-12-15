package com.bixuebihui.tablegear.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

class TableGearParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        builder.setDebugMode(false)
        val rootMarker = builder.mark()
        
        while (!builder.eof()) {
            parseProperty(builder)
        }
        
        rootMarker.done(root)
        return builder.treeBuilt
    }
    
    private fun parseProperty(builder: PsiBuilder) {
        // 跳过空白
        while (builder.tokenType == com.intellij.psi.TokenType.WHITE_SPACE) {
            builder.advanceLexer()
        }
        
        if (builder.eof()) return
        
        // 注释行
        if (builder.tokenType == TableGearTypes.COMMENT) {
            val marker = builder.mark()
            builder.advanceLexer()
            // 跳过注释内容直到换行
            while (!builder.eof() && builder.tokenType != TableGearTypes.NEWLINE) {
                builder.advanceLexer()
            }
            if (builder.tokenType == TableGearTypes.NEWLINE) {
                builder.advanceLexer()
            }
            marker.done(TableGearTypes.COMMENT)
            return
        }
        
        // key=value 行
        if (builder.tokenType == TableGearTypes.KEY) {
            val propertyMarker = builder.mark()
            
            // KEY
            builder.advanceLexer()
            
            // 跳过空白
            while (builder.tokenType == com.intellij.psi.TokenType.WHITE_SPACE) {
                builder.advanceLexer()
            }
            
            // EQUALS
            if (builder.tokenType == TableGearTypes.EQUALS) {
                builder.advanceLexer()
            }
            
            // 跳过空白
            while (builder.tokenType == com.intellij.psi.TokenType.WHITE_SPACE) {
                builder.advanceLexer()
            }
            
            // VALUE (可能包含多个token，直到换行)
            while (!builder.eof() && 
                   builder.tokenType != TableGearTypes.NEWLINE && 
                   builder.tokenType != TableGearTypes.COMMENT) {
                builder.advanceLexer()
            }
            
            // NEWLINE
            if (builder.tokenType == TableGearTypes.NEWLINE) {
                builder.advanceLexer()
            }
            
            propertyMarker.done(TableGearTypes.KEY)
        } else {
            // 未知token，跳过
            builder.advanceLexer()
        }
    }
}

