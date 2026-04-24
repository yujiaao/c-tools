package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Font
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.text.JTextComponent

/**
 * 高级配置面板
 */
class AdvancedConfigPanel(
    private val model: TableGenConfigModel,
    private val project: Project? = null,
    private val tablePanel: TableConfigPanel? = null
) : JPanel() {
    
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
        val tabbedPane = JBTabbedPane()
        
        // 基本高级配置
        tabbedPane.addTab(TableGearBundle.message("advanced.tab.basic"), createBasicConfigPanel())
        
        // 字段配置
        tabbedPane.addTab(TableGearBundle.message("advanced.tab.fields"), createFieldConfigPanel())
        
        // 扩展接口配置
        tabbedPane.addTab(TableGearBundle.message("advanced.tab.interfaces"), createInterfaceConfigPanel())
        
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
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.addPrefix")), addPrefixField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.removePrefix")), removePrefixField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.versionCol")), versionColNameField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.delCol")), delColNameField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.sharedModelGroup")), sharedModelGroupField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.extraSetting")), extraSettingField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return JScrollPane(form)
    }
    
    private fun createFieldConfigPanel(): JComponent {
        encryptedFieldsArea = JBTextArea(8, 50)
        encryptedFieldsArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        ignoreFieldsArea = JBTextArea(8, 50)
        ignoreFieldsArea.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        
        // 加密字段选择按钮
        val encryptedFieldsButtonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val selectEncryptedFieldsBtn = JButton(TableGearBundle.message("advanced.button.selectEncrypted"))
        selectEncryptedFieldsBtn.addActionListener { selectFields(FieldSelectionDialog.FieldType.ENCRYPTED, encryptedFieldsArea) }
        encryptedFieldsButtonPanel.add(selectEncryptedFieldsBtn as java.awt.Component)
        
        // 忽略字段选择按钮
        val ignoreFieldsButtonPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        val selectIgnoreFieldsBtn = JButton(TableGearBundle.message("advanced.button.selectIgnore"))
        selectIgnoreFieldsBtn.addActionListener { selectFields(FieldSelectionDialog.FieldType.IGNORE, ignoreFieldsArea) }
        ignoreFieldsButtonPanel.add(selectIgnoreFieldsBtn as java.awt.Component)
        
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.encryptedFields")), encryptedFieldsButtonPanel)
            .addLabeledComponent(JBLabel(""), JScrollPane(encryptedFieldsArea))
            .addTooltip(TableGearBundle.message("advanced.tooltip.fieldFormat"))
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.ignoreFields")), ignoreFieldsButtonPanel)
            .addLabeledComponent(JBLabel(""), JScrollPane(ignoreFieldsArea))
            .addTooltip(TableGearBundle.message("advanced.tooltip.fieldFormat"))
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return form
    }
    
    private fun selectFields(fieldType: FieldSelectionDialog.FieldType, textArea: JTextArea) {
        // 检查数据库是否已连接
        val currentTablePanel = tablePanel ?: getTablePanel()
        val dbHelper = currentTablePanel?.getDbHelper()
        
        if (dbHelper == null) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("advanced.dbNotConnected.message"),
                TableGearBundle.message("advanced.dbNotConnected.title")
            )
            return
        }
        
        try {
            val dialog = FieldSelectionDialog(
                project,
                dbHelper,
                fieldType,
                textArea.text
            )
            
            if (dialog.showAndGet()) {
                val config = dialog.getSelectedFieldsConfig()
                textArea.text = config
                
                // 显示 toast 通知
                val notificationGroup = com.intellij.notification.NotificationGroupManager.getInstance()
                    .getNotificationGroup("TableGear.Notification")
                notificationGroup?.createNotification(
                    when (fieldType) {
                        FieldSelectionDialog.FieldType.ENCRYPTED -> TableGearBundle.message("advanced.notification.encryptedSelected")
                        FieldSelectionDialog.FieldType.IGNORE -> TableGearBundle.message("advanced.notification.ignoreSelected")
                    },
                    com.intellij.notification.NotificationType.INFORMATION
                )?.notify(project)
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                TableGearBundle.message("advanced.selectFieldsFailed", e.message ?: "unknown"),
                TableGearBundle.message("notification.common.errorTitle")
            )
            e.printStackTrace()
        }
    }
    
    private fun getTablePanel(): TableConfigPanel? {
        // 如果已经通过构造函数传入了 tablePanel，直接返回
        if (tablePanel != null) {
            return tablePanel
        }
        
        // 否则尝试通过父组件查找
        // 由于 TableGenConfigDialog 继承自 DialogWrapper，我们需要通过其他方式查找
        // 最简单的方法是遍历组件树，查找包含 tablePanel 的对话框
        var current: java.awt.Container? = this
        while (current != null) {
            // 查找当前组件的所有子组件
            for (component in current.components) {
                if (component is TableConfigPanel) {
                    return component
                }
                if (component is java.awt.Container) {
                    // 递归查找
                    val found = findTablePanel(component)
                    if (found != null) return found
                }
            }
            current = current.parent
        }
        return null
    }
    
    private fun findTablePanel(container: java.awt.Container): TableConfigPanel? {
        for (component in container.components) {
            if (component is TableConfigPanel) {
                return component
            }
            if (component is java.awt.Container) {
                val found = findTablePanel(component)
                if (found != null) return found
            }
        }
        return null
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
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoNodeInterface")), pojoNodeInterfaceField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoNodeInterfaceList")), pojoNodeInterfaceListField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoVersionInterface")), pojoVersionInterfaceField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoVersionInterfaceList")), pojoVersionInterfaceListField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoStateInterface")), pojoStateInterfaceField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoStateInterfaceList")), pojoStateInterfaceListField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoUuidInterface")), pojoUuidInterfaceField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoUuidInterfaceList")), pojoUuidInterfaceListField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoModifyDateInterface")), pojoModifyDateInterfaceField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("advanced.label.pojoModifyDateInterfaceList")), pojoModifyDateInterfaceListField)
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

