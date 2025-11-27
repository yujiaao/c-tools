package com.bixuebihui.tablegear.parser

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class TableGearLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd = 0
    private var currentStart = 0
    private var currentEnd = 0
    private var state = 0
    
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        currentStart = startOffset
        currentEnd = startOffset
        state = initialState
        advance()
    }
    
    override fun getState(): Int = state
    
    override fun getTokenType(): IElementType? {
        if (currentStart >= bufferEnd) return null
        
        val char = buffer[currentStart]
        return when {
            char == '#' -> TableGearTypes.COMMENT
            char == '=' -> TableGearTypes.EQUALS
            char == '\n' || char == '\r' -> TableGearTypes.NEWLINE
            char.isWhitespace() -> com.intellij.psi.TokenType.WHITE_SPACE
            else -> {
                // 简单判断：等号前是 KEY，等号后是 VALUE
                val lineStart = findLineStart(currentStart)
                val equalsIndex = buffer.indexOf('=', lineStart)
                if (equalsIndex > 0 && currentStart < equalsIndex) {
                    TableGearTypes.KEY
                } else {
                    TableGearTypes.VALUE
                }
            }
        }
    }
    
    override fun getTokenStart(): Int = currentStart
    
    override fun getTokenEnd(): Int = currentEnd
    
    override fun advance() {
        if (currentStart >= bufferEnd) {
            currentStart = bufferEnd
            currentEnd = bufferEnd
            return
        }
        
        currentStart = currentEnd
        if (currentStart >= bufferEnd) return
        
        val char = buffer[currentStart]
        when {
            char == '#' -> {
                // 注释到行尾
                currentEnd = findLineEnd(currentStart)
            }
            char == '=' -> {
                currentEnd = currentStart + 1
            }
            char == '\n' || char == '\r' -> {
                currentEnd = currentStart + 1
                if (char == '\r' && currentEnd < bufferEnd && buffer[currentEnd] == '\n') {
                    currentEnd++
                }
            }
            char.isWhitespace() -> {
                while (currentEnd < bufferEnd && buffer[currentEnd].isWhitespace()) {
                    currentEnd++
                }
            }
            else -> {
                // 读取到下一个分隔符
                while (currentEnd < bufferEnd) {
                    val c = buffer[currentEnd]
                    if (c == '=' || c == '\n' || c == '\r' || c == '#' || c.isWhitespace()) {
                        break
                    }
                    currentEnd++
                }
            }
        }
    }
    
    override fun getBufferSequence(): CharSequence = buffer
    
    override fun getBufferEnd(): Int = bufferEnd
    
    private fun findLineStart(offset: Int): Int {
        var pos = offset
        while (pos > 0) {
            val c = buffer[pos - 1]
            if (c == '\n' || c == '\r') break
            pos--
        }
        return pos
    }
    
    private fun findLineEnd(offset: Int): Int {
        var pos = offset
        while (pos < bufferEnd) {
            val c = buffer[pos]
            if (c == '\n' || c == '\r') {
                pos++
                if (c == '\r' && pos < bufferEnd && buffer[pos] == '\n') {
                    pos++
                }
                break
            }
            pos++
        }
        return pos
    }
}

