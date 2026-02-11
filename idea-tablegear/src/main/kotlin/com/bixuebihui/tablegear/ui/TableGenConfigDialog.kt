package com.bixuebihui.tablegear.ui

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notification
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import com.bixuebihui.tablegear.ConfigMode
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegen.TableGen
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
    
    fun getTablePanel(): TableConfigPanel = tablePanel
    
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
        advancedPanel = AdvancedConfigPanel(model, project, tablePanel)
        
        tabbedPane.addTab("数据库配置", databasePanel)
        tabbedPane.addTab("代码生成配置", codeGenPanel)
        tabbedPane.addTab("表配置", tablePanel)
        tabbedPane.addTab("高级配置", advancedPanel)
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        
        // 设置对话框大小
        mainPanel.preferredSize = Dimension(900, 700)
        
        // 对话框显示后自动连接数据库（如果配置完整）
        javax.swing.SwingUtilities.invokeLater {
            tryAutoConnect()
        }
        
        return mainPanel
    }
    
    /**
     * 尝试自动连接数据库（如果配置完整）
     */
    private fun tryAutoConnect() {
        // 检查数据库配置是否完整
        val className = model.getProperty("className")
        val dburl = model.getProperty("dburl")
        
        if (!className.isNullOrBlank() && !dburl.isNullOrBlank()) {
            // 配置完整，自动连接
            tablePanel.autoConnectIfConfigured()
        }
    }
    
    override fun createActions(): Array<Action> {
        val generateCodeAction = object : AbstractAction("生成代码") {
            override fun actionPerformed(e: java.awt.event.ActionEvent?) {
                runGenerateCode()
            }
        }
        return arrayOf(generateCodeAction, getOKAction(), getCancelAction())
    }

    /**
     * 用当前配置执行代码生成（相当于 Maven tablegen:gen-merge：启用 enable_merge 后执行生成）
     */
    private fun runGenerateCode() {
        // 先同步各面板到模型
        databasePanel.updateToModel()
        codeGenPanel.updateToModel()
        tablePanel.updateToModel()
        advancedPanel.updateToModel()

        val file = currentFile
        if (file == null) {
            Messages.showErrorDialog(project, "请先保存或打开一个 TableGen 配置文件后再生成代码。", "无法生成代码")
            return
        }

        // 启用智能合并（与 tablegen:gen-merge 行为一致）
        model.setProperty("enable_merge", "yes")
        saveFile(file)

        val configPath = file.path
        val proj = project

        ProgressManager.getInstance().run(object : Task.Backgroundable(proj, "TableGen 生成代码", true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                indicator.text = "正在根据当前配置生成代码..."
                try {
                    TableGen().run(configPath)
                    javax.swing.SwingUtilities.invokeLater {
                        NotificationGroupManager.getInstance()
                            .getNotificationGroup("TableGear.Notification")
                            ?.createNotification("代码生成完成", NotificationType.INFORMATION)
                            ?.notify(proj)
                    }
                } catch (t: Throwable) {
                    javax.swing.SwingUtilities.invokeLater {
                        Messages.showErrorDialog(
                            proj,
                            "代码生成失败: ${t.message}\n\n${t.stackTraceToString().take(800)}",
                            "TableGen 生成代码"
                        )
                    }
                }
            }
        })
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
            
            // 显示 toast 通知而不是弹窗
            val notificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("TableGear.Notification")
            notificationGroup?.createNotification(
                "配置文件加载成功！",
                NotificationType.INFORMATION
            )?.notify(project)
            
            // 如果配置完整，自动连接数据库
            javax.swing.SwingUtilities.invokeLater {
                tryAutoConnect()
            }
            
            // 如果配置完整，自动连接数据库
            javax.swing.SwingUtilities.invokeLater {
                tryAutoConnect()
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "加载配置文件失败: ${e.message}", "错误")
        }
    }
    
    private fun saveFile(file: VirtualFile) {
        try {
            val props = model.saveToProperties()
            val outputStream = file.getOutputStream(this)
            OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                // 自定义保存逻辑，避免 Properties.store() 自动转义冒号
                writer.append("# TableGen Configuration File\n")
                
                // 获取配置模式
                val configMode = model.getConfigMode()
                
                // 按分组排序保存配置项
                val sortedKeys = getSortedPropertyKeys(props, configMode)
                
                sortedKeys.forEach { key ->
                    var value = props.getProperty(key)
                    if (value != null) {
                        // 移除 Properties 可能添加的转义反斜杠（如 \: 变为 :）
                        value = value.replace("\\:", ":")
                        value = value.replace("\\=", "=")
                        value = value.replace("\\ ", " ")
                        value = value.replace("\\!", "!")
                        value = value.replace("\\#", "#")
                        // 但保留真正的反斜杠（\\ 保持不变）
                        value = value.replace("\\\\", "\\")
                        // 手动写入，确保冒号不被转义
                        writer.append("$key=$value\n")
                    }
                }
            }
            // 显示 toast 通知而不是弹窗
            val notificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("TableGear.Notification")
            notificationGroup?.createNotification(
                "配置文件保存成功！",
                NotificationType.INFORMATION
            )?.notify(project)
        } catch (e: Exception) {
            Messages.showErrorDialog(project, "保存配置文件失败: ${e.message}", "错误")
        }
    }
    
    /**
     * 获取按高亮配色分组排序的配置项键列表
     */
    private fun getSortedPropertyKeys(props: Properties, configMode: ConfigMode): List<String> {
        val allKeys = props.stringPropertyNames()
        val propertyMap = TableGearProperties.PROPERTY_MAP
        val usedKeys = mutableSetOf<String>()
        val sortedKeys = mutableListOf<String>()
        
        // 数据库相关配置项
        val databaseKeys = setOf(
            "className", "dburl", "username", "password", "schema", "table_owner", 
            "catalog", "projectname", "alias", "add_prefix", "remove_prefix"
        )
        
        // 敏感配置项
        val sensitiveKeys = setOf("password", "username")
        
        // 1. PACKAGE_NAME_KEY - package_name
        if (allKeys.contains("package_name")) {
            sortedKeys.add("package_name")
            usedKeys.add("package_name")
        }
        
        // 2. VIEW_LIST_KEY - view_list
        if (allKeys.contains("view_list")) {
            sortedKeys.add("view_list")
            usedKeys.add("view_list")
        }
        
        // 3. PATH_KEY - 所有 PATH 类型的配置项
        TableGearProperties.ALL_PROPERTIES
            .filter { it.valueType == com.bixuebihui.tablegear.ValueType.PATH }
            .forEach { prop ->
                if (allKeys.contains(prop.key) && !usedKeys.contains(prop.key)) {
                    sortedKeys.add(prop.key)
                    usedKeys.add(prop.key)
                }
            }
        
        // 4. BOOLEAN_KEY - 所有 BOOLEAN 类型的配置项
        TableGearProperties.ALL_PROPERTIES
            .filter { it.valueType == com.bixuebihui.tablegear.ValueType.BOOLEAN }
            .forEach { prop ->
                if (allKeys.contains(prop.key) && !usedKeys.contains(prop.key)) {
                    sortedKeys.add(prop.key)
                    usedKeys.add(prop.key)
                }
            }
        
        // 5. LIST_KEY - 所有 LIST 类型的配置项（除了 view_list）
        TableGearProperties.ALL_PROPERTIES
            .filter { it.valueType == com.bixuebihui.tablegear.ValueType.LIST && it.key != "view_list" }
            .forEach { prop ->
                if (allKeys.contains(prop.key) && !usedKeys.contains(prop.key)) {
                    sortedKeys.add(prop.key)
                    usedKeys.add(prop.key)
                }
            }
        
        // 6. DATABASE_KEY - 数据库相关的配置项
        databaseKeys.forEach { key ->
            if (allKeys.contains(key) && !usedKeys.contains(key)) {
                sortedKeys.add(key)
                usedKeys.add(key)
            }
        }
        
        // 7. SENSITIVE_KEY - 敏感配置项（如果还没添加）
        sensitiveKeys.forEach { key ->
            if (allKeys.contains(key) && !usedKeys.contains(key)) {
                sortedKeys.add(key)
                usedKeys.add(key)
            }
        }
        
        // 8. KEY - 其他普通配置项（按照定义顺序）
        TableGearProperties.ALL_PROPERTIES.forEach { prop ->
            if (allKeys.contains(prop.key) && !usedKeys.contains(prop.key)) {
                sortedKeys.add(prop.key)
                usedKeys.add(prop.key)
            }
        }
        
        // 9. 未定义的配置项（按字母顺序）
        val undefinedKeys = allKeys.filter { !usedKeys.contains(it) }.sorted()
        sortedKeys.addAll(undefinedKeys)
        
        return sortedKeys
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
