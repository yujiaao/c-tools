package com.bixuebihui.tablegear.util

import java.io.File
import java.nio.charset.Charset

/**
 * 保存 tablegen*.properties / es*.properties 时请使用此类，以保留：
 * - key 的原有顺序
 * - 所有注释与空行
 */
object TableGearPropertiesIo {

    /**
     * 将当前配置写回文件，不破坏原有文件结构（key 顺序与注释均保留）。
     * 配置编辑器保存时请调用此方法，勿使用 [java.util.Properties].store()。
     *
     * @param file 目标 properties 文件
     * @param keyValueMap 要写回的 key -> value（可包含新增 key，会追加到文件末尾）
     * @param charset 文件编码，默认 UTF-8
     */
    @JvmStatic
    fun savePreservingFormat(
        file: File,
        keyValueMap: Map<String, String>,
        charset: Charset = Charsets.UTF_8
    ) {
        PropertiesFormatPreserving.savePreservingFormat(file, keyValueMap, charset)
    }

    /**
     * 重载：使用文件路径。
     */
    @JvmStatic
    fun savePreservingFormat(
        path: String,
        keyValueMap: Map<String, String>,
        charset: Charset = Charsets.UTF_8
    ) {
        PropertiesFormatPreserving.savePreservingFormat(path, keyValueMap, charset)
    }
}
