package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearBundle
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notification
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

/**
 * 表配置面板
 */
class TableConfigPanel(
    private val model: TableGenConfigModel,
    private val project: Project? = null
) : JPanel() {
    
    private lateinit var tableListArea: JTextArea
    private lateinit var excludeTableListArea: JTextArea
    private lateinit var viewListArea: JTextArea
    
    private lateinit var connectDbButton: JButton
    private lateinit var selectTablesButton: JButton
    private lateinit var statusLabel: JLabel
    
    private var dbHelper: DatabaseConnectionHelper? = null
    
    init {
        initializePanel()
    }
    
    private fun initializePanel() {
        tableListArea = JBTextArea(5, 50)
        tableListArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        excludeTableListArea = JBTextArea(5, 50)
        excludeTableListArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        viewListArea = JBTextArea(8, 50)
        viewListArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        // 数据库连接按钮
        connectDbButton = JButton(TableGearBundle.message("tableConfig.connectDb"))
        connectDbButton.addActionListener { connectToDatabase() }
        
        selectTablesButton = JButton(TableGearBundle.message("tableConfig.selectTables"))
        selectTablesButton.isEnabled = false
        selectTablesButton.addActionListener { selectTables() }
        
        statusLabel = JBLabel(TableGearBundle.message("tableConfig.status.disconnected"))
        statusLabel.font = statusLabel.font.deriveFont(Font.ITALIC, 11f)
        
        val buttonPanel = JPanel()
        buttonPanel.add(connectDbButton)
        buttonPanel.add(selectTablesButton)
        buttonPanel.add(statusLabel)
        
        val form = FormBuilder.createFormBuilder()
            .addComponent(buttonPanel)
            .addLabeledComponent(JBLabel(TableGearBundle.message("tableConfig.label.tableList")), JScrollPane(tableListArea))
            .addTooltip(TableGearBundle.message("tableConfig.tooltip.tableList"))
            .addLabeledComponent(JBLabel(TableGearBundle.message("tableConfig.label.excludeTableList")), JScrollPane(excludeTableListArea))
            .addTooltip(TableGearBundle.message("tableConfig.tooltip.excludeTableList"))
            .addLabeledComponent(JBLabel(TableGearBundle.message("tableConfig.label.viewList")), JScrollPane(viewListArea))
            .addTooltip(TableGearBundle.message("tableConfig.tooltip.viewList"))
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        layout = java.awt.BorderLayout()
        add(form, java.awt.BorderLayout.CENTER)
    }
    
    /**
     * 自动连接数据库（如果配置完整）
     * @return true 如果配置完整并开始连接，false 如果配置不完整
     */
    fun autoConnectIfConfigured(): Boolean {
        val className = model.getProperty("className")
        val dburl = model.getProperty("dburl")
        
        if (className.isNullOrBlank() || dburl.isNullOrBlank()) {
            return false
        }
        
        // 配置完整，自动连接
        connectToDatabase()
        return true
    }
    
    private fun connectToDatabase() {
        // 检查数据库配置是否完整
        val className = model.getProperty("className")
        val dburl = model.getProperty("dburl")
        
        if (className.isNullOrBlank() || dburl.isNullOrBlank()) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("tableConfig.incomplete.message"),
                TableGearBundle.message("tableConfig.incomplete.title")
            )
            return
        }
        
        // 更新UI状态
        connectDbButton.isEnabled = false
        connectDbButton.text = TableGearBundle.message("tableConfig.connecting")
        statusLabel.text = TableGearBundle.message("tableConfig.status.connecting")
        
        // 关闭之前的连接
        dbHelper?.close()
        
        // 在后台线程中执行数据库连接
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, TableGearBundle.message("tableConfig.task.connect"), false) {
            override fun run(indicator: ProgressIndicator) {
                indicator.isIndeterminate = true
                indicator.text = TableGearBundle.message("tableConfig.status.connecting")
                
                try {
                    // 创建新的连接
                    indicator.text = TableGearBundle.message("tableConfig.status.creatingConnection")
                    dbHelper = DatabaseConnectionHelper(model)
                    
                    indicator.text = TableGearBundle.message("tableConfig.status.testingConnection")
                    dbHelper!!.testConnection() // 如果成功会继续，失败会抛出异常
                    
                    indicator.text = TableGearBundle.message("tableConfig.status.connected")
                    
                    // 连接成功，在 UI 线程中更新界面
                    SwingUtilities.invokeLater {
                        statusLabel.text = TableGearBundle.message("tableConfig.status.connected")
                        selectTablesButton.isEnabled = true
                        connectDbButton.isEnabled = true
                        connectDbButton.text = TableGearBundle.message("tableConfig.connectDb")
                        
                        // 显示 toast 通知而不是弹窗
                        val notificationGroup = NotificationGroupManager.getInstance()
                            .getNotificationGroup("TableGear.Notification")
                        notificationGroup?.createNotification(
                            TableGearBundle.message("tableConfig.notification.connectSuccess"),
                            NotificationType.INFORMATION
                        )?.notify(project)
                    }
                } catch (e: Exception) {
                    val cause = e.cause ?: e
                    val driverClass = model.getProperty("className")?.trim().orEmpty()
                    val isDriverNotFound = cause is ClassNotFoundException ||
                        (e.message?.contains(TableGearBundle.message("dbHelper.driverLoadFailedPrefix")) == true && driverClass.isNotEmpty())
                    val canDownload = driverClass.isNotBlank() && JdbcDriverDownloadService.isDownloadable(driverClass)

                    if (isDriverNotFound && canDownload) {
                        val downloadInfo = JdbcDriverDownloadService.getDownloadInfo(driverClass) ?: ""
                        val dirPath = JdbcDriverDownloadService.getDriversDirPath()
                        val choiceHolder = intArrayOf(-1)
                        SwingUtilities.invokeAndWait {
                            choiceHolder[0] = Messages.showDialog(
                                project,
                                TableGearBundle.message("tableConfig.driverNotFoundPrompt", driverClass, downloadInfo, dirPath),
                                TableGearBundle.message("tableConfig.driverDownloadTitle"),
                                arrayOf(TableGearBundle.message("common.download"), TableGearBundle.message("common.cancel")),
                                0,
                                Messages.getQuestionIcon()
                            )
                        }
                        if (choiceHolder[0] == 0) {
                            ProgressManager.getInstance().run(object : Task.Backgroundable(project, TableGearBundle.message("tableConfig.driverDownloading"), true) {
                                override fun run(indicator: ProgressIndicator) {
                                    try {
                                        JdbcDriverDownloadService.downloadDriver(driverClass, indicator)
                                        SwingUtilities.invokeLater {
                                            statusLabel.text = TableGearBundle.message("tableConfig.status.driverDownloaded")
                                            connectToDatabase()
                                        }
                                    } catch (downloadEx: Exception) {
                                        SwingUtilities.invokeLater {
                                            statusLabel.text = TableGearBundle.message("tableConfig.status.downloadFailed")
                                            connectDbButton.isEnabled = true
                                            connectDbButton.text = TableGearBundle.message("tableConfig.connectDb")
                                            Messages.showErrorDialog(
                                                project,
                                                TableGearBundle.message("tableConfig.driverDownloadFailed", downloadEx.message ?: "unknown", dirPath),
                                                TableGearBundle.message("tableConfig.driverDownloadFailedTitle")
                                            )
                                        }
                                    }
                                }
                            })
                            return@run
                        }
                    }

                    val errorMessage = e.message ?: TableGearBundle.message("common.unknown")
                    SwingUtilities.invokeLater {
                        statusLabel.text = TableGearBundle.message("tableConfig.status.connectFailed", errorMessage.take(50))
                        connectDbButton.isEnabled = true
                        connectDbButton.text = TableGearBundle.message("tableConfig.connectDb")
                        Messages.showErrorDialog(project, errorMessage, TableGearBundle.message("tableConfig.connectFailedTitle"))
                    }
                    e.printStackTrace()
                }
            }
        })
    }
    
    private fun selectTables() {
        val helper = dbHelper
        if (helper == null) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("tableConfig.requireConnect.message"),
                TableGearBundle.message("tableConfig.requireConnect.title")
            )
            return
        }
        
        try {
            // 获取所有表
            val tables = helper.getAllTables()
            
            if (tables.isEmpty()) {
                // 显示 toast 通知而不是弹窗
                val notificationGroup = NotificationGroupManager.getInstance()
                    .getNotificationGroup("TableGear.Notification")
                notificationGroup?.createNotification(
                    TableGearBundle.message("tableConfig.notification.noTablesFound"),
                    NotificationType.WARNING
                )?.notify(project)
                return
            }
            
            // 获取当前选中的表
            val currentTableList = tableListArea.text.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
            
            val currentExcludeList = excludeTableListArea.text.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
            
            // 显示表选择对话框
            val dialog = TableSelectionDialog(
                project,
                tables,
                currentTableList,
                currentExcludeList
            )
            
            if (dialog.showAndGet()) {
                // 更新表列表
                val selectedTables = dialog.getSelectedTables()
                tableListArea.text = selectedTables.joinToString(",")
                
                // 更新排除表列表
                val selectedExcludeTables = dialog.getSelectedExcludeTables()
                excludeTableListArea.text = selectedExcludeTables.joinToString(",")
                
                // 显示 toast 通知而不是弹窗
                val notificationGroup = NotificationGroupManager.getInstance()
                    .getNotificationGroup("TableGear.Notification")
                notificationGroup?.createNotification(
                    TableGearBundle.message("tableConfig.notification.tablesSelected", selectedTables.size, selectedExcludeTables.size),
                    NotificationType.INFORMATION
                )?.notify(project)
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("tableConfig.loadTablesFailed", e.message ?: "unknown"),
                TableGearBundle.message("notification.common.errorTitle")
            )
            e.printStackTrace()
        }
    }
    
    fun updateFromModel() {
        tableListArea.text = model.getProperty("table_list") ?: ""
        excludeTableListArea.text = model.getProperty("exclude_table_list") ?: ""
        viewListArea.text = model.getProperty("view_list") ?: ""
    }
    
    fun updateToModel() {
        model.setProperty("table_list", tableListArea.text)
        model.setProperty("exclude_table_list", excludeTableListArea.text)
        model.setProperty("view_list", viewListArea.text)
    }
    
    /**
     * 获取数据库连接助手
     */
    fun getDbHelper(): DatabaseConnectionHelper? = dbHelper
    
    /**
     * 清理资源
     */
    fun dispose() {
        dbHelper?.close()
    }
}
