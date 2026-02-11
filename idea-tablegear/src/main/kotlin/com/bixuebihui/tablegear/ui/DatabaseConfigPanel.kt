package com.bixuebihui.tablegear.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*
import javax.swing.text.JTextComponent

/**
 * 数据库配置面板
 */
class DatabaseConfigPanel(private val model: TableGenConfigModel) : JPanel() {
    
    private lateinit var aliasField: JTextComponent
    private lateinit var classNameField: JComboBox<String>
    private lateinit var dburlField: JTextComponent
    private lateinit var usernameField: JTextComponent
    private lateinit var passwordField: JPasswordField
    private lateinit var catalogField: JTextComponent
    private lateinit var schemaField: JTextComponent
    private lateinit var tableOwnerField: JTextComponent
    
    companion object {
        private val JDBC_DRIVER_SUGGESTIONS = listOf(
            "com.mysql.cj.jdbc.Driver",  // MySQL Connector/J 8.0+ (推荐)
            "com.mysql.jdbc.Driver",  // MySQL Connector/J 5.x (已弃用)
            "org.mariadb.jdbc.Driver",  // MariaDB JDBC Driver
            "org.postgresql.Driver",  // PostgreSQL JDBC Driver
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",  // Microsoft SQL Server JDBC Driver
            "oracle.jdbc.OracleDriver",  // Oracle JDBC Driver
            "oracle.jdbc.driver.OracleDriver",  // Oracle JDBC Driver (旧版)
            "org.h2.Driver",  // H2 Database Driver
            "com.ibm.db2.jcc.DB2Driver",  // IBM DB2 JDBC Driver
            "com.sap.db.jdbc.Driver",  // SAP HANA JDBC Driver
            "org.apache.derby.jdbc.EmbeddedDriver",  // Apache Derby Embedded Driver
            "org.apache.derby.jdbc.ClientDriver",  // Apache Derby Client Driver
            "com.sybase.jdbc4.jdbc.SybDriver",  // Sybase JDBC Driver
            "com.informix.jdbc.IfxDriver"  // IBM Informix JDBC Driver
        )
    }
    
    init {
        initializePanel()
    }
    
    private fun initializePanel() {
        aliasField = JBTextField()
        // 创建可编辑的下拉选择框
        classNameField = JComboBox(JDBC_DRIVER_SUGGESTIONS.toTypedArray())
        classNameField.isEditable = true
        classNameField.selectedItem = null
        usernameField = JBTextField()
        passwordField = JPasswordField()
        catalogField = JBTextField()
        schemaField = JBTextField()
        tableOwnerField = JBTextField()
        
        // 使用 ExpandableTextField 但设置与其他字段一致的宽度
        dburlField = ExpandableTextField()
        // 获取其他字段的 preferred width，然后设置给 dburlField
        val referenceWidth = aliasField.preferredSize.width
        // ExpandableTextField 继承自 JTextField，可以设置 preferred size
        (dburlField as? JTextField)?.preferredSize = java.awt.Dimension(referenceWidth, dburlField.preferredSize.height)
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("别名 (alias):"), aliasField)
            .addTooltip("数据源别名，用于Spring的@Qualifier注解")
            .addLabeledComponent(JBLabel("驱动类名 (className):"), classNameField)
            .addTooltip("JDBC驱动类名，可从下拉列表选择常用驱动，也可手动输入")
            .addLabeledComponent(JBLabel("数据库URL (dburl):"), dburlField)
            .addTooltip("数据库连接URL")
            .addLabeledComponent(JBLabel("用户名 (username):"), usernameField)
            .addLabeledComponent(JBLabel("密码 (password):"), passwordField)
            .addLabeledComponent(JBLabel("目录 (catalog):"), catalogField)
            .addLabeledComponent(JBLabel("模式 (schema):"), schemaField)
            .addLabeledComponent(JBLabel("表所有者 (table_owner):"), tableOwnerField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        layout = java.awt.BorderLayout()
        add(form, java.awt.BorderLayout.CENTER)
    }
    
    fun updateFromModel() {
        aliasField.text = model.getProperty("alias") ?: ""
        val className = model.getProperty("className") ?: ""
        // 如果配置的值在下拉列表中，则选中；否则设置为可编辑文本
        if (className.isNotEmpty()) {
            val index = JDBC_DRIVER_SUGGESTIONS.indexOf(className)
            if (index >= 0) {
                classNameField.selectedIndex = index
            } else {
                classNameField.selectedItem = className
            }
        } else {
            classNameField.selectedItem = null
        }
        dburlField.text = model.getProperty("dburl") ?: ""
        usernameField.text = model.getProperty("username") ?: ""
        passwordField.text = model.getProperty("password") ?: ""
        catalogField.text = model.getProperty("catalog") ?: ""
        schemaField.text = model.getProperty("schema") ?: ""
        tableOwnerField.text = model.getProperty("table_owner") ?: ""
    }
    
    fun updateToModel() {
        model.setProperty("alias", aliasField.text)
        // 获取下拉框的当前值（可能是选中的项或手动输入的文本）
        val className = classNameField.selectedItem?.toString() ?: ""
        model.setProperty("className", className)
        model.setProperty("dburl", dburlField.text)
        model.setProperty("username", usernameField.text)
        model.setProperty("password", String(passwordField.password))
        model.setProperty("catalog", catalogField.text)
        model.setProperty("schema", schemaField.text)
        model.setProperty("table_owner", tableOwnerField.text)
    }
}
