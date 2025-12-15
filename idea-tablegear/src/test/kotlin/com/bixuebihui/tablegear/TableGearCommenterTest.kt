package com.bixuebihui.tablegear

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test
import kotlin.test.assertEquals

class TableGearCommenterTest : LightPlatformCodeInsightFixture4TestCase() {

    @Test
    fun testToggleLineCommentUsesHash() {
        val file = myFixture.configureByText(
            "tablegen.properties",
            "package_name=com.demo"
        )
        myFixture.performEditorAction("CommentByLineComment")
        val updated = myFixture.getDocument(file).text.trim()
        assertEquals("#package_name=com.demo", updated)
        myFixture.performEditorAction("CommentByLineComment")
        val toggledBack = myFixture.getDocument(file).text.trim()
        assertEquals("package_name=com.demo", toggledBack)
    }
}

