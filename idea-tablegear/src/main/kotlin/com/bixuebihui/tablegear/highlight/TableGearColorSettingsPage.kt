package com.bixuebihui.tablegear.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.bixuebihui.tablegear.TableGearIcons
import javax.swing.Icon

class TableGearColorSettingsPage : ColorSettingsPage {
    override fun getHighlighter(): SyntaxHighlighter = TableGearSyntaxHighlighter()
    
    override fun getDemoText(): String = """
        # TableGear Configuration
        alias=myDataSource
        package_name=com.example
        add_prefix=My
        remove_prefix=tb_
        className=com.mysql.cj.jdbc.Driver
        dburl=jdbc:mysql://localhost:3306/testdb
        username=root
        password=secret
        view_list=my_view: select * from table1; another_view: select id, name from users;
        encrypted_fields=table1:field1,field2;table2:field3,field4;
        type=elasticsearch_mapping
        url=http://localhost:9200
        index_list=my-index:alias
    """.trimIndent()
    
    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
    
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor("Key", TableGearColors.KEY),
        AttributesDescriptor("Database Key", TableGearColors.DATABASE_KEY),
        AttributesDescriptor("Path Key", TableGearColors.PATH_KEY),
        AttributesDescriptor("Boolean Key", TableGearColors.BOOLEAN_KEY),
        AttributesDescriptor("List Key", TableGearColors.LIST_KEY),
        AttributesDescriptor("Package Name Key", TableGearColors.PACKAGE_NAME_KEY),
        AttributesDescriptor("View List Key", TableGearColors.VIEW_LIST_KEY),
        AttributesDescriptor("View Separator", TableGearColors.VIEW_SEPARATOR),
        AttributesDescriptor("Assignment (=)", TableGearColors.ASSIGNMENT),
        AttributesDescriptor("View Name", TableGearColors.VIEW_NAME),
        AttributesDescriptor("SQL", TableGearColors.SQL),
        AttributesDescriptor("Sensitive Key", TableGearColors.SENSITIVE_KEY),
        AttributesDescriptor("Sensitive Field", TableGearColors.SENSITIVE_FIELD),
        AttributesDescriptor("Value", TableGearColors.VALUE),
        AttributesDescriptor("Comment", TableGearColors.COMMENT)
    )
    
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    
    override fun getDisplayName(): String = "TableGear"
    
    override fun getIcon(): Icon? = TableGearIcons.FILE
}

