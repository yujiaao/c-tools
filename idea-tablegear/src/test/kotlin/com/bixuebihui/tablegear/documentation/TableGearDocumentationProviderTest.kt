package com.bixuebihui.tablegear.documentation

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TableGearDocumentationProviderTest : LightPlatformCodeInsightFixture4TestCase() {

    @Test
    fun testViewListDocumentationMentionsFileReferenceSyntax() {
        val file = myFixture.configureByText(
            "tablegen.properties",
            "view_list=view_a: select * from dual;\n"
        )
        val keyOffset = file.text.indexOf("view_list") + 2 // middle of key
        val element = file.findElementAt(keyOffset)
        val provider = TableGearDocumentationProvider()
        val doc = provider.generateDoc(element!!, element)
        assertNotNull(doc, "Hover documentation should be generated for view_list")
        assertTrue(
            doc!!.contains("@relative/path.sql"),
            "Documentation should mention @relative/path.sql usage"
        )
    }

    @Test
    fun testNonTableFileReturnsNullDocumentation() {
        val file = myFixture.configureByText(
            "application.properties",
            "package_name=com.demo"
        )
        val element = file.findElementAt(file.text.indexOf("package_name") + 2)
        val provider = TableGearDocumentationProvider()
        val doc = provider.generateDoc(element!!, element)
        assertTrue(doc == null, "Non tablegen file should not trigger documentation")
    }

    @Test
    fun testContinuationLineStillFindsKey() {
        val file = myFixture.configureByText(
            "tablegen.properties",
            """
            view_list=view_a: select * from dual where a = 1 \
              and b = 2;
            """.trimIndent()
        )
        val offset = file.text.indexOf("and b")
        val element = file.findElementAt(offset)
        val provider = TableGearDocumentationProvider()
        val doc = provider.generateDoc(element!!, element)
        assertNotNull(doc, "Continuation lines should resolve to the original key")
    }

    @Test
    fun testApiDocProviderDocumentationMentionsSpringdocBehavior() {
        val file = myFixture.configureByText(
            "tablegen.properties",
            "api_doc_provider=springdoc\n"
        )
        val keyOffset = file.text.indexOf("api_doc_provider") + 2
        val element = file.findElementAt(keyOffset)
        val provider = TableGearDocumentationProvider()
        val doc = provider.generateDoc(element!!, element)
        assertNotNull(doc, "Hover documentation should be generated for api_doc_provider")
        assertTrue(
            doc!!.contains("springdoc"),
            "Documentation should mention springdoc option"
        )
    }
}

