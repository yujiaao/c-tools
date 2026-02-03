package com.bixuebihui.tablegear

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

fun PsiElement.isInTableGearFile(): Boolean {
    val vFileName = containingFile?.virtualFile?.name
    val name = vFileName ?: containingFile?.name ?: return false
    return TableGearFileType.isTableGearFile(name)
}

fun PsiElement.extractPropertyKey(): String? {
    if (!isInTableGearFile()) {
        return null
    }
    val document = containingFile?.viewProvider?.document ?: return null
    val offset = textOffset
    val currentLine = document.getLineNumber(offset)
    if (currentLine < 0) {
        return null
    }
    val logicalLine = findLogicalLineStart(document, currentLine)
    val lineStart = document.getLineStartOffset(logicalLine)
    val lineEnd = document.getLineEndOffset(logicalLine)
    val lineText = document.getText(TextRange(lineStart, lineEnd))
    val separatorIndex = findSeparatorIndex(lineText)
    if (separatorIndex <= 0) {
        return null
    }
    val keyText = lineText.substring(0, separatorIndex).trim()
    return keyText.takeIf { it.isNotEmpty() }
}

private fun findSeparatorIndex(line: String): Int {
    val eq = line.indexOf('=')
    val colon = line.indexOf(':')
    val candidates = listOf(eq, colon).filter { it >= 0 }
    return candidates.minOrNull() ?: -1
}

private fun findLogicalLineStart(document: Document, lineNumber: Int): Int {
    var line = lineNumber
    while (line > 0) {
        val prevLineRange = TextRange(document.getLineStartOffset(line - 1), document.getLineEndOffset(line - 1))
        val prevLineText = document.getText(prevLineRange)
        if (continuesToNextLine(prevLineText)) {
            line--
        } else {
            break
        }
    }
    return line
}

private fun continuesToNextLine(line: String): Boolean {
    val trimmed = line.trimEnd()
    if (trimmed.isEmpty()) return false
    var count = 0
    var index = trimmed.length - 1
    while (index >= 0 && trimmed[index] == '\\') {
        count++
        index--
    }
    return count % 2 == 1
}

