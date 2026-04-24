package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.TableGearBundle
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.*
import javax.swing.text.JTextComponent

/**
 * 代码生成配置面板
 */
class CodeGenConfigPanel(private val model: TableGenConfigModel) : JPanel() {
    
    // 目录配置
    private lateinit var packageNameField: JTextComponent
    private lateinit var srcDirField: JTextComponent
    private lateinit var testDirField: JTextComponent
    private lateinit var resourceDirField: JTextComponent
    private lateinit var jspDirField: JTextComponent
    private lateinit var serviceDirField: JTextComponent
    private lateinit var controllerDirField: JTextComponent
    private lateinit var dtoDirField: JTextComponent
    private lateinit var cmdDirField: JTextComponent
    
    // 功能选项
    private lateinit var indexesCheckBox: JBCheckBox
    private lateinit var foreignKeysCheckBox: JBCheckBox
    private lateinit var r2dbcCheckBox: JBCheckBox
    private lateinit var readWriteSeparateCheckBox: JBCheckBox
    private lateinit var useCustomMetaTableCheckBox: JBCheckBox
    private lateinit var useAnnotationCheckBox: JBCheckBox
    private lateinit var useSwaggerCheckBox: JBCheckBox
    private lateinit var useAutoincrementCheckBox: JBCheckBox
    private lateinit var generateProceduresCheckBox: JBCheckBox
    private lateinit var generateAllCheckBox: JBCheckBox
    private lateinit var overWriteAllCheckBox: JBCheckBox
    private lateinit var enableMergeCheckBox: JBCheckBox
    
    private lateinit var apiDocProviderCombo: JComboBox<String>
    private lateinit var swaggerVersionField: JTextComponent
    
    init {
        initializePanel()
    }
    
