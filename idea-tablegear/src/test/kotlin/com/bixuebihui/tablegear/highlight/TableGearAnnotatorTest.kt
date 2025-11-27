package com.bixuebihui.tablegear.highlight

import com.bixuebihui.tablegear.inspection.TableGearValidationInspection
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test
import kotlin.test.assertTrue

class TableGearAnnotatorTest : LightPlatformCodeInsightFixture4TestCase() {

    @Test
    fun testContinuationLineDoesNotProduceUnknownKeyWarning() {
        myFixture.enableInspections(TableGearValidationInspection())
        myFixture.configureByText(
            "tablegen.properties",
            """
            view_list=view_a: select * from dual where a = 1 \
              and b = 2;
            """.trimIndent()
        )
        val highlights = myFixture.doHighlighting()
        val warnings = highlights.filter {
            it.severity == HighlightSeverity.WARNING && it.description?.contains("未识别的配置项") == true
        }
        assertTrue(warnings.isEmpty(), "Continuation lines should not trigger unknown key warnings")

        val keyValueErrors = highlights.filter { it.description?.contains("key=value") == true }
        assertTrue(keyValueErrors.isEmpty(), "Continuation lines should not trigger key=value errors")
    }
}

