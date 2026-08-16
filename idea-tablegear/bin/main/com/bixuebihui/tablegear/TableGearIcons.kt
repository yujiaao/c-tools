package com.bixuebihui.tablegear

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object TableGearIcons {
    /** 文件类型/设置页等使用的图标，与 META-INF 插件 logo 一致 */
    val FILE: Icon? = try {
        IconLoader.getIcon("/META-INF/icon.svg", TableGearIcons::class.java)
    } catch (_: Exception) {
        try {
            IconLoader.getIcon("/META-INF/icon.png", TableGearIcons::class.java)
        } catch (_: Exception) {
            null
        }
    }
}

