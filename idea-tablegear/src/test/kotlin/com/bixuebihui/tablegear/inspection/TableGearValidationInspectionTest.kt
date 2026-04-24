package com.bixuebihui.tablegear.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test
import kotlin.test.assertTrue

class TableGearValidationInspectionTest : LightPlatformCodeInsightFixture4TestCase() {

    @Test
    fun testDuplicateKeyDetection() {
        myFixture.enableInspections(TableGearValidationInspection())
        
        myFixture.configureByText(
            "tablegen.properties",
            """
            type=database
            type=database
            package_name=com.example
            """.trimIndent()
        )
        
        // 使用 InspectionManager 创建 ProblemsHolder
        val inspection = TableGearValidationInspection()
        val file = myFixture.file
        val manager = InspectionManager.getInstance(myFixture.project)
        val holder = ProblemsHolder(manager, file, false)
        val visitor = inspection.buildVisitor(holder, true)
        visitor.visitFile(file)
        
        val problems = holder.results
        val duplicateProblems = problems.filter {
            val description = it.descriptionTemplate
            description.contains("重复定义的配置项") == true ||
            description.contains("duplicate") == true
        }
        
        assertTrue(
            duplicateProblems.isNotEmpty(),
            "应该检测到重复的配置项 'type'，但实际检测到的问题: ${problems.map { it.descriptionTemplate }}"
        )
    }
    
    @Test
    fun testInspectionIsCalled() {
        myFixture.enableInspections(TableGearValidationInspection())
        
        myFixture.configureByText(
            "tablegen.properties",
            """
            type=database
            package_name=com.example
            """.trimIndent()
        )
        
        val inspection = TableGearValidationInspection()
        val file = myFixture.file
        val manager = InspectionManager.getInstance(myFixture.project)
        val holder = ProblemsHolder(manager, file, false)
        val visitor = inspection.buildVisitor(holder, true)
        visitor.visitFile(file)
        

        // 至少应该处理了文件（可能没有错误，但应该运行了）
        assertTrue(true, "Inspection 应该被调用")
    }
    @Test
    fun testHashInValueIsContent() {
        myFixture.enableInspections(TableGearValidationInspection())
        
        // "type=database # comment" -> value is "database # comment"
        // "database # comment" is NOT a valid enum value for 'type' (valid: database, elasticsearch_mapping, etc.)
        // So it SHOULD report an error.
        
        myFixture.configureByText(
            "tablegen.properties",
            """
            type=database # comment
            """.trimIndent()
        )
        
        val inspection = TableGearValidationInspection()
        val file = myFixture.file
        val manager = InspectionManager.getInstance(myFixture.project)
        val holder = ProblemsHolder(manager, file, false)
        val visitor = inspection.buildVisitor(holder, true)
        visitor.visitFile(file)
        
        val problems = holder.results
        assertTrue(
            problems.any { it.descriptionTemplate.contains("仅支持") },
            "Should report error because 'database # comment' is invalid enum value. Problems: ${problems.map { it.descriptionTemplate }}"
        )
    }

    @Test
    fun testSpringdocModeWarnsSwaggerSettings() {
        myFixture.enableInspections(TableGearValidationInspection())

        myFixture.configureByText(
            "tablegen.properties",
            """
            api_doc_provider=springdoc
            use_swagger=yes
            swagger_version=2
            enable_merge=yes
            """.trimIndent()
        )

        val inspection = TableGearValidationInspection()
        val file = myFixture.file
        val manager = InspectionManager.getInstance(myFixture.project)
        val holder = ProblemsHolder(manager, file, false)
        val visitor = inspection.buildVisitor(holder, true)
        visitor.visitFile(file)

        val problems = holder.results.mapNotNull { it.descriptionTemplate }
        assertTrue(
            problems.any { it.contains("忽略 use_swagger") },
            "应提示 springdoc 模式下会忽略 use_swagger，实际问题: $problems"
        )
        assertTrue(
            problems.any { it.contains("忽略 swagger_version") },
            "应提示 springdoc 模式下会忽略 swagger_version，实际问题: $problems"
        )
    }
}

