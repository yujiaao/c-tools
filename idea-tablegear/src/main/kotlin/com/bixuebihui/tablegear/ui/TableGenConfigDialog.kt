package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.ConfigMode
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegen.TableGen
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
    private lateinit var esPanel: EsConfigPanel
    private lateinit var codeGenPanel: CodeGenConfigPanel
    private lateinit var tablePanel: TableConfigPanel
    private lateinit var advancedPanel: AdvancedConfigPanel
    
    fun getTablePanel(): TableConfigPanel = tablePanel
    
    // 数据源配置卡片（数据库 / ES 切换）
    private lateinit var dataSourceCardPanel: JPanel
    private val DATA_SOURCE_CARD_DB = "database"
    private val DATA_SOURCE_CARD_ES = "es"
    
    // UI组件
    private lateinit var tabbedPane: JBTabbedPane
    private lateinit var filePathLabel: JLabel
    private lateinit var modeCombo: JComboBox<String>
    private var tableTabIndex: Int = -1  // 表配置标签页索引，ES 模式下会移除
    
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
        modeCombo = JComboBox(arrayOf("数据库", "ES配置"))
        modeCombo.addActionListener { onConfigModeChanged() }
        buttonPanel.add(JLabel("配置模式: "))
        buttonPanel.add(modeCombo)
        buttonPanel.add(Box.createHorizontalStrut(15))
        val newDefaultBtn = JButton("生成默认配置...")
        newDefaultBtn.addActionListener { generateDefaultConfig() }
        buttonPanel.add(newDefaultBtn)
        buttonPanel.add(Box.createHorizontalStrut(10))
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
        esPanel = EsConfigPanel(model)
        codeGenPanel = CodeGenConfigPanel(model)
        tablePanel = TableConfigPanel(model, project)
        advancedPanel = AdvancedConfigPanel(model, project, tablePanel)
        
        dataSourceCardPanel = JPanel(CardLayout())
        dataSourceCardPanel.add(databasePanel, DATA_SOURCE_CARD_DB)
        dataSourceCardPanel.add(esPanel, DATA_SOURCE_CARD_ES)
        val dataSourceTab = JPanel(BorderLayout())
        dataSourceTab.add(dataSourceCardPanel, BorderLayout.CENTER)
        
        tabbedPane.addTab("数据源配置", dataSourceTab)
        tableTabIndex = 1
        tabbedPane.addTab("表配置", tablePanel)
        tabbedPane.addTab("代码生成配置", codeGenPanel)
        tabbedPane.addTab("高级配置", advancedPanel)
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        refreshModeFromModel()
        
        mainPanel.preferredSize = Dimension(900, 700)
        
        javax.swing.SwingUtilities.invokeLater {
            tryAutoConnect()
        }
        
        return mainPanel
    }
    
    private fun refreshModeFromModel() {
        val isEs = model.getConfigMode() == ConfigMode.ELASTICSEARCH
        modeCombo.selectedIndex = if (isEs) 1 else 0
        (dataSourceCardPanel.layout as CardLayout).show(dataSourceCardPanel, if (isEs) DATA_SOURCE_CARD_ES else DATA_SOURCE_CARD_DB)
        if (isEs) {
            if (tableTabIndex >= 0 && tableTabIndex < tabbedPane.tabCount && tabbedPane.getTitleAt(tableTabIndex) == "表配置") {
                tabbedPane.removeTabAt(tableTabIndex)
                tableTabIndex = -1
            }
        } else {
            if (tableTabIndex < 0) {
                tableTabIndex = 1
                tabbedPane.insertTab("表配置", null, tablePanel, null, tableTabIndex)
            }
        }
    }
    
    private fun onConfigModeChanged() {
        val isEs = modeCombo.selectedIndex == 1
        databasePanel.updateToModel()
        esPanel.updateToModel()
        model.setProperty("type", if (isEs) "elasticsearch_mapping" else "database")
        databasePanel.updateFromModel()
        esPanel.updateFromModel()
        (dataSourceCardPanel.layout as CardLayout).show(dataSourceCardPanel, if (isEs) DATA_SOURCE_CARD_ES else DATA_SOURCE_CARD_DB)
        if (isEs) {
            if (tableTabIndex >= 0 && tableTabIndex < tabbedPane.tabCount && tabbedPane.getTitleAt(tableTabIndex) == "表配置") {
                tabbedPane.removeTabAt(tableTabIndex)
                tableTabIndex = -1
            }
        } else {
            if (tableTabIndex < 0) {
                tableTabIndex = 1
                tabbedPane.insertTab("表配置", null, tablePanel, null, tableTabIndex)
            }
        }
    }
    
    private fun generateDefaultConfig() {
        val options = arrayOf("数据库配置", "ES配置", "取消")
        val choice = Messages.showDialog(
            project,
            "选择要生成的默认配置类型：",
            "生成默认配置",
            options,
            0,
            Messages.getQuestionIcon()
        )
        if (choice < 0 || choice == 2) return
        val isEs = (choice == 1)
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            .apply {
                withFileFilter { it.extension == "properties" || it.extension == "prop" }
                withTitle(if (isEs) "保存 ES 默认配置文件" else "保存数据库默认配置文件")
            }
        FileChooser.chooseFile(descriptor, project, null)?.let { file ->
            val props = DefaultConfigTemplates.forMode(isEs)
            val configMode = if (isEs) ConfigMode.ELASTICSEARCH else ConfigMode.DATABASE
            val sortedKeys = getSortedPropertyKeys(props, configMode)
            val content = buildString {
                append(if (isEs) "# TableGear ES Configuration File\n" else "# TableGen Configuration File\n")
                sortedKeys.forEach { key ->
                    val value = props.getProperty(key)
                    if (value != null) {
                        append("$key=$value\n")
                    }
                }
            }
            try {
                ApplicationManager.getApplication().runWriteAction {
                    OutputStreamWriter(file.getOutputStream(this@TableGenConfigDialog), StandardCharsets.UTF_8).use { writer ->
                        writer.write(content)
                    }
                }
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("TableGear.Notification")
                    ?.createNotification("默认配置文件已生成：${file.path}", NotificationType.INFORMATION)
                    ?.notify(project)
                val load = Messages.showYesNoDialog(project, "是否在编辑器中打开该文件？", "默认配置已生成", "打开", "不打开", Messages.getQuestionIcon())
                if (load == Messages.YES) {
                    loadFile(file)
                }
            } catch (e: Exception) {
                Messages.showErrorDialog(project, "写入配置文件失败: ${e.message}", "错误")
            }
        }
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
        databasePanel.updateToModel()
        esPanel.updateToModel()
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
        databasePanel.updateToModel()
        esPanel.updateToModel()
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
            
            databasePanel.updateFromModel()
            esPanel.updateFromModel()
            codeGenPanel.updateFromModel()
            tablePanel.updateFromModel()
            advancedPanel.updateFromModel()
            refreshModeFromModel()
            
            // 显示 toast 通知而不是弹窗
            val notificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("TableGear.Notification")
            notificationGroup?.createNotification(
                "配置文件加载成功！",
                NotificationType.INFORMATION
            )?.notify(project)
            
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
            val configMode = model.getConfigMode()
            val sortedKeys = getSortedPropertyKeys(props, configMode)
            val content = buildString {
                append("# TableGen Configuration File\n")
                sortedKeys.forEach { key ->
                    var value = props.getProperty(key)
                    if (value != null) {
                        value = value.replace("\\:", ":")
                        value = value.replace("\\=", "=")
                        value = value.replace("\\ ", " ")
                        value = value.replace("\\!", "!")
                        value = value.replace("\\#", "#")
                        value = value.replace("\\\\", "\\")
                        append("$key=$value\n")
                    }
                }
            }
            // 写入 VFS 必须在 runWriteAction 内执行
            ApplicationManager.getApplication().runWriteAction {
                OutputStreamWriter(file.getOutputStream(this@TableGenConfigDialog), StandardCharsets.UTF_8).use { writer ->
                    writer.write(content)
                }
            }
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
        
        // 1b. ES 模式时 type/url/index_list 等靠前
        if (configMode == ConfigMode.ELASTICSEARCH || configMode == ConfigMode.JSON) {
            listOf("type", "url", "index_list", "primary_key_fields", "alias", "username", "password").forEach { key ->
                if (allKeys.contains(key) && !usedKeys.contains(key)) {
                    sortedKeys.add(key)
                    usedKeys.add(key)
                }
            }
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
