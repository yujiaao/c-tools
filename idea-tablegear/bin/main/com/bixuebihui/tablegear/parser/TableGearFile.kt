package com.bixuebihui.tablegear.parser

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.bixuebihui.tablegear.TableGearFileType
import com.bixuebihui.tablegear.TableGearLanguage

class TableGearFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TableGearLanguage) {
    override fun getFileType(): FileType = TableGearFileType.INSTANCE
    
    override fun toString(): String = "TableGear Properties File"
}

