package com.bixuebihui.tablegear.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Font
import javax.swing.*
import javax.swing.text.JTextComponent

/**
 * 高级配置面板
 */
class AdvancedConfigPanel(private val model: TableGenConfigModel) : JPanel() {
    
    private lateinit var addPrefixField: JTextComponent
    private lateinit var removePrefixField: JTextComponent
    private lateinit var versionColNameField: JTextComponent
    private lateinit var delColNameField: JTextComponent
    private lateinit var sharedModelGroupField: JTextComponent
    private lateinit var extraSettingField: JTextComponent
    private lateinit var encryptedFieldsArea: JTextArea
    private lateinit var ignoreFieldsArea: JTextArea
    
    // 扩展接口配置
    private lateinit var pojoNodeInterfaceField: JTextComponent
    private lateinit var pojoVersionInterfaceField: JTextComponent
    private lateinit var pojoStateInterfaceField: JTextComponent
    private lateinit var pojoUuidInterfaceField: JTextComponent
    private lateinit var pojoModifyDateInterfaceField: JTextComponent
    private lateinit var pojoNodeInterfaceListField: JTextComponent
    private lateinit var pojoVersionInterfaceListField: JTextComponent
    private lateinit var pojoStateInterfaceListField: JTextComponent
    private lateinit var pojoUuidInterfaceListField: JTextComponent
    private lateinit var pojoModifyDateInterfaceListField: JTextComponent
    
    init {
        initializePanel()
    }
    
    private fun initializePanel() {
        val tabbedPane = JTabbedPane()
        
        // 基本高级配置
        tabbedPane.addTab("基本配置", createBasicConfigPanel())
        
        // 字段配置
        tabbedPane.addTab("字段配置", createFieldConfigPanel())
        
        // 扩展接口配置
        tabbedPane.addTab("扩展接口", createInterfaceConfigPanel())
        
        layout = java.awt.BorderLayout()
        add(tabbedPane, java.awt.BorderLayout.CENTER)
    }
    
    private fun createBasicConfigPanel(): JComponent {
        addPrefixField = JBTextField()
        removePrefixField = JBTextField()
        versionColNameField = JBTextField()
        delColNameField = JBTextField()
        sharedModelGroupField = JBTextField()
        extraSettingField = JBTextField()
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("添加前缀 (add_prefix):"), addPrefixField)
            .addLabeledComponent(JBLabel("移除前缀 (remove_prefix):"), removePrefixField)
            .addLabeledComponent(JBLabel("版本列名 (version_col_name):"), versionColNameField)
            .addLabeledComponent(JBLabel("删除列名 (del_col_name):"), delColNameField)
            .addLabeledComponent(JBLabel("共享模型组 (shared_model_group):"), sharedModelGroupField)
            .addLabeledComponent(JBLabel("额外设置 (extra_setting):"), extraSettingField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return JScrollPane(form)
    }
    
