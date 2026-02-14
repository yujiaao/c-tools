package com.bixuebihui.tablegear.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*
import javax.swing.text.JTextComponent

/**
 * Elasticsearch 配置面板
 * 用于 type=elasticsearch_mapping / elasticsearch_sample 时的 URL、索引、主键等配置
 */
class EsConfigPanel(private val model: TableGenConfigModel) : JPanel() {

    private lateinit var aliasField: JTextComponent
    private lateinit var typeCombo: JComboBox<String>
    private lateinit var urlField: JTextComponent
    private lateinit var usernameField: JTextComponent
    private lateinit var passwordField: JPasswordField
    private lateinit var indexListField: JTextComponent
    private lateinit var primaryKeyFieldsField: JTextComponent

    companion object {
        private val ES_TYPE_OPTIONS = listOf(
            "elasticsearch_mapping",
            "elasticsearch_sample"
        )
    }

    init {
        initializePanel()
    }

    private fun initializePanel() {
        aliasField = JBTextField()
        typeCombo = JComboBox(ES_TYPE_OPTIONS.toTypedArray())
        typeCombo.isEditable = false
        urlField = ExpandableTextField()
        usernameField = JBTextField()
        passwordField = JPasswordField()
        indexListField = JBTextField()
        primaryKeyFieldsField = JBTextField()

        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("别名 (alias):"), aliasField)
            .addTooltip("数据源或项目别名，用于 Spring Bean qualifier")
            .addLabeledComponent(JBLabel("生成模式 (type):"), typeCombo)
            .addTooltip("elasticsearch_mapping: 从 ES mapping 生成；elasticsearch_sample: 从 ES 样本文档生成")
            .addLabeledComponent(JBLabel("ES 地址 (url):"), urlField)
            .addTooltip("Elasticsearch 访问地址，如 http://localhost:9200")
            .addLabeledComponent(JBLabel("用户名 (username):"), usernameField)
            .addLabeledComponent(JBLabel("密码 (password):"), passwordField)
            .addLabeledComponent(JBLabel("索引列表 (index_list):"), indexListField)
            .addTooltip("格式: indexName 或 indexName:aliasName，多个用逗号分隔")
            .addLabeledComponent(JBLabel("主键字段 (primary_key_fields):"), primaryKeyFieldsField)
            .addTooltip("映射到 _id 的字段，多个用逗号分隔")
            .addComponentFillVertically(JPanel(), 0)
            .panel

        layout = java.awt.BorderLayout()
        add(form, java.awt.BorderLayout.CENTER)
    }

    fun updateFromModel() {
        aliasField.text = model.getProperty("alias") ?: ""
        val type = model.getProperty("type") ?: "elasticsearch_mapping"
        val index = ES_TYPE_OPTIONS.indexOf(type).takeIf { it >= 0 } ?: 0
        typeCombo.selectedIndex = index
        urlField.text = model.getProperty("url") ?: ""
        usernameField.text = model.getProperty("username") ?: ""
        passwordField.text = model.getProperty("password") ?: ""
        indexListField.text = model.getProperty("index_list") ?: ""
        primaryKeyFieldsField.text = model.getProperty("primary_key_fields") ?: ""
    }

    fun updateToModel() {
        model.setProperty("alias", aliasField.text)
        model.setProperty("type", typeCombo.selectedItem?.toString() ?: "elasticsearch_mapping")
        model.setProperty("url", urlField.text)
        model.setProperty("username", usernameField.text)
        model.setProperty("password", String(passwordField.password))
        model.setProperty("index_list", indexListField.text)
        model.setProperty("primary_key_fields", primaryKeyFieldsField.text)
    }
}
