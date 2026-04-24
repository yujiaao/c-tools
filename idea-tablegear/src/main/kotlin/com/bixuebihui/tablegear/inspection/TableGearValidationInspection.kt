package com.bixuebihui.tablegear.inspection

import com.bixuebihui.tablegear.TableGearBundle
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegear.ValueType
import com.bixuebihui.tablegear.isInTableGearFile
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class TableGearValidationInspection : LocalInspectionTool() {
    companion object {
        private val LOG = Logger.getInstance(TableGearValidationInspection::class.java)
    }
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                try {
                    // 检查是否是 TableGear 文件（支持 Properties 语言的文件）
                    val fileName = file.virtualFile?.name ?: file.name
                    val isTableGear = file.isInTableGearFile()
                    
                    // 使用 INFO 级别以便更容易看到日志
                    LOG.info("TableGearValidationInspection.visitFile: fileName=$fileName, isTableGear=$isTableGear, fileClass=${file.javaClass.simpleName}")
                    
                    if (!isTableGear) {
                        LOG.info("Skipping file (not TableGear): $fileName")
                        return
                    }
                    
                    // 检查文件是否有效
                    if (!file.isValid) {
                        LOG.warn("File is not valid: $fileName")
                        return
                    }
                    
                    LOG.info("Processing TableGear file: $fileName, text length=${file.text.length}")

                    val seenKeys = mutableMapOf<String, Int>() // key -> line number (0-based)
                    val document = file.viewProvider.document
                    if (document == null) {
                        LOG.warn("Document is null for file: $fileName")
                        return
                    }
                    val lines = file.text.split("\n")
                    var buffer = StringBuilder()
                    var logicalLineStart = 0
                    var inContinuation = false

                    fun flushLine() {
                        if (buffer.isEmpty()) return
                        val content = buffer.toString()
                        buffer = StringBuilder()
                        processLogicalLine(content, logicalLineStart, seenKeys, file, document, holder)
                    }

                    lines.forEachIndexed { index, rawLine ->
                        val line = rawLine.trimEnd('\r')
                        val trimmedLeft = line.trimStart()
                        if (!inContinuation && (trimmedLeft.isEmpty() || trimmedLeft.startsWith("#"))) {
                            // 独立的空行或注释，忽略
                            buffer = StringBuilder()
                            inContinuation = false
                            return@forEachIndexed
                        }

                        if (!inContinuation) {
                            logicalLineStart = index
                            buffer = StringBuilder()
                        }

                        val normalized = if (inContinuation) trimmedLeft else line
                        val (segment, continues) = stripContinuation(normalized)
                        buffer.append(segment)
                        inContinuation = continues

                        if (!continues) {
                            flushLine()
                        }
                    }

                    // 文件尾部如果存在拼接但未换行的情况
                    if (inContinuation) {
                        flushLine()
                    }
                } catch (e: Exception) {
                    // 捕获任何异常，避免影响 IntelliJ 的 Problems View
                    LOG.warn("Error during TableGear validation for file: ${file.name}", e)
                }
            }
        }
    }

    private fun processLogicalLine(
        lineContent: String,
        zeroBasedLine: Int,
        seenKeys: MutableMap<String, Int>,
        file: PsiFile,
        document: Document,
        holder: ProblemsHolder
    ) {
        try {
            // 检查文件是否仍然有效
            if (!file.isValid) {
                return
            }
            
            val trimmed = lineContent.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return
            }

            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex <= 0) {
                // 检查行号是否有效
                if (zeroBasedLine < 0 || zeroBasedLine >= document.lineCount) {
                    return
                }
                val lineStart = document.getLineStartOffset(zeroBasedLine)
                val lineEnd = document.getLineEndOffset(zeroBasedLine)
                holder.registerProblem(
                    file,
                    TableGearBundle.message("inspection.invalidFormat", zeroBasedLine + 1),
                    ProblemHighlightType.ERROR,
                    TextRange(lineStart, lineEnd)
                )
                return
            }

            val key = trimmed.substring(0, equalsIndex).trim()
            val value = trimmed.substring(equalsIndex + 1).trim()

            // 检查行号是否有效
            if (zeroBasedLine < 0 || zeroBasedLine >= document.lineCount) {
                return
            }

            // 获取原始行文本以准确定位 key
            val lineStart = document.getLineStartOffset(zeroBasedLine)
            val lineEnd = document.getLineEndOffset(zeroBasedLine)
            val lineText = document.getText(TextRange(lineStart, lineEnd))
            val valueStartInLine = lineText.indexOf('=').let { if (it >= 0) it + 1 else lineText.length }
            val valueRange = TextRange(lineStart + valueStartInLine, lineEnd)
        
            // 找到 key 在原始行中的位置（考虑前导空格）
            val trimmedLine = lineText.trimStart()
            val leadingSpaces = lineText.length - trimmedLine.length
            val keyStartInTrimmed = trimmedLine.indexOf(key)
            val keyStartInLine = if (keyStartInTrimmed >= 0) leadingSpaces + keyStartInTrimmed else -1
            
            // 检查重复的配置项
            val previousLine = seenKeys[key]
            if (previousLine != null) {
                // 标记当前行的 key 部分为重复
                if (keyStartInLine >= 0) {
                    val keyEndInLine = keyStartInLine + key.length
                    val keyRange = TextRange(lineStart + keyStartInLine, lineStart + keyEndInLine)
                    LOG.info("Found duplicate key: $key at line ${zeroBasedLine + 1} (first at line ${previousLine + 1})")
                    holder.registerProblem(
                        file,
                        TableGearBundle.message("inspection.duplicateKey", key, previousLine + 1),
                        ProblemHighlightType.ERROR,
                        keyRange
                    )
                } else {
                    LOG.warn("Could not find key '$key' in line ${zeroBasedLine + 1}: $lineText")
                }
            } else {
                seenKeys[key] = zeroBasedLine
                LOG.info("Registered key: $key at line ${zeroBasedLine + 1}")
            }

            val prop = TableGearProperties.PROPERTY_MAP[key]
            if (prop == null) {
                // 标记未识别的配置项
                if (keyStartInLine >= 0) {
                    val keyEndInLine = keyStartInLine + key.length
                    val keyRange = TextRange(lineStart + keyStartInLine, lineStart + keyEndInLine)
                    holder.registerProblem(
                        file,
                        TableGearBundle.message("inspection.unknownKey", key),
                        ProblemHighlightType.WARNING,
                        keyRange
                    )
                }
            } else {
                // 验证值类型
                when (prop.valueType) {
                    ValueType.BOOLEAN -> {
                        if (value.isNotEmpty() && !value.matches(Regex("^(yes|no|true|false)$", RegexOption.IGNORE_CASE))) {
                            holder.registerProblem(
                                file,
                                TableGearBundle.message("inspection.booleanExpected", key),
                                ProblemHighlightType.ERROR,
                                valueRange
                            )
                        }
                    }
                    ValueType.ENUM -> {
                        if (value.isNotEmpty() && prop.enumValues?.contains(value.lowercase()) == false) {
                            holder.registerProblem(
                                file,
                                TableGearBundle.message("inspection.enumExpected", key, prop.enumValues.joinToString(", ")),
                                ProblemHighlightType.ERROR,
                                valueRange
                            )
                        }
                    }
                    else -> {}
                }

                val allText = file.text
                val resolvedApiDocProvider = resolveApiDocProvider(allText)
                if (key == "use_swagger" && resolvedApiDocProvider == "springdoc" && isTruthy(value)) {
                    holder.registerProblem(
                        file,
                        TableGearBundle.message("inspection.springdocIgnoreUseSwagger"),
                        ProblemHighlightType.WARNING,
                        valueRange
                    )
                }
                if (key == "swagger_version" && resolvedApiDocProvider == "springdoc" && value.isNotEmpty()) {
                    holder.registerProblem(
                        file,
                        TableGearBundle.message("inspection.springdocIgnoreSwaggerVersion"),
                        ProblemHighlightType.WARNING,
                        valueRange
                    )
                }
                if (key == "api_doc_provider" && value.equals("springdoc", ignoreCase = true)) {
                    val useSwagger = extractPropertyValue(allText, "use_swagger")
                    if (isTruthy(useSwagger)) {
                        holder.registerProblem(
                            file,
                            TableGearBundle.message("inspection.springdocSuggestion"),
                            ProblemHighlightType.WARNING,
                            valueRange
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // 捕获处理行时的异常，避免影响 IntelliJ 的 Problems View
            LOG.warn("Error processing line $zeroBasedLine in file: ${file.name}", e)
        }
    }

    private fun stripContinuation(line: String): Pair<String, Boolean> {
        val trimmedRight = line.trimEnd()
        val continuation = isContinuationLine(trimmedRight)
        val content = if (continuation && trimmedRight.isNotEmpty()) {
            trimmedRight.substring(0, trimmedRight.length - 1)
        } else {
            trimmedRight
        }
        val finalSegment = if (continuation) content else content
        return finalSegment to continuation
    }

    private fun isContinuationLine(line: String): Boolean {
        if (line.isEmpty()) return false
        var backslashCount = 0
        var index = line.length - 1
        while (index >= 0 && line[index] == '\\') {
            backslashCount++
            index--
        }
        return backslashCount % 2 == 1
    }

    private fun extractPropertyValue(text: String, targetKey: String): String? {
        val lines = text.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue
            }
            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex <= 0) {
                continue
            }
            val key = trimmed.substring(0, equalsIndex).trim()
            if (key == targetKey) {
                return trimmed.substring(equalsIndex + 1).trim()
            }
        }
        return null
    }

    private fun resolveApiDocProvider(text: String): String {
        val explicit = extractPropertyValue(text, "api_doc_provider")?.lowercase()
        if (explicit == "swagger" || explicit == "springdoc") {
            return explicit
        }
        val hasSwaggerVersion = !extractPropertyValue(text, "swagger_version").isNullOrBlank()
        return if (hasSwaggerVersion) "swagger" else "springdoc"
    }

    private fun isTruthy(value: String?): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }
        return value.equals("yes", ignoreCase = true) || value.equals("true", ignoreCase = true)
    }
}