    private fun createFieldConfigPanel(): JComponent {
        encryptedFieldsArea = JBTextArea(8, 50)
        encryptedFieldsArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        ignoreFieldsArea = JBTextArea(8, 50)
        ignoreFieldsArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("加密字段 (encrypted_fields):"), JScrollPane(encryptedFieldsArea))
            .addTooltip("格式为 table1:field1,field2;table2:field3")
            .addLabeledComponent(JBLabel("忽略字段 (ignore_fields):"), JScrollPane(ignoreFieldsArea))
            .addTooltip("格式为 table1:field1,field2;table2:field3")
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return form
    }
    
    private fun createInterfaceConfigPanel(): JComponent {
        pojoNodeInterfaceField = JBTextField()
        pojoVersionInterfaceField = JBTextField()
        pojoStateInterfaceField = JBTextField()
        pojoUuidInterfaceField = JBTextField()
        pojoModifyDateInterfaceField = JBTextField()
        pojoNodeInterfaceListField = JBTextField()
        pojoVersionInterfaceListField = JBTextField()
        pojoStateInterfaceListField = JBTextField()
        pojoUuidInterfaceListField = JBTextField()
        pojoModifyDateInterfaceListField = JBTextField()
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("节点接口 (pojo_node_interface):"), pojoNodeInterfaceField)
            .addLabeledComponent(JBLabel("节点接口列表 (pojo_node_interface_list):"), pojoNodeInterfaceListField)
            .addLabeledComponent(JBLabel("版本接口 (pojo_version_interface):"), pojoVersionInterfaceField)
            .addLabeledComponent(JBLabel("版本接口列表 (pojo_version_interface_list):"), pojoVersionInterfaceListField)
            .addLabeledComponent(JBLabel("状态接口 (pojo_state_interface):"), pojoStateInterfaceField)
            .addLabeledComponent(JBLabel("状态接口列表 (pojo_state_interface_list):"), pojoStateInterfaceListField)
            .addLabeledComponent(JBLabel("UUID接口 (pojo_uuid_interface):"), pojoUuidInterfaceField)
            .addLabeledComponent(JBLabel("UUID接口列表 (pojo_uuid_interface_list):"), pojoUuidInterfaceListField)
            .addLabeledComponent(JBLabel("修改日期接口 (pojo_modifydate_interface):"), pojoModifyDateInterfaceField)
            .addLabeledComponent(JBLabel("修改日期接口列表 (pojo_modifydate_interface_list):"), pojoModifyDateInterfaceListField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return JScrollPane(form)
    }
    
    fun updateFromModel() {
        addPrefixField.text = model.getProperty("add_prefix") ?: ""
        removePrefixField.text = model.getProperty("remove_prefix") ?: ""
        versionColNameField.text = model.getProperty("version_col_name") ?: "version"
        delColNameField.text = model.getProperty("del_col_name") ?: "is_del"
        sharedModelGroupField.text = model.getProperty("shared_model_group") ?: ""
        extraSettingField.text = model.getProperty("extra_setting") ?: ""
        encryptedFieldsArea.text = model.getProperty("encrypted_fields") ?: ""
        ignoreFieldsArea.text = model.getProperty("ignore_fields") ?: ""
        
        pojoNodeInterfaceField.text = model.getProperty("pojo_node_interface") ?: ""
        pojoVersionInterfaceField.text = model.getProperty("pojo_version_interface") ?: ""
        pojoStateInterfaceField.text = model.getProperty("pojo_state_interface") ?: ""
        pojoUuidInterfaceField.text = model.getProperty("pojo_uuid_interface") ?: ""
        pojoModifyDateInterfaceField.text = model.getProperty("pojo_modifydate_interface") ?: ""
        pojoNodeInterfaceListField.text = model.getProperty("pojo_node_interface_list") ?: ""
        pojoVersionInterfaceListField.text = model.getProperty("pojo_version_interface_list") ?: ""
        pojoStateInterfaceListField.text = model.getProperty("pojo_state_interface_list") ?: ""
        pojoUuidInterfaceListField.text = model.getProperty("pojo_uuid_interface_list") ?: ""
        pojoModifyDateInterfaceListField.text = model.getProperty("pojo_modifydate_interface_list") ?: ""
    }
    
    fun updateToModel() {
        model.setProperty("add_prefix", addPrefixField.text)
        model.setProperty("remove_prefix", removePrefixField.text)
        model.setProperty("version_col_name", versionColNameField.text)
        model.setProperty("del_col_name", delColNameField.text)
        model.setProperty("shared_model_group", sharedModelGroupField.text)
        model.setProperty("extra_setting", extraSettingField.text)
        model.setProperty("encrypted_fields", encryptedFieldsArea.text)
        model.setProperty("ignore_fields", ignoreFieldsArea.text)
        
        model.setProperty("pojo_node_interface", pojoNodeInterfaceField.text)
        model.setProperty("pojo_version_interface", pojoVersionInterfaceField.text)
        model.setProperty("pojo_state_interface", pojoStateInterfaceField.text)
        model.setProperty("pojo_uuid_interface", pojoUuidInterfaceField.text)
        model.setProperty("pojo_modifydate_interface", pojoModifyDateInterfaceField.text)
        model.setProperty("pojo_node_interface_list", pojoNodeInterfaceListField.text)
        model.setProperty("pojo_version_interface_list", pojoVersionInterfaceListField.text)
        model.setProperty("pojo_state_interface_list", pojoStateInterfaceListField.text)
        model.setProperty("pojo_uuid_interface_list", pojoUuidInterfaceListField.text)
        model.setProperty("pojo_modifydate_interface_list", pojoModifyDateInterfaceListField.text)
    }
}

