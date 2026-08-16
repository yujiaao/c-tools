package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearBundle
import com.bixuebihui.tablegear.TableGearIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 * 打开TableGen配置编辑器的Action
 */
class OpenTableGenConfigAction : AnAction(
    TableGearBundle.message("action.openConfigEditor.text"),
    TableGearBundle.message("action.openConfigEditor.description"),
    null
) {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val selectedFile = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE)
        
        // 如果选中的是tablegen*.properties文件，直接打开它
        val fileToOpen = if (selectedFile != null && isTableGenFile(selectedFile)) {
            selectedFile
        } else {
            null
        }
        
        val dialog = TableGenConfigDialog(project, fileToOpen)
        dialog.show()
    }
    
    override fun update(e: AnActionEvent) {
        // Action始终可用
        e.presentation.isEnabledAndVisible = true
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

