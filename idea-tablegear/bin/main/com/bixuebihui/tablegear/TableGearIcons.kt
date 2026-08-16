package com.bixuebihui.tablegear

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object TableGearIcons {
    // 如果没有图标文件，使用默认图标
    val FILE: Icon? = try {
        IconLoader.getIcon("/icons/tablegear.png", TableGearIcons::class.java)
    } catch (e: Exception) {
        null
    }
}

