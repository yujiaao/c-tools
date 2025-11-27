package com.bixuebihui.tablegear.inspection

import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegear.ValueType
import com.bixuebihui.tablegear.parser.TableGearFile
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class TableGearValidationInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is TableGearFile) return

                val seenKeys = mutableSetOf<String>()
                val lines = file.text.split("\n")
                var buffer = StringBuilder()
                var logicalLineStart = 0
                var inContinuation = false

                fun flushLine() {
                    if (buffer.isEmpty()) return
                    val content = buffer.toString()
                    buffer = StringBuilder()
                    processLogicalLine(content, logicalLineStart, seenKeys, file, holder)
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
            }
        }
    }

    private fun processLogicalLine(
        lineContent: String,
        zeroBasedLine: Int,
        seenKeys: MutableSet<String>,
        file: PsiFile,
        holder: ProblemsHolder
    ) {
        val trimmed = lineContent.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return
        }

        val equalsIndex = trimmed.indexOf('=')
        if (equalsIndex <= 0) {
            holder.registerProblem(
                file,
                "第 ${zeroBasedLine + 1} 行：配置项必须使用 key=value 形式",
                ProblemHighlightType.ERROR
            )
            return
        }

        val key = trimmed.substring(0, equalsIndex).trim()
        val value = trimmed.substring(equalsIndex + 1).trim()

        if (!seenKeys.add(key)) {
            holder.registerProblem(
                file,
                "重复定义的配置项: $key",
                ProblemHighlightType.WARNING
            )
        }

        val prop = TableGearProperties.PROPERTY_MAP[key]
        if (prop == null) {
            holder.registerProblem(
                file,
                "未识别的配置项: $key",
                ProblemHighlightType.WARNING
            )
        } else {
            when (prop.valueType) {
                ValueType.BOOLEAN -> {
                    if (value.isNotEmpty() && !value.matches(Regex("^(yes|no|true|false)$", RegexOption.IGNORE_CASE))) {
                        holder.registerProblem(
                            file,
                            "$key 需要 yes/no（或 true/false）",
                            ProblemHighlightType.ERROR
                        )
                    }
                }
                ValueType.ENUM -> {
                    if (value.isNotEmpty() && prop.enumValues?.contains(value.lowercase()) == false) {
                        holder.registerProblem(
                            file,
                            "$key 仅支持 ${prop.enumValues.joinToString(", ")}",
                            ProblemHighlightType.ERROR
                        )
                    }
                }
                else -> {}
            }
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
}

