package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.ExpandVetoException
import javax.swing.tree.TreePath

/**
 * 字段选择对话框
 * 用于从数据库中选择表和字段
 */
class FieldSelectionDialog(
    project: Project?,
    private val dbHelper: DatabaseConnectionHelper,
    private val fieldType: FieldType,
    private val currentConfig: String = ""
) : DialogWrapper(project) {
    
    enum class FieldType {
        ENCRYPTED,  // 加密字段
        IGNORE      // 忽略字段
    }
    
    private val tableFieldMap = mutableMapOf<String, MutableMap<String, JBCheckBox>>()
    private val loadedTables = mutableSetOf<String>() // 已加载字段的表
    private val configuredTables = mutableSetOf<String>() // 已配置的表（从 currentConfig 解析）
    private val originalTableNodes = mutableMapOf<String, DefaultMutableTreeNode>() // 保存原始表节点，用于搜索后恢复
    private val loadedTableNodes = mutableMapOf<String, DefaultMutableTreeNode>() // 保存已加载字段的表节点
    private lateinit var tree: JTree
    private lateinit var tableSearchField: JTextField  // 表搜索框
    private lateinit var fieldSearchField: JTextField  // 字段搜索框
    private lateinit var treeModel: DefaultTreeModel
    private lateinit var rootNode: DefaultMutableTreeNode
    private val loadingNodeText = TableGearBundle.message("fieldSelection.loadingNode")
    
    init {
        title = when (fieldType) {
            FieldType.ENCRYPTED -> TableGearBundle.message("fieldSelection.title.encrypted")
            FieldType.IGNORE -> TableGearBundle.message("fieldSelection.title.ignore")
        }
        init()
        parseCurrentConfig() // 先解析配置，找出已配置的表
        loadTablesAndFields() // 然后加载表和字段（优先加载已配置的表）
    }
    
    override fun createCenterPanel(): JComponent {
        val mainPanel = JPanel(BorderLayout())
        
        // 搜索框面板
        val searchPanel = JPanel()
        searchPanel.layout = java.awt.BorderLayout()
        searchPanel.border = JBUI.Borders.empty(5)
        
        // 表搜索框
        val tableSearchPanel = JPanel(BorderLayout())
        tableSearchPanel.add(JBLabel(TableGearBundle.message("fieldSelection.searchTable")), BorderLayout.WEST)
        tableSearchField = JTextField()
        tableSearchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = filterTree()
            override fun removeUpdate(e: DocumentEvent?) = filterTree()
            override fun changedUpdate(e: DocumentEvent?) = filterTree()
        })
        tableSearchPanel.add(tableSearchField, BorderLayout.CENTER)
        
        // 字段搜索框
        val fieldSearchPanel = JPanel(BorderLayout())
        fieldSearchPanel.add(JBLabel(TableGearBundle.message("fieldSelection.searchField")), BorderLayout.WEST)
        fieldSearchField = JTextField()
        fieldSearchField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = filterTree()
            override fun removeUpdate(e: DocumentEvent?) = filterTree()
            override fun changedUpdate(e: DocumentEvent?) = filterTree()
        })
        fieldSearchPanel.add(fieldSearchField, BorderLayout.CENTER)
        
        // 将两个搜索框垂直排列
        val searchFieldsPanel = JPanel()
        searchFieldsPanel.layout = java.awt.BorderLayout()
        searchFieldsPanel.add(tableSearchPanel, BorderLayout.NORTH)
        searchFieldsPanel.add(fieldSearchPanel, BorderLayout.CENTER)
        
        searchPanel.add(searchFieldsPanel, BorderLayout.CENTER)
        mainPanel.add(searchPanel, BorderLayout.NORTH)
        
        // 树形结构：表 -> 字段
        rootNode = DefaultMutableTreeNode(TableGearBundle.message("fieldSelection.root"))
        treeModel = DefaultTreeModel(rootNode)
        tree = JTree(treeModel)
        tree.isRootVisible = false
        tree.showsRootHandles = true
        tree.cellRenderer = object : javax.swing.tree.DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: JTree?,
                value: Any?,
                sel: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ): java.awt.Component {
                val component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
                if (value is DefaultMutableTreeNode) {
                    val userObject = value.userObject
                    if (userObject is TableFieldNode) {
                        if (userObject.isTable) {
                            text = userObject.name
                        } else {
                            text = "${userObject.name} (${userObject.type})"
                        }
                    }
                }
                return component
            }
        }
        
        // 添加复选框支持
        tree.cellRenderer = CheckBoxTreeCellRenderer()
        
        // 添加懒加载监听器：当展开表节点时加载字段
        tree.addTreeWillExpandListener(object : TreeWillExpandListener {
            override fun treeWillExpand(event: javax.swing.event.TreeExpansionEvent) {
                val path = event.path
                val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
                val userObject = node.userObject as? TableFieldNode ?: return
                
                // 如果是表节点且尚未加载字段，则加载字段
                if (userObject.isTable && !loadedTables.contains(userObject.name)) {
                    // 先添加一个"加载中..."的占位节点
                    javax.swing.SwingUtilities.invokeLater {
                        // 清除之前的占位节点（如果有）
                        node.removeAllChildren()
                        val loadingNode = DefaultMutableTreeNode(TableFieldNode(loadingNodeText, false))
                        node.add(loadingNode)
                        treeModel.reload(node)
                    }
                    
                    // 在后台线程中加载字段
                    Thread {
                        try {
                            // 直接使用当前节点加载字段（确保字段加载到树中显示的节点）
                            // 如果当前节点还没有保存到 loadedTableNodes，先保存
                            if (!loadedTableNodes.containsKey(userObject.name)) {
                                loadedTableNodes[userObject.name] = node
                            }
                            
                            // 加载字段到当前节点
                            loadTableFields(userObject.name, node)
                            
                            // 加载完成后应用配置（如果有）
                            if (configuredTables.contains(userObject.name)) {
                                applyTableConfig(userObject.name)
                            }
                            
                            // 如果当前有搜索条件，重新应用过滤以显示新加载的字段
                            javax.swing.SwingUtilities.invokeLater {
                                val currentTableSearch = tableSearchField.text.lowercase().trim()
                                val currentFieldSearch = fieldSearchField.text.lowercase().trim()
                                if (currentTableSearch.isNotEmpty() || currentFieldSearch.isNotEmpty()) {
                                    filterTree()
                                } else {
                                    // 如果没有搜索条件，更新当前节点
                                    treeModel.reload(node)
                                }
                            }
                        } catch (e: Exception) {
                            // 如果加载失败，显示错误信息
                            javax.swing.SwingUtilities.invokeLater {
                                node.removeAllChildren()
                                val errorNode = DefaultMutableTreeNode(TableFieldNode(TableGearBundle.message("fieldSelection.loadFailed", e.message ?: "unknown"), false))
                                node.add(errorNode)
                                treeModel.reload(node)
                            }
                        }
                    }.start()
                }
            }
            
            override fun treeWillCollapse(event: javax.swing.event.TreeExpansionEvent) {
                // 不需要处理折叠事件
            }
        })
        
        tree.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                val path = tree.getPathForLocation(e.x, e.y) ?: return
                val node = path.lastPathComponent as? DefaultMutableTreeNode ?: return
                val userObject = node.userObject as? TableFieldNode ?: return
                
                // 只处理字段节点的点击，表节点不处理
                if (!userObject.isTable) {
                    // 点击字段节点：切换字段选中状态
                    val newState = !(userObject.checkBox?.isSelected ?: false)
                    userObject.checkBox?.isSelected = newState
                }
                tree.repaint()
            }
        })
        
        mainPanel.add(JBScrollPane(tree), BorderLayout.CENTER)
        
        // 按钮面板
        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val selectAllBtn = JButton(TableGearBundle.message("common.selectAll"))
        selectAllBtn.addActionListener { selectAll(true) }
        val deselectAllBtn = JButton(TableGearBundle.message("common.deselectAll"))
        deselectAllBtn.addActionListener { selectAll(false) }
        buttonPanel.add(selectAllBtn)
        buttonPanel.add(deselectAllBtn)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        
        mainPanel.preferredSize = Dimension(700, 600)
        return mainPanel
    }
    
    /**
     * 加载表和字段（懒加载模式）
     * 优先加载已配置的表，其他表在展开时再加载
     */
    private fun loadTablesAndFields() {
        try {
            val tables = dbHelper.getAllTables()
            
            // 先添加所有表节点
            tables.forEach { tableInfo ->
                val tableNode = DefaultMutableTreeNode(TableFieldNode(tableInfo.name, true))
                val tableUserObject = tableNode.userObject as TableFieldNode
                tableUserObject.fieldCheckBoxes = mutableMapOf()
                
                // 添加一个隐藏的占位子节点，确保表节点显示展开图标
                // 这个节点会在加载字段时被清除
                val placeholderNode = DefaultMutableTreeNode(TableFieldNode("__placeholder__", false))
                tableNode.add(placeholderNode)
                
                rootNode.add(tableNode)
                tableFieldMap[tableInfo.name] = mutableMapOf()
                
                // 保存原始表节点的引用（深拷贝），用于搜索后恢复
                originalTableNodes[tableInfo.name] = cloneTreeNode(tableNode)
            }
            
            treeModel.reload()
            
            // 优先加载已配置的表的字段（在后台线程中异步加载）
            if (configuredTables.isNotEmpty()) {
                Thread {
                    configuredTables.forEach { tableName ->
                        val tableNode = findTableNode(tableName)
                        if (tableNode != null && !loadedTables.contains(tableName)) {
                            loadTableFields(tableName, tableNode)
                            // 在EDT中展开已配置的表
                            javax.swing.SwingUtilities.invokeLater {
                                val path = TreePath(treeModel.getPathToRoot(tableNode))
                                tree.expandPath(path)
                            }
                        }
                    }
                    // 等待一小段时间确保字段加载完成，然后应用配置
                    Thread.sleep(100)
                    javax.swing.SwingUtilities.invokeLater {
                        applyCurrentConfig()
                    }
                }.start()
            }
        } catch (e: Exception) {
            JOptionPane.showMessageDialog(
                null,
                TableGearBundle.message("fieldSelection.loadTableListFailed", e.message ?: "unknown"),
                TableGearBundle.message("notification.common.errorTitle"),
                JOptionPane.ERROR_MESSAGE
            )
        }
    }
    
    /**
     * 加载指定表的字段
     * 注意：此方法可能在后台线程中调用，更新UI时需要切换到EDT
     */
    private fun loadTableFields(tableName: String, tableNode: DefaultMutableTreeNode) {
        if (loadedTables.contains(tableName)) {
            return // 已经加载过，避免重复加载
        }
        
        try {
            // 在后台线程中获取字段数据
            val columns = dbHelper.getTableColumns(tableName)
            
            // 在EDT中更新UI
            javax.swing.SwingUtilities.invokeLater {
                val fieldMap = mutableMapOf<String, JBCheckBox>()
                val tableUserObject = tableNode.userObject as TableFieldNode
                
                // 清除占位节点（如果有）
                tableNode.removeAllChildren()
                
                columns.forEach { columnInfo ->
                    val fieldNode = TableFieldNode(columnInfo.name, false, columnInfo.type)
                    val checkBox = JBCheckBox()
                    fieldNode.checkBox = checkBox
                    fieldMap[columnInfo.name] = checkBox
                    tableNode.add(DefaultMutableTreeNode(fieldNode))
                }
                
                // 更新表节点的字段映射
                tableUserObject.fieldCheckBoxes = fieldMap
                tableFieldMap[tableName] = fieldMap
                
                // 标记为已加载
                loadedTables.add(tableName)
                
                // 保存已加载字段的表节点引用
                loadedTableNodes[tableName] = tableNode
                
                // 更新树模型
                treeModel.reload(tableNode)
                
                // 如果该表在已配置列表中，应用选中状态
                if (configuredTables.contains(tableName)) {
                    applyTableConfig(tableName)
                }
                
                // 如果当前有搜索条件，重新应用过滤以显示新加载的字段
                val currentTableSearch = tableSearchField.text.lowercase().trim()
                val currentFieldSearch = fieldSearchField.text.lowercase().trim()
                if (currentTableSearch.isNotEmpty() || currentFieldSearch.isNotEmpty()) {
                    filterTree()
                }
            }
        } catch (e: Exception) {
            // 如果获取字段失败，在EDT中显示错误信息
            javax.swing.SwingUtilities.invokeLater {
                tableNode.removeAllChildren()
                val errorNode = DefaultMutableTreeNode(TableFieldNode(TableGearBundle.message("fieldSelection.loadFailed", e.message ?: "unknown"), false))
                tableNode.add(errorNode)
                treeModel.reload(tableNode)
            }
        }
    }
    
    /**
     * 查找表节点
     */
    private fun findTableNode(tableName: String): DefaultMutableTreeNode? {
        val root = rootNode
        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i) as? DefaultMutableTreeNode ?: continue
            val userObject = child.userObject as? TableFieldNode ?: continue
            if (userObject.isTable && userObject.name == tableName) {
                return child
            }
        }
        return null
    }
    
    
    private fun expandAll() {
        // 只展开已加载字段的表节点
        var row = 0
        while (row < tree.rowCount) {
            val path = tree.getPathForRow(row)
            if (path != null) {
                val node = path.lastPathComponent as? DefaultMutableTreeNode
                val userObject = node?.userObject as? TableFieldNode
                // 只展开已加载字段的表
                if (userObject?.isTable == true && loadedTables.contains(userObject.name)) {
                    tree.expandRow(row)
                }
            }
            row++
        }
    }
    
    private fun filterTree() {
        val tableSearchText = tableSearchField.text.lowercase().trim()
        val fieldSearchText = fieldSearchField.text.lowercase().trim()
        
        // 如果两个搜索文本都为空，恢复所有表节点
        if (tableSearchText.isEmpty() && fieldSearchText.isEmpty()) {
            rootNode.removeAllChildren()
            originalTableNodes.values.forEach { originalNode ->
                // 恢复原始表节点，但保留已加载的字段
                val tableName = (originalNode.userObject as? TableFieldNode)?.name ?: return@forEach
                val loadedNode = loadedTableNodes[tableName]
                if (loadedNode != null && loadedTables.contains(tableName)) {
                    // 如果表已经加载了字段，使用已加载的节点（包含字段）
                    rootNode.add(loadedNode)
                } else {
                    // 如果表还没有加载字段，创建新节点（不是克隆），这样展开时可以直接更新
                    val newNode = DefaultMutableTreeNode(TableFieldNode(tableName, true))
                    val newTableUserObject = newNode.userObject as TableFieldNode
                    newTableUserObject.fieldCheckBoxes = mutableMapOf()
                    // 添加占位节点以便展开
                    val placeholderNode = DefaultMutableTreeNode(TableFieldNode("__placeholder__", false))
                    newNode.add(placeholderNode)
                    rootNode.add(newNode)
                }
            }
            treeModel.reload()
            expandAll()
            return
        }
        
        // 过滤树节点：先按表名过滤，再按字段名过滤
        rootNode.removeAllChildren()
        
        originalTableNodes.values.forEach { originalTableNode ->
            val tableUserObject = originalTableNode.userObject as? TableFieldNode
            val tableName = tableUserObject?.name ?: return@forEach
            
            // 检查表名是否匹配（如果表搜索框有内容）
            val tableMatches = tableSearchText.isEmpty() || tableName.lowercase().contains(tableSearchText)
            
            // 如果表名不匹配，跳过该表
            if (!tableMatches) {
                return@forEach
            }
            
            // 获取当前表节点（可能已加载字段）
            val currentTableNode = loadedTableNodes[tableName] ?: originalTableNode
            
            // 创建过滤后的表节点（总是创建新节点，避免节点引用问题）
            val filteredTableNode = DefaultMutableTreeNode(TableFieldNode(tableName, true))
            val filteredTableUserObject = filteredTableNode.userObject as TableFieldNode
            filteredTableUserObject.fieldCheckBoxes = (currentTableNode.userObject as? TableFieldNode)?.fieldCheckBoxes ?: mutableMapOf()
            
            // 如果表已经加载了字段
            if (loadedTables.contains(tableName)) {
                // 如果字段搜索框为空，显示所有字段；否则只显示匹配的字段
                if (fieldSearchText.isEmpty()) {
                    // 添加所有字段（排除占位节点）
                    for (i in 0 until currentTableNode.childCount) {
                        val childNode = currentTableNode.getChildAt(i) as? DefaultMutableTreeNode ?: continue
                        val fieldUserObject = childNode.userObject as? TableFieldNode ?: continue
                        if (fieldUserObject.name != "__placeholder__" && fieldUserObject.name != loadingNodeText) {
                            // 克隆字段节点，但保留复选框引用
                            val clonedFieldNode = DefaultMutableTreeNode(fieldUserObject)
                            filteredTableNode.add(clonedFieldNode)
                        }
                    }
                } else {
                    // 只添加匹配的字段
                    for (i in 0 until currentTableNode.childCount) {
                        val childNode = currentTableNode.getChildAt(i) as? DefaultMutableTreeNode ?: continue
                        val fieldUserObject = childNode.userObject as? TableFieldNode ?: continue
                        
                        // 跳过占位节点
                        if (fieldUserObject.name == "__placeholder__" || fieldUserObject.name == loadingNodeText) {
                            continue
                        }
                        
                        // 检查字段名是否匹配
                        if (fieldUserObject.name.lowercase().contains(fieldSearchText)) {
                            // 克隆字段节点，但保留复选框引用
                            val clonedFieldNode = DefaultMutableTreeNode(fieldUserObject)
                            filteredTableNode.add(clonedFieldNode)
                        }
                    }
                }
            } else {
                // 如果字段还没加载，添加占位节点以便展开
                val placeholderNode = DefaultMutableTreeNode(TableFieldNode("__placeholder__", false))
                filteredTableNode.add(placeholderNode)
            }
            
            // 只有当表节点有字段或占位节点时才添加（避免空表）
            if (filteredTableNode.childCount > 0 || !loadedTables.contains(tableName)) {
                rootNode.add(filteredTableNode)
            }
        }
        
        treeModel.reload()
        expandAll()
    }
    
    /**
     * 克隆树节点（深拷贝）
     */
    private fun cloneTreeNode(node: DefaultMutableTreeNode): DefaultMutableTreeNode {
        val clonedNode = DefaultMutableTreeNode(node.userObject)
        for (i in 0 until node.childCount) {
            val child = node.getChildAt(i) as? DefaultMutableTreeNode ?: continue
            clonedNode.add(cloneTreeNode(child))
        }
        return clonedNode
    }
    
    private fun selectAll(select: Boolean) {
        tableFieldMap.values.forEach { fieldMap ->
            fieldMap.values.forEach { it.isSelected = select }
        }
        tree.repaint()
    }
    
    /**
     * 解析当前配置，找出已配置的表
     */
    private fun parseCurrentConfig() {
        if (currentConfig.isBlank()) return
        
        // 解析格式: table1:field1,field2;table2:field3
        val tableFieldPairs = currentConfig.split(";").filter { it.isNotBlank() }
        
        tableFieldPairs.forEach { pair ->
            val parts = pair.split(":", limit = 2)
            if (parts.size == 2) {
                val tableName = parts[0].trim()
                configuredTables.add(tableName) // 记录已配置的表
            }
        }
    }
    
    /**
     * 应用指定表的配置选中状态
     */
    private fun applyTableConfig(tableName: String) {
        if (currentConfig.isBlank()) return
        
        // 解析格式: table1:field1,field2;table2:field3
        val tableFieldPairs = currentConfig.split(";").filter { it.isNotBlank() }
        
        tableFieldPairs.forEach { pair ->
            val parts = pair.split(":", limit = 2)
            if (parts.size == 2) {
                val configTableName = parts[0].trim()
                if (configTableName == tableName) {
                    val fields = parts[1].split(",").map { it.trim() }.filter { it.isNotBlank() }
                    
                    tableFieldMap[tableName]?.let { fieldMap ->
                        fields.forEach { fieldName ->
                            fieldMap[fieldName]?.isSelected = true
                        }
                    }
                }
            } else {
                // 全局字段（没有表名前缀）
                val fields = parts[0].split(",").map { it.trim() }.filter { it.isNotBlank() }
                // 对于全局字段，应用到当前表
                tableFieldMap[tableName]?.let { fieldMap ->
                    fields.forEach { fieldName ->
                        fieldMap[fieldName]?.isSelected = true
                    }
                }
            }
        }
        
        tree.repaint()
    }
    
    /**
     * 应用所有已加载表的配置选中状态
     */
    private fun applyCurrentConfig() {
        if (currentConfig.isBlank()) return
        
        // 解析格式: table1:field1,field2;table2:field3
        val tableFieldPairs = currentConfig.split(";").filter { it.isNotBlank() }
        
        tableFieldPairs.forEach { pair ->
            val parts = pair.split(":", limit = 2)
            if (parts.size == 2) {
                val tableName = parts[0].trim()
                val fields = parts[1].split(",").map { it.trim() }.filter { it.isNotBlank() }
                
                // 如果表已加载，应用选中状态
                if (loadedTables.contains(tableName)) {
                    tableFieldMap[tableName]?.let { fieldMap ->
                        fields.forEach { fieldName ->
                            fieldMap[fieldName]?.isSelected = true
                        }
                    }
                }
            } else {
                // 全局字段（没有表名前缀）
                val fields = parts[0].split(",").map { it.trim() }.filter { it.isNotBlank() }
                // 对于全局字段，需要标记所有已加载表中的这些字段
                loadedTables.forEach { tableName ->
                    tableFieldMap[tableName]?.let { fieldMap ->
                        fields.forEach { fieldName ->
                            fieldMap[fieldName]?.isSelected = true
                        }
                    }
                }
            }
        }
        
        tree.repaint()
    }
    
    /**
     * 获取选中的字段配置
     * 格式: table1:field1,field2;table2:field3
     */
    fun getSelectedFieldsConfig(): String {
        val result = mutableListOf<String>()
        
        tableFieldMap.forEach { (tableName, fieldMap) ->
            val selectedFields = fieldMap.filter { it.value.isSelected }.keys.toList()
            if (selectedFields.isNotEmpty()) {
                result.add("$tableName:${selectedFields.joinToString(",")}")
            }
        }
        
        return result.joinToString(";")
    }
    
    /**
     * 表或字段节点数据
     */
    private data class TableFieldNode(
        val name: String,
        val isTable: Boolean,
        val type: String = "",
        var checkBox: JBCheckBox? = null,
        var fieldCheckBoxes: MutableMap<String, JBCheckBox> = mutableMapOf()
    )
    
    /**
     * 自定义树单元格渲染器，支持复选框
     */
    private class CheckBoxTreeCellRenderer : javax.swing.tree.DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree?,
            value: Any?,
            sel: Boolean,
            expanded: Boolean,
            leaf: Boolean,
            row: Int,
            hasFocus: Boolean
        ): java.awt.Component {
            val component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
            
            if (value is DefaultMutableTreeNode) {
                val userObject = value.userObject as? TableFieldNode ?: return component
                
                // 表节点不显示复选框，只显示表名
                if (userObject.isTable) {
                    val label = JBLabel().apply {
                        text = userObject.name
                    }
                    return label
                }
                
                // 字段节点：显示复选框和字段名
                val panel = JPanel(BorderLayout())
                panel.isOpaque = false
                
                val checkBox = userObject.checkBox ?: JBCheckBox()
                
                val label = JBLabel().apply {
                    // 隐藏占位节点
                    if (userObject.name == "__placeholder__" || userObject.name == TableGearBundle.message("fieldSelection.loadingNode")) {
                        text = ""
                    } else {
                        text = "${userObject.name} (${userObject.type})"
                    }
                }
                
                panel.add(checkBox, BorderLayout.WEST)
                panel.add(label, BorderLayout.CENTER)
                
                return panel
            }
            
            return component
        }
    }
}

