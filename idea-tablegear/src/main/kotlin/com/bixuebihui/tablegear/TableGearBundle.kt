package com.bixuebihui.tablegear

import org.jetbrains.annotations.PropertyKey
import java.util.ResourceBundle

object TableGearBundle {
    private const val BUNDLE = "messages.TableGearBundle"

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return ResourceBundle.getBundle(BUNDLE).getString(key)
            .let { pattern -> if (params.isEmpty()) pattern else java.text.MessageFormat.format(pattern, *params) }
    }
}

