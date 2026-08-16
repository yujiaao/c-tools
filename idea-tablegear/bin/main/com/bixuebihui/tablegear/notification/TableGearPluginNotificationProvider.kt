package com.bixuebihui.tablegear.notification

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationProvider
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.intellij.ui.JBColor
import com.bixuebihui.tablegear.TableGearBundle
import com.bixuebihui.tablegear.TableGearFileType
import java.util.function.Function
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * 当用户编辑 tablegen*.properties 文件时，显示通知提示可以使用 idea-tablegear 插件的功能
 * 
 * 注意：由于插件代码只有在插件已安装时才会运行，所以这个通知主要用于：
 * 1. 提示用户可以使用插件的功能（如配置编辑器）
 * 2. 引导用户使用插件的功能
 */
class TableGearPluginNotificationProvider : EditorNotificationProvider {
    
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?> {
        // 检查是否是 tablegen*.properties 文件
        if (!TableGearFileType.isTableGearFile(file.name)) {
            return Function { null }
        }
        
        // 返回通知组件（提示用户可以使用插件功能）
        return Function { _ ->
            createNotificationPanel(project, file)
        }
    }
    
    private fun createNotificationPanel(project: Project, file: VirtualFile): JPanel {
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(8, 12)
            background = JBColor.background()
        }
        
        val messageLabel = JBLabel(TableGearBundle.message("editorNotification.detected")).apply {
            foreground = JBColor.foreground()
        }
        
        val linkLabel = HyperlinkLabel(TableGearBundle.message("editorNotification.openHint")).apply {
            addHyperlinkListener {
                // 打开配置编辑器
                openConfigEditor(project, file)
            }
        }
        
        panel.add(messageLabel)
        panel.add(linkLabel)
        
        return panel
    }
    
    private fun openConfigEditor(project: Project, file: VirtualFile) {
        // 直接打开 TableGen 配置编辑器对话框
        val dialog = com.bixuebihui.tablegear.ui.TableGenConfigDialog(project, file)
        dialog.show()
    }
}
