package com.bixuebihui.tablegear.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.swing.*

/**
 * TableGen配置编辑器对话框
 */
class TableGenConfigDialog(
    private val project: Project?,
    private val initialFile: VirtualFile? = null
) : DialogWrapper(project) {
    
    private val model = TableGenConfigModel()
    private var currentFile: VirtualFile? = initialFile
    
    // 配置面板
    private lateinit var databasePanel: DatabaseConfigPanel
    private lateinit var codeGenPanel: CodeGenConfigPanel
    private lateinit var tablePanel: TableConfigPanel
    private lateinit var advancedPanel: AdvancedConfigPanel
    
    // UI组件
    private lateinit var tabbedPane: JBTabbedPane
    private lateinit var filePathLabel: JLabel
    
    init {
        title = "TableGen 配置编辑器"
        init()
        
        // 如果提供了初始文件，加载它
        if (initialFile != null) {
            loadFile(initialFile)
        }
    }
    
    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        
        // 创建文件路径显示和操作按钮
        val topPanel = JPanel(BorderLayout())
        filePathLabel = JLabel("未选择文件")
        topPanel.add(filePathLabel, BorderLayout.WEST)
        
        val buttonPanel = JPanel()
        val openBtn = JButton("打开文件...")
        openBtn.addActionListener { openFile() }
        buttonPanel.add(openBtn)
        
        val saveAsBtn = JButton("另存为...")
        saveAsBtn.addActionListener { saveAsFile() }
        buttonPanel.add(saveAsBtn)
        
        topPanel.add(buttonPanel, BorderLayout.EAST)
        topPanel.border = JBUI.Borders.empty(5)
        
        mainPanel.add(topPanel, BorderLayout.NORTH)
        
        // 创建标签页
        tabbedPane = JBTabbedPane()
        
        databasePanel = DatabaseConfigPanel(model)
        codeGenPanel = CodeGenConfigPanel(model)
        tablePanel = TableConfigPanel(model, project)
        advancedPanel = AdvancedConfigPanel(model)
        
        tabbedPane.addTab("数据库配置", databasePanel)
        tabbedPane.addTab("代码生成配置", codeGenPanel)
        tabbedPane.addTab("表配置", tablePanel)
        tabbedPane.addTab("高级配置", advancedPanel)
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        
        // 设置对话框大小
        mainPanel.preferredSize = Dimension(900, 700)
        
        return mainPanel
    }
    
    override fun doOKAction() {
        // 更新模型
        databasePanel.updateToModel()
        codeGenPanel.updateToModel()
        tablePanel.updateToModel()
        advancedPanel.updateToModel()
        
        // 清理资源
        tablePanel.dispose()
        
        // 如果当前有文件，保存到该文件
        if (currentFile != null) {
            saveFile(currentFile!!)
        }
        
        super.doOKAction()
    }
    
    override fun doCancelAction() {
        // 清理资源
        tablePanel.dispose()
        super.doCancelAction()
    }
    
    private fun openFile() {
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
            .apply {
                withFileFilter { it.extension == "properties" || it.extension == "prop" }
                withTitle("选择 TableGen 配置文件")
            }
        
        FileChooser.chooseFiles(descriptor, project, null) { files ->
            if (files.isNotEmpty()) {
                loadFile(files[0])
            }
        }
    }
    
    private fun loadFile(file: VirtualFile) {
        try {
            val props = Properties()
            file.inputStream.use { input ->
                InputStreamReader(input, StandardCharsets.UTF_8).use { reader ->
                    props.load(reader)
                }
            }
            
            model.loadFromProperties(props)
            currentFile = file
            filePathLabel.text = "文件: ${file.path}"
            
            // 更新所有面板
            databasePanel.updateFromModel()
            codeGenPanel.updateFromModel()
            tablePanel.updateFromModel()
            advancedPanel.updateFromModel()
            
            Messages.showInfoMessage(project, "配置文件加载成功！", "成功")
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "加载配置文件失败: ${e.message}", "错误")
        }
    }
    
    private fun saveFile(file: VirtualFile) {
        try {
            val props = model.saveToProperties()
            val outputStream = file.getOutputStream(this)
            OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                props.store(writer, "TableGen Configuration File")
            }
            Messages.showInfoMessage(project, "配置文件保存成功！", "成功")
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "保存配置文件失败: ${e.message}", "错误")
        }
    }
    
    private fun saveAsFile() {
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            .apply {
                withFileFilter { it.extension == "properties" || it.extension == "prop" }
                withTitle("保存 TableGen 配置文件")
            }
        
        FileChooser.chooseFile(descriptor, project, null)?.let { file ->
            saveFile(file)
            currentFile = file
            filePathLabel.text = "文件: ${file.path}"
        }
    }
    
    /**
     * 获取当前配置模型（用于外部访问）
     */
    fun getModel(): TableGenConfigModel = model
    
    /**
     * 获取当前文件
     */
    fun getCurrentFile(): VirtualFile? = currentFile
}
