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
    private lateinit var classNameField: JTextComponent
    private lateinit var dburlField: JTextComponent
    private lateinit var usernameField: JTextComponent
    private lateinit var passwordField: JPasswordField
    private lateinit var catalogField: JTextComponent
    private lateinit var schemaField: JTextComponent
    private lateinit var tableOwnerField: JTextComponent
    
    init {
        initializePanel()
    }
    
    private fun initializePanel() {
        aliasField = JBTextField()
        classNameField = JBTextField()
        dburlField = ExpandableTextField()
        usernameField = JBTextField()
        passwordField = JPasswordField()
        catalogField = JBTextField()
        schemaField = JBTextField()
        tableOwnerField = JBTextField()
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("别名 (alias):"), aliasField)
            .addTooltip("数据源别名，用于Spring的@Qualifier注解")
            .addLabeledComponent(JBLabel("驱动类名 (className):"), classNameField)
            .addTooltip("JDBC驱动类名，如 com.mysql.cj.jdbc.Driver")
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
        classNameField.text = model.getProperty("className") ?: ""
        dburlField.text = model.getProperty("dburl") ?: ""
        usernameField.text = model.getProperty("username") ?: ""
        passwordField.text = model.getProperty("password") ?: ""
        catalogField.text = model.getProperty("catalog") ?: ""
        schemaField.text = model.getProperty("schema") ?: ""
        tableOwnerField.text = model.getProperty("table_owner") ?: ""
    }
    
    fun updateToModel() {
        model.setProperty("alias", aliasField.text)
        model.setProperty("className", classNameField.text)
        model.setProperty("dburl", dburlField.text)
        model.setProperty("username", usernameField.text)
        model.setProperty("password", String(passwordField.password))
        model.setProperty("catalog", catalogField.text)
        model.setProperty("schema", schemaField.text)
        model.setProperty("table_owner", tableOwnerField.text)
    }
}
