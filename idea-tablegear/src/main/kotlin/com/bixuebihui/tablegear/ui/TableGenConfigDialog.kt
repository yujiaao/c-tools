package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.ConfigMode
import com.bixuebihui.tablegear.TableGearBundle
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
        title = TableGearBundle.message("config.dialog.title")
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
        filePathLabel = JLabel(TableGearBundle.message("dialog.file.notSelected"))
        topPanel.add(filePathLabel, BorderLayout.WEST)
        
        val buttonPanel = JPanel()
        modeCombo = JComboBox(
            arrayOf(
                TableGearBundle.message("dialog.mode.database"),
                TableGearBundle.message("dialog.mode.es")
            )
        )
        modeCombo.addActionListener { onConfigModeChanged() }
        buttonPanel.add(JLabel(TableGearBundle.message("dialog.mode.label")))
        buttonPanel.add(modeCombo)
        buttonPanel.add(Box.createHorizontalStrut(15))
        val newDefaultBtn = JButton(TableGearBundle.message("dialog.button.generateDefault"))
        newDefaultBtn.addActionListener { generateDefaultConfig() }
        buttonPanel.add(newDefaultBtn)
        buttonPanel.add(Box.createHorizontalStrut(10))
        val openBtn = JButton(TableGearBundle.message("dialog.button.open"))
        openBtn.addActionListener { openFile() }
        buttonPanel.add(openBtn)
        
        val saveAsBtn = JButton(TableGearBundle.message("dialog.button.saveAs"))
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

        tabbedPane.addTab(TableGearBundle.message("dialog.tab.dataSource"), dataSourceTab)
        tableTabIndex = 1
        tabbedPane.addTab(TableGearBundle.message("dialog.tab.table"), tablePanel)
        tabbedPane.addTab(TableGearBundle.message("dialog.tab.codegen"), codeGenPanel)
        tabbedPane.addTab(TableGearBundle.message("dialog.tab.advanced"), advancedPanel)
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        refreshModeFromModel()

        // 设置对话框大小
        mainPanel.preferredSize = Dimension(900, 700)
        
        // 对话框显示后自动连接数据库（如果配置完整）
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
            if (tableTabIndex >= 0 && tableTabIndex < tabbedPane.tabCount && tabbedPane.getTitleAt(tableTabIndex) == TableGearBundle.message("dialog.tab.table")) {
                tabbedPane.removeTabAt(tableTabIndex)
                tableTabIndex = -1
            }
        } else {
            if (tableTabIndex < 0) {
                tableTabIndex = 1
                tabbedPane.insertTab(TableGearBundle.message("dialog.tab.table"), null, tablePanel, null, tableTabIndex)
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
            if (tableTabIndex >= 0 && tableTabIndex < tabbedPane.tabCount && tabbedPane.getTitleAt(tableTabIndex) == TableGearBundle.message("dialog.tab.table")) {
                tabbedPane.removeTabAt(tableTabIndex)
                tableTabIndex = -1
            }
        } else {
            if (tableTabIndex < 0) {
                tableTabIndex = 1
                tabbedPane.insertTab(TableGearBundle.message("dialog.tab.table"), null, tablePanel, null, tableTabIndex)
            }
        }
    }

    private fun generateDefaultConfig() {
        val options = arrayOf(
            TableGearBundle.message("dialog.defaultConfig.option.database"),
            TableGearBundle.message("dialog.defaultConfig.option.es"),
            TableGearBundle.message("dialog.defaultConfig.option.cancel")
        )
        val choice = Messages.showDialog(
            project,
            TableGearBundle.message("dialog.defaultConfig.selectType.message"),
            TableGearBundle.message("dialog.defaultConfig.selectType.title"),
            options,
            0,
            Messages.getQuestionIcon()
        )
        if (choice < 0 || choice == 2) return
        val isEs = (choice == 1)
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            .apply {
                withFileFilter { it.extension == "properties" || it.extension == "prop" }
                withTitle(
                    if (isEs) TableGearBundle.message("dialog.defaultConfig.saveTitle.es")
                    else TableGearBundle.message("dialog.defaultConfig.saveTitle.db")
                )
            }
        val defaultFileName = if (isEs) TableGearBundle.message("dialog.defaultConfig.defaultFileName.es")
        else TableGearBundle.message("dialog.defaultConfig.defaultFileName.db")
        FileChooser.chooseFile(descriptor, project, null)?.let { selected ->
            val file = resolveTargetFile(selected, defaultFileName) ?: run {
                Messages.showErrorDialog(
                    project,
                    TableGearBundle.message("dialog.defaultConfig.createFailed", selected.path),
                    TableGearBundle.message("notification.common.errorTitle")
                )
                return@let
            }
            val props = DefaultConfigTemplates.forMode(isEs)
            val configMode = if (isEs) ConfigMode.ELASTICSEARCH else ConfigMode.DATABASE
            val sortedKeys = getSortedPropertyKeys(props, configMode)
            val content = buildString {
                append(
                    if (isEs) TableGearBundle.message("dialog.file.header.comment.es")
                    else TableGearBundle.message("dialog.file.header.comment.db")
                )
                append("\n")
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
                    ?.createNotification(
                        TableGearBundle.message("dialog.defaultConfig.generated", file.path),
                        NotificationType.INFORMATION
                    )
                    ?.notify(project)
                val load = Messages.showYesNoDialog(
                    project,
                    TableGearBundle.message("dialog.defaultConfig.openPrompt"),
                    TableGearBundle.message("dialog.defaultConfig.openPrompt.title"),
                    TableGearBundle.message("dialog.defaultConfig.openPrompt.open"),
                    TableGearBundle.message("dialog.defaultConfig.openPrompt.skip"),
                    Messages.getQuestionIcon()
                )
                if (load == Messages.YES) {
                    loadFile(file)
                }
            } catch (e: Exception) {
                Messages.showErrorDialog(
                    project,
                    TableGearBundle.message("dialog.defaultConfig.writeFailed", e.message ?: "unknown"),
                    TableGearBundle.message("notification.common.errorTitle")
                )
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
        val generateCodeAction = object : AbstractAction(TableGearBundle.message("dialog.button.generateCode")) {
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
        esPanel.updateToModel()
        codeGenPanel.updateToModel()
        tablePanel.updateToModel()
        advancedPanel.updateToModel()

        val file = currentFile
        if (file == null) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("notification.generateCode.requireFile"),
                TableGearBundle.message("notification.generateCode.failed.title")
            )
            return
        }

        // 启用智能合并（与 tablegen:gen-merge 行为一致）
        model.setProperty("enable_merge", "yes")
        saveFile(file)

        val configPath = file.path
        val proj = project

        ProgressManager.getInstance().run(object : Task.Backgroundable(proj, TableGearBundle.message("notification.generateCode.progressTitle"), true) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                indicator.text = TableGearBundle.message("notification.generateCode.running")
                try {
                    TableGen().run(configPath)
                    javax.swing.SwingUtilities.invokeLater {
                        NotificationGroupManager.getInstance()
                            .getNotificationGroup("TableGear.Notification")
                            ?.createNotification(TableGearBundle.message("notification.generateCode.success"), NotificationType.INFORMATION)
                            ?.notify(proj)
                    }
                } catch (t: Throwable) {
                    javax.swing.SwingUtilities.invokeLater {
                        Messages.showErrorDialog(
                            proj,
                            TableGearBundle.message("notification.generateCode.error", t.message ?: "unknown", t.stackTraceToString().take(800)),
                            TableGearBundle.message("notification.generateCode.progressTitle")
                        )
                    }
                }
            }
        })
    }

    override fun doOKAction() {
        // 更新模型
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
                withTitle(TableGearBundle.message("dialog.file.chooseTitle"))
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
            filePathLabel.text = TableGearBundle.message("dialog.file.current", file.path)
            
            // 更新所有面板
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
                TableGearBundle.message("notification.loadConfig.success"),
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
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("notification.loadConfig.error", e.message ?: "unknown"),
                TableGearBundle.message("notification.common.errorTitle")
            )
        }
    }
    
    private fun saveFile(file: VirtualFile) {
        try {
            val props = model.saveToProperties()
            val outputStream = file.getOutputStream(this)
            OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                // 自定义保存逻辑，避免 Properties.store() 自动转义冒号
                writer.append(TableGearBundle.message("dialog.file.header.comment.db"))
                writer.append("\n")
                
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
            val notificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("TableGear.Notification")
            notificationGroup?.createNotification(
                TableGearBundle.message("notification.saveConfig.success"),
                NotificationType.INFORMATION
            )?.notify(project)
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("notification.saveConfig.error", e.message ?: "unknown"),
                TableGearBundle.message("notification.common.errorTitle")
            )
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
                withTitle(TableGearBundle.message("dialog.file.saveTitle"))
            }
        val defaultName = currentFile?.name ?: "tablegen.properties"
        FileChooser.chooseFile(descriptor, project, null)?.let { selected ->
            val file = resolveTargetFile(selected, defaultName) ?: run {
                Messages.showErrorDialog(
                    project,
                    TableGearBundle.message("dialog.defaultConfig.createFailed", selected.path),
                    TableGearBundle.message("notification.common.errorTitle")
                )
                return@let
            }
            saveFile(file)
            currentFile = file
            filePathLabel.text = TableGearBundle.message("dialog.file.current", file.path)
        }
    }

    /**
     * 若用户选中的是目录，则在该目录下创建/使用指定文件名的文件并返回；
     * 若选中的已是文件则直接返回。这样可支持“选目录 + 默认文件名”的写入。
     */
    private fun resolveTargetFile(selected: VirtualFile, defaultFileName: String): VirtualFile? {
        if (!selected.isDirectory) return selected
        var target: VirtualFile? = null
        ApplicationManager.getApplication().runWriteAction {
            val existing = selected.findChild(defaultFileName)
            target = if (existing != null && !existing.isDirectory) existing
            else selected.createChildData(this@TableGenConfigDialog, defaultFileName)
        }
        return target
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
