package com.bixuebihui.tablegear.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.FormBuilder
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
        connectDbButton = JButton("连接数据库")
        connectDbButton.addActionListener { connectToDatabase() }
        
        selectTablesButton = JButton("选择表...")
        selectTablesButton.isEnabled = false
        selectTablesButton.addActionListener { selectTables() }
        
        statusLabel = JBLabel("未连接")
        statusLabel.font = statusLabel.font.deriveFont(Font.ITALIC, 11f)
        
        val buttonPanel = JPanel()
        buttonPanel.add(connectDbButton)
        buttonPanel.add(selectTablesButton)
        buttonPanel.add(statusLabel)
        
        val form = FormBuilder.createFormBuilder()
            .addComponent(buttonPanel)
            .addLabeledComponent(JBLabel("表列表 (table_list):"), JScrollPane(tableListArea))
            .addTooltip("要生成的表列表，多个表用逗号分隔，支持正则表达式\n例如: table1,table2,test(\\w+)")
            .addLabeledComponent(JBLabel("排除表列表 (exclude_table_list):"), JScrollPane(excludeTableListArea))
            .addTooltip("要排除的表列表，多个表用逗号分隔\n例如: PLAN_TABLE,T_SESSION,T_SEQUENCE")
            .addLabeledComponent(JBLabel("视图列表 (view_list):"), JScrollPane(viewListArea))
            .addTooltip("视图列表，用分号(;)分隔，格式为 view_name: select sql\n例如: my_view: select id, name from table1;")
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        layout = java.awt.BorderLayout()
        add(form, java.awt.BorderLayout.CENTER)
    }
    
    private fun connectToDatabase() {
        // 检查数据库配置是否完整
        val className = model.getProperty("className")
        val dburl = model.getProperty("dburl")
        val username = model.getProperty("username")
        val password = model.getProperty("password")
        
        if (className.isNullOrBlank() || dburl.isNullOrBlank()) {
            Messages.showErrorDialog(
                project,
                "请先配置数据库连接信息（驱动类名和数据库URL）",
                "配置不完整"
            )
            return
        }
        
        try {
            connectDbButton.isEnabled = false
            connectDbButton.text = "连接中..."
            statusLabel.text = "正在连接..."
            
            // 关闭之前的连接
            dbHelper?.close()
            
            // 创建新的连接
            dbHelper = DatabaseConnectionHelper(model)
            val success = dbHelper!!.testConnection()
            
            if (success) {
                statusLabel.text = "已连接"
                selectTablesButton.isEnabled = true
                Messages.showInfoMessage(
                    project,
                    "数据库连接成功！",
                    "连接成功"
                )
            } else {
                statusLabel.text = "连接失败"
                Messages.showErrorDialog(
                    project,
                    "数据库连接失败，请检查配置信息",
                    "连接失败"
                )
            }
        } catch (e: Exception) {
            statusLabel.text = "连接失败: ${e.message}"
            Messages.showErrorDialog(
                project,
                "数据库连接失败: ${e.message}",
                "连接失败"
            )
        } finally {
            connectDbButton.isEnabled = true
            connectDbButton.text = "连接数据库"
        }
    }
    
    private fun selectTables() {
        val helper = dbHelper
        if (helper == null) {
            Messages.showErrorDialog(
                project,
                "请先连接数据库",
                "未连接"
            )
            return
        }
        
        try {
            // 获取所有表
            val tables = helper.getAllTables()
            
            if (tables.isEmpty()) {
                Messages.showInfoMessage(
                    project,
                    "数据库中没有找到表或视图",
                    "无数据"
                )
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
                
                Messages.showInfoMessage(
                    project,
                    "已选择 ${selectedTables.size} 个表，${selectedExcludeTables.size} 个排除表",
                    "选择完成"
                )
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "获取表列表失败: ${e.message}",
                "错误"
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
     * 清理资源
     */
    fun dispose() {
        dbHelper?.close()
    }
}