    private fun initializePanel() {
        // 初始化字段
        packageNameField = JBTextField()
        srcDirField = JBTextField()
        testDirField = JBTextField()
        resourceDirField = JBTextField()
        jspDirField = JBTextField()
        serviceDirField = JBTextField()
        controllerDirField = JBTextField()
        dtoDirField = JBTextField()
        cmdDirField = JBTextField()
        
        indexesCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.indexes"))
        foreignKeysCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.foreignKeys"))
        r2dbcCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.r2dbc"))
        readWriteSeparateCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.readWriteSeparate"))
        useCustomMetaTableCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.useCustomMetaTable"))
        useAnnotationCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.useAnnotation"))
        useSwaggerCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.useSwagger"))
        useAutoincrementCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.useAutoincrement"))
        generateProceduresCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.generateProcedures"))
        generateAllCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.generateAll"))
        overWriteAllCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.overwriteAll"))
        enableMergeCheckBox = JBCheckBox(TableGearBundle.message("config.checkbox.enableMerge"))
        
        apiDocProviderCombo = JComboBox(arrayOf("springdoc", "swagger"))
        swaggerVersionField = JBTextField()
        apiDocProviderCombo.addActionListener {
            updateApiDocDependentState()
        }
        
        val form = FormBuilder.createFormBuilder()
            .addComponent(JBLabel(TableGearBundle.message("config.section.options")).apply { font = font.deriveFont(java.awt.Font.BOLD) })
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.packageName")), packageNameField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.srcDir")), srcDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.testDir")), testDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.resourceDir")), resourceDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.jspDir")), jspDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.serviceDir")), serviceDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.controllerDir")), controllerDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.dtoDir")), dtoDirField)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.cmdDir")), cmdDirField)
            .addComponent(JBLabel(TableGearBundle.message("config.section.codegen")).apply { font = font.deriveFont(java.awt.Font.BOLD) })
            .addComponent(indexesCheckBox)
            .addComponent(foreignKeysCheckBox)
            .addComponent(r2dbcCheckBox)
            .addComponent(readWriteSeparateCheckBox)
            .addComponent(useCustomMetaTableCheckBox)
            .addComponent(useAnnotationCheckBox)
            .addComponent(useSwaggerCheckBox)
            .addComponent(useAutoincrementCheckBox)
            .addComponent(generateProceduresCheckBox)
            .addComponent(generateAllCheckBox)
            .addComponent(overWriteAllCheckBox)
            .addComponent(enableMergeCheckBox)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.apiDocProvider")), apiDocProviderCombo)
            .addLabeledComponent(JBLabel(TableGearBundle.message("config.label.swaggerVersion")), swaggerVersionField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        layout = java.awt.BorderLayout()
        add(JScrollPane(form), java.awt.BorderLayout.CENTER)
    }
    
    fun updateFromModel() {
        packageNameField.text = model.getProperty("package_name") ?: ""
        srcDirField.text = model.getProperty("src_dir") ?: ""
        testDirField.text = model.getProperty("test_dir") ?: ""
        resourceDirField.text = model.getProperty("resource_dir") ?: ""
        jspDirField.text = model.getProperty("jsp_dir") ?: ""
        serviceDirField.text = model.getProperty("service_dir") ?: ""
        controllerDirField.text = model.getProperty("controller_dir") ?: ""
        dtoDirField.text = model.getProperty("dto_dir") ?: ""
        cmdDirField.text = model.getProperty("cmd_dir") ?: ""
        
        indexesCheckBox.isSelected = model.getBooleanProperty("indexes")
        foreignKeysCheckBox.isSelected = model.getBooleanProperty("foreign_keys")
        r2dbcCheckBox.isSelected = model.getBooleanProperty("r2dbc")
        readWriteSeparateCheckBox.isSelected = model.getBooleanProperty("read_write_separate")
        useCustomMetaTableCheckBox.isSelected = model.getBooleanProperty("use_custom_meta_table")
        useAnnotationCheckBox.isSelected = model.getBooleanProperty("use_annotation")
        useSwaggerCheckBox.isSelected = model.getBooleanProperty("use_swagger")
        useAutoincrementCheckBox.isSelected = model.getBooleanProperty("use_autoincrement")
        generateProceduresCheckBox.isSelected = model.getBooleanProperty("generate_procedures")
        generateAllCheckBox.isSelected = model.getBooleanProperty("generate_all")
        overWriteAllCheckBox.isSelected = model.getBooleanProperty("over_write_all")
        enableMergeCheckBox.isSelected = model.getBooleanProperty("enable_merge")
        apiDocProviderCombo.setSelectedItem(model.getProperty("api_doc_provider") ?: "springdoc")
        swaggerVersionField.text = model.getProperty("swagger_version") ?: "2"
        updateApiDocDependentState()
    }
    
    fun updateToModel() {
        model.setProperty("package_name", packageNameField.text)
        model.setProperty("src_dir", srcDirField.text)
        model.setProperty("test_dir", testDirField.text)
        model.setProperty("resource_dir", resourceDirField.text)
        model.setProperty("jsp_dir", jspDirField.text)
        model.setProperty("service_dir", serviceDirField.text)
        model.setProperty("controller_dir", controllerDirField.text)
        model.setProperty("dto_dir", dtoDirField.text)
        model.setProperty("cmd_dir", cmdDirField.text)
        
        model.setBooleanProperty("indexes", indexesCheckBox.isSelected)
        model.setBooleanProperty("foreign_keys", foreignKeysCheckBox.isSelected)
        model.setBooleanProperty("r2dbc", r2dbcCheckBox.isSelected)
        model.setBooleanProperty("read_write_separate", readWriteSeparateCheckBox.isSelected)
        model.setBooleanProperty("use_custom_meta_table", useCustomMetaTableCheckBox.isSelected)
        model.setBooleanProperty("use_annotation", useAnnotationCheckBox.isSelected)
        model.setBooleanProperty("use_swagger", useSwaggerCheckBox.isSelected)
        model.setBooleanProperty("use_autoincrement", useAutoincrementCheckBox.isSelected)
        model.setBooleanProperty("generate_procedures", generateProceduresCheckBox.isSelected)
        model.setBooleanProperty("generate_all", generateAllCheckBox.isSelected)
        model.setBooleanProperty("over_write_all", overWriteAllCheckBox.isSelected)
        model.setBooleanProperty("enable_merge", enableMergeCheckBox.isSelected)
        model.setProperty("api_doc_provider", apiDocProviderCombo.selectedItem?.toString())
        model.setProperty("swagger_version", swaggerVersionField.text)
    }

    private fun updateApiDocDependentState() {
        val provider = apiDocProviderCombo.selectedItem?.toString() ?: "springdoc"
        val useSwaggerControls = provider == "swagger"
        useSwaggerCheckBox.isEnabled = useSwaggerControls
        swaggerVersionField.isEnabled = useSwaggerControls
        if (!useSwaggerControls) {
            useSwaggerCheckBox.isSelected = false
        }
    }
}

