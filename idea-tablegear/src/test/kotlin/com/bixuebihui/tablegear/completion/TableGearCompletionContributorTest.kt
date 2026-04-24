package com.bixuebihui.tablegear.completion

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TableGearCompletionContributorTest : LightPlatformCodeInsightFixture4TestCase() {

    @Test
    fun testClassNameDriverSuggestions() {
        myFixture.configureByText(
            "tablegen.properties",
            """
            className=<caret>
            """.trimIndent()
        )

        val lookups = myFixture.completeBasic()
        assertNotNull(lookups, "应返回 className 的驱动补全项")
        val lookupStrings = lookups.map { it.lookupString }
        assertTrue(
            "com.mysql.cj.jdbc.Driver" in lookupStrings,
            "补全结果中应该包含 com.mysql.cj.jdbc.Driver"
        )
    }

    @Test
    fun testCaretPlacedAfterEqualsForKeyCompletion() {
        myFixture.configureByText(
            "tablegen.properties",
            "<caret>"
        )

        val lookups = myFixture.completeBasic()
        val target = lookups?.firstOrNull { it.lookupString == "package_name" }
        assertNotNull(target, "应能补全 package_name 键")
        myFixture.lookup.currentItem = target
        myFixture.type('\n')

        val documentText = myFixture.editor.document.text
        assertEquals("package_name=", documentText)
        assertEquals(
            "光标应落在等号之后",
            documentText.length,
            myFixture.editor.caretModel.offset
        )
    }

    @Test
    fun testTypeValueSuggestionsIncludeAllModes() {
        myFixture.configureByText(
            "tablegen.properties",
            "type=<caret>"
        )

        val lookups = myFixture.completeBasic()
        assertNotNull(lookups, "type 应该提供枚举补全")
        val lookupStrings = lookups.map { it.lookupString }.toSet()
        listOf("database", "elasticsearch_mapping", "elasticsearch_sample", "json").forEach { value ->
            assertTrue(
                lookupStrings.contains(value),
                "type 补全缺少 $value"
            )
        }
    }

    @Test
    fun testApiDocProviderSuggestions() {
        myFixture.configureByText(
            "tablegen.properties",
            "api_doc_provider=<caret>"
        )

        val lookups = myFixture.completeBasic()
        assertNotNull(lookups, "api_doc_provider 应该提供枚举补全")
        val lookupStrings = lookups.map { it.lookupString }.toSet()
        listOf("swagger", "springdoc").forEach { value ->
            assertTrue(
                lookupStrings.contains(value),
                "api_doc_provider 补全缺少 $value"
            )
        }
    }
}

