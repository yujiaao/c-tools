package com.bixuebihui.tablegear.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * 表选择对话框
 * 用于从数据库中选择表和视图
 */
class TableSelectionDialog(
    project: Project?,
    private val tables: List<DatabaseConnectionHelper.TableInfo>,
    private val selectedTables: Set<String> = emptySet(),
    private val selectedExcludeTables: Set<String> = emptySet()
) : DialogWrapper(project) {
    
    private val tableCheckBoxes = mutableMapOf<String, JBCheckBox>()
    private val excludeTableCheckBoxes = mutableMapOf<String, JBCheckBox>()
    private lateinit var searchField: JTextField
    private lateinit var tableListPanel: JPanel
    private lateinit var excludeTableListPanel: JPanel
    private lateinit var tabbedPane: JTabbedPane
    
    init {
        title = "选择表和视图"
        init()
    }
    
    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        
        // 搜索框
        val searchPanel = JPanel(BorderLayout())
        searchPanel.border = JBUI.Borders.empty(5)
        searchPanel.add(JBLabel("搜索:"), BorderLayout.WEST)
        searchField = JTextField()
        searchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = filterTables()
            override fun removeUpdate(e: DocumentEvent?) = filterTables()
            override fun changedUpdate(e: DocumentEvent?) = filterTables()
        })
        searchPanel.add(searchField, BorderLayout.CENTER)
        mainPanel.add(searchPanel, BorderLayout.NORTH)
        
        // 标签页：包含表和排除表
        tabbedPane = JTabbedPane()
        
        // 包含表标签页
        tableListPanel = createTableListPanel(tables, selectedTables, tableCheckBoxes)
        tabbedPane.addTab("包含的表", JScrollPane(tableListPanel))
        
        // 排除表标签页
        excludeTableListPanel = createTableListPanel(tables, selectedExcludeTables, excludeTableCheckBoxes)
        tabbedPane.addTab("排除的表", JScrollPane(excludeTableListPanel))
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER)
        
        // 全选/取消全选按钮
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val selectAllBtn = JButton("全选")
        selectAllBtn.addActionListener { selectAll(true) }
        val deselectAllBtn = JButton("取消全选")
        deselectAllBtn.addActionListener { selectAll(false) }
        buttonPanel.add(selectAllBtn)
        buttonPanel.add(deselectAllBtn)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        
        mainPanel.preferredSize = Dimension(600, 500)
        return mainPanel
    }
    
    private fun createTableListPanel(
        tables: List<DatabaseConnectionHelper.TableInfo>,
        selected: Set<String>,
        checkBoxMap: MutableMap<String, JBCheckBox>
    ): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        
        tables.forEach { tableInfo ->
            val checkBox = JBCheckBox(tableInfo.name + if (tableInfo.isView) " (视图)" else "")
            checkBox.isSelected = selected.contains(tableInfo.name)
            checkBoxMap[tableInfo.name] = checkBox
            panel.add(checkBox)
        }
        
        return panel
    }
    
    private fun filterTables() {
        val searchText = searchField.text.lowercase()
        val currentPanel = if (tabbedPane.selectedIndex == 0) tableListPanel else excludeTableListPanel
        val currentCheckBoxes = if (tabbedPane.selectedIndex == 0) tableCheckBoxes else excludeTableCheckBoxes
        
        currentPanel.components.forEach { component ->
            if (component is JBCheckBox) {
                val visible = searchText.isEmpty() || component.text.lowercase().contains(searchText)
                component.isVisible = visible
            }
        }
        currentPanel.revalidate()
        currentPanel.repaint()
    }
    
    private fun selectAll(select: Boolean) {
        val currentCheckBoxes = if (tabbedPane.selectedIndex == 0) tableCheckBoxes else excludeTableCheckBoxes
        currentCheckBoxes.values.forEach { it.isSelected = select }
    }
    
    /**
     * 获取选中的包含表列表
     */
    fun getSelectedTables(): List<String> {
        return tableCheckBoxes.filter { it.value.isSelected }.keys.toList()
    }
    
    /**
     * 获取选中的排除表列表
     */
    fun getSelectedExcludeTables(): List<String> {
        return excludeTableCheckBoxes.filter { it.value.isSelected }.keys.toList()
    }
}

