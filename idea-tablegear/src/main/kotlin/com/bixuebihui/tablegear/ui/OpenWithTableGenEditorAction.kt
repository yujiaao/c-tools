package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile

/**
 * 在文件上下文菜单中打开TableGen配置编辑器的Action
 */
class OpenWithTableGenEditorAction : AnAction("用 TableGen 编辑器打开", "使用 TableGen 可视化编辑器打开配置文件", TableGearIcons.FILE) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        
        if (selectedFile != null && isTableGenFile(selectedFile)) {
            val dialog = TableGenConfigDialog(project, selectedFile)
            dialog.show()
        }
    }
    
    override fun update(e: AnActionEvent) {
        val selectedFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val isEnabled = selectedFile != null && isTableGenFile(selectedFile)
        e.presentation.isEnabledAndVisible = isEnabled
        if (e.presentation.icon == null) {
            e.presentation.icon = TableGearIcons.FILE
        }
    }
    
    private fun isTableGenFile(file: VirtualFile): Boolean {
        val name = file.name.lowercase()
        return (name.startsWith("tablegen") || name.startsWith("es")) && 
               (name.endsWith(".properties") || name.endsWith(".prop"))
    }
}
