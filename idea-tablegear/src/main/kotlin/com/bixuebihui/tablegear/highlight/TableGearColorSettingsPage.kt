package com.bixuebihui.tablegear.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.bixuebihui.tablegear.TableGearIcons
import java.awt.Color
import java.awt.Font
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
    
    /**
     * 提供默认颜色设置
     * 当用户未自定义颜色时，使用这些默认值
     */
    fun getDefaultColors(): Map<TextAttributesKey, TextAttributes> {
        return mapOf(
            TableGearColors.KEY to textAttr("#F8F8F2"),
            TableGearColors.DATABASE_KEY to textAttr("#FFD866"),
            TableGearColors.PATH_KEY to textAttr("#A9DC76"),
            TableGearColors.BOOLEAN_KEY to textAttr("#FF6188", bold = true),
            TableGearColors.LIST_KEY to textAttr("#78DCE8"),
            TableGearColors.PACKAGE_NAME_KEY to textAttr("#7BD88F"),
            TableGearColors.VIEW_LIST_KEY to textAttr("#55CFE3", bold = true),
            TableGearColors.VIEW_SEPARATOR to textAttr("#AB9DF2", bold = true),
            TableGearColors.ASSIGNMENT to textAttr("#AB9DF2", bold = true),
            TableGearColors.VIEW_NAME to textAttr("#00FFFF", bold = true),
            TableGearColors.SQL to textAttr("#A6E22E"),
            TableGearColors.SENSITIVE_KEY to textAttr("#FF9B5E"),
            TableGearColors.SENSITIVE_FIELD to textAttr("#FFB347"),
            TableGearColors.VALUE to textAttr("#A6E22E"),
            TableGearColors.COMMENT to textAttr("#035813")
        )
    }
    
    private fun textAttr(hex: String, bold: Boolean = false): TextAttributes {
        val color = Color.decode(hex)
        val fontStyle = if (bold) Font.BOLD else Font.PLAIN
        return TextAttributes(color, null, null, null, fontStyle)
    }
}

