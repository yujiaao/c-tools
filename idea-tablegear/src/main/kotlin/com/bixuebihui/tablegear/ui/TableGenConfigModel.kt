package com.bixuebihui.tablegear.ui

import com.bixuebihui.tablegear.ConfigMode
import com.bixuebihui.tablegear.PropertyDefinition
import com.bixuebihui.tablegear.TableGearProperties
import java.util.*

/**
 * TableGen配置模型
 * 用于在UI中编辑和保存配置
 */
class TableGenConfigModel {
    private val properties = mutableMapOf<String, String>()
    
    /**
     * 从Properties对象加载配置
     */
    fun loadFromProperties(props: Properties) {
        properties.clear()
        props.stringPropertyNames().forEach { key ->
            properties[key] = props.getProperty(key)
        }
    }
    
    /**
     * 保存配置到Properties对象
     */
    fun saveToProperties(): Properties {
        val props = Properties()
        properties.forEach { (key, value) ->
            if (value.isNotBlank()) {
                props.setProperty(key, value)
            }
        }
        return props
    }
    
    /**
     * 获取配置值
     */
    fun getProperty(key: String): String? = properties[key]
    
    /**
     * 设置配置值
     */
    fun setProperty(key: String, value: String?) {
        if (value.isNullOrBlank()) {
            properties.remove(key)
        } else {
            properties[key] = value.trim()
        }
    }
    
    /**
     * 获取布尔配置值
     */
    fun getBooleanProperty(key: String): Boolean {
        val value = properties[key]?.lowercase()
        return value == "yes" || value == "true"
    }
    
    /**
     * 设置布尔配置值
     */
    fun setBooleanProperty(key: String, value: Boolean) {
        if (value) {
            properties[key] = "yes"
        } else {
            properties.remove(key)
        }
    }
    
    /**
     * 获取所有配置项
     */
    fun getAllProperties(): Map<String, String> = properties.toMap()
    
    /**
     * 获取指定模式的配置项
     */
    fun getPropertiesForMode(mode: ConfigMode): Map<String, String> {
        val modeProps = TableGearProperties.getPropertiesForMode(mode)
        val modeKeys = modeProps.map { it.key }.toSet()
        return properties.filterKeys { modeKeys.contains(it) }
    }
    
    /**
     * 获取配置模式
     */
    fun getConfigMode(): ConfigMode {
        val typeValue = properties["type"]
        return TableGearProperties.getConfigMode(typeValue)
    }
    
    /**
     * 检查配置是否已修改
     */
    fun isModified(original: Properties): Boolean {
        val originalMap = original.stringPropertyNames().associateWith { original.getProperty(it) }
        return properties != originalMap
    }
}

