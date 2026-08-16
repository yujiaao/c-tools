package com.bixuebihui.tablegear.documentation

import com.bixuebihui.tablegear.TableGearBundle
import com.bixuebihui.tablegear.TableGearProperties
import com.bixuebihui.tablegear.ValueType
import com.bixuebihui.tablegear.extractPropertyKey
import com.bixuebihui.tablegear.isInTableGearFile
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement

class TableGearDocumentationProvider : DocumentationProvider {
    private val logger = Logger.getInstance(TableGearDocumentationProvider::class.java)

    override fun getQuickNavigateInfo(element: PsiElement, originalElement: PsiElement): String? {
        return buildDocumentation(originalElement) ?: buildDocumentation(element)
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return buildDocumentation(originalElement ?: element)
    }

    private fun buildDocumentation(element: PsiElement?): String? {
        if (element == null) {
            logger.debug("TableGearDoc: element is null, skip documentation.")
            return null
        }
        if (!element.isInTableGearFile()) {
            logger.debug("TableGearDoc: skip non-tablegear file ${element.containingFile?.name}")
            return null
        }
        val key = element.extractPropertyKey()
        if (key == null) {
            logger.debug("TableGearDoc: unable to resolve key for element=${element.javaClass.simpleName}, text='${element.text}'")
            return null
        }
        val prop = TableGearProperties.PROPERTY_MAP[key] ?: return null
        logger.debug("TableGearDoc: providing documentation for key=$key")
        val builder = StringBuilder()
        builder.append("<b>${prop.key}</b><br/>")
        builder.append("<i>${prop.valueType.name.lowercase()}</i><br/><br/>")
        builder.append(prop.description)

        builder.append("<br/><br/>${TableGearBundle.message("doc.typeLabel")}<code>${prop.valueType.name.lowercase()}</code>")

        when (prop.valueType) {
            ValueType.BOOLEAN -> builder.append("<br/>${TableGearBundle.message("doc.validValues")}<code>yes</code> / <code>no</code> / <code>true</code> / <code>false</code>")
            ValueType.ENUM -> {
                if (!prop.enumValues.isNullOrEmpty()) {
                    builder.append("<br/>${TableGearBundle.message("doc.enumValues")}")
                    builder.append(prop.enumValues.joinToString("、") { "<code>$it</code>" })
                }
            }
            else -> {}
        }
        return builder.toString()
    }

}


