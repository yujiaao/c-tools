package com.bixuebihui.tablegear.parser

import com.bixuebihui.tablegear.TableGearLanguage
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class TableGearParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer {
        return TableGearLexer()
    }

    override fun createParser(project: Project?): PsiParser {
        return TableGearParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode): PsiElement {
        return TableGearTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return TableGearFile(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }

    companion object {
        val FILE = IFileElementType(TableGearLanguage)
        val COMMENTS = TokenSet.create(TableGearTypes.COMMENT)
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    }
}

