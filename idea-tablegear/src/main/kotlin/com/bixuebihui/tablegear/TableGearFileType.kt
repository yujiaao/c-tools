package com.bixuebihui.tablegear

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import javax.swing.Icon

class TableGearFileType : LanguageFileType(TableGearLanguage) {
    override fun getName(): String = "TableGear Properties"
    
    override fun getDescription(): @NlsContexts.Label String = "TableGear configuration files"
    
    override fun getDefaultExtension(): @NlsSafe String = "properties"
    
    override fun getIcon(): Icon? = TableGearIcons.FILE
    
    companion object {
        @JvmStatic
        val INSTANCE = TableGearFileType()

        private val FILE_PATTERN = Regex("^(tablege|es).*\\.(properties|prop)$", RegexOption.IGNORE_CASE)

        @JvmStatic
        fun isTableGearFile(fileName: String): Boolean {
            return FILE_PATTERN.matches(fileName)
        }
    }
}
