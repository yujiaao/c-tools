package com.bixuebihui.tablegear.util

import java.io.*
import java.nio.charset.Charset

/**
 * 解析并写出 properties 文件时保留原有结构：key 顺序与注释均不改变。
 * 适用于 tablegen*.properties / es*.properties。
 */
object PropertiesFormatPreserving {

    private const val COMMENT_PREFIX = '#'
    private const val KEY_VALUE_SEPARATOR = '='

    /**
     * 表示文件中的一行：注释、空行或 key=value
     */
    sealed class Line {
        data class Comment(val raw: String) : Line()           // 含 # 的整行，原样保留
        data object Blank : Line()                             // 空行
        data class Property(val key: String, val value: String, val rawKey: String) : Line()  // key=value，rawKey 保留等号前空白等
    }

    /**
     * 将文件内容解析为有序的 Line 列表（保留注释与 key 顺序）。
     */
    fun parse(content: String): List<Line> {
        val lines = mutableListOf<Line>()
        for (raw in content.split("\n")) {
            val trimmed = raw.trimStart()
            when {
                trimmed.isEmpty() -> lines.add(Line.Blank)
                trimmed[0] == COMMENT_PREFIX -> lines.add(Line.Comment(raw))
                trimmed.contains(KEY_VALUE_SEPARATOR.toString()) -> {
                    val sep = raw.indexOf(KEY_VALUE_SEPARATOR)
                    val keyPart = raw.substring(0, sep)
                    val key = keyPart.trim()
                    val value = raw.substring(sep + 1).trim()  // 只从第一个 = 分割，value 可含 =
                    lines.add(Line.Property(key, value, keyPart))
                }
                else -> lines.add(Line.Comment(raw)) // 不规范的行当注释保留
            }
        }
        return lines
    }

    /**
     * 将 Line 列表与新的 key->value 合并，然后按原顺序写出。
     * - 已存在的 key 用 [newValues] 中的新值覆盖，写在同一行位置；
     * - [newValues] 中存在但文件中未出现的 key 会追加到文件末尾。
     */
    fun mergeAndWrite(
        lines: List<Line>,
        newValues: Map<String, String>,
        out: Writer,
        lineSeparator: String = "\n"
    ) {
        val usedKeys = mutableSetOf<String>()
        for (line in lines) {
            when (line) {
                is Line.Comment -> out.write(line.raw + lineSeparator)
                is Line.Blank -> out.write(lineSeparator)
                is Line.Property -> {
                    val value = newValues[line.key] ?: line.value
                    usedKeys.add(line.key)
                    out.write(line.rawKey + "=" + value + lineSeparator)
                }
            }
        }
        val appended = newValues.keys.filter { it !in usedKeys }
        if (appended.isNotEmpty()) {
            if (lines.isNotEmpty()) out.write(lineSeparator)
            for (key in appended) {
                newValues[key]?.let { out.write("$key=$it$lineSeparator") }
            }
        }
        out.flush()
    }

    /**
     * 从文件读取、用 [newValues] 覆盖对应 key、再写回同一文件，保留 key 顺序和注释。
     */
    @Throws(IOException::class)
    fun savePreservingFormat(file: File, newValues: Map<String, String>, charset: Charset = Charsets.UTF_8) {
        val content = if (file.exists()) file.readText(charset) else ""
        val lines = parse(content)
        file.writer(charset).use { mergeAndWrite(lines, newValues, it) }
    }

    /**
     * 从路径读取、用 [newValues] 覆盖、再写回，保留 key 顺序和注释。
     */
    @Throws(IOException::class)
    fun savePreservingFormat(path: String, newValues: Map<String, String>, charset: Charset = Charsets.UTF_8) {
        savePreservingFormat(File(path), newValues, charset)
    }

    /**
     * 从解析结果中收集当前所有 key=value（用于从文件加载到内存）。
     */
    fun collectProperties(lines: List<Line>): Map<String, String> {
        return lines.filterIsInstance<Line.Property>().associate { it.key to it.value }
    }
}
