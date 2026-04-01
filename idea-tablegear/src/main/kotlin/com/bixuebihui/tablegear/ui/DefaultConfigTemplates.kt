package com.bixuebihui.tablegear.ui

import java.util.*

/**
 * 默认配置文件模板
 * 用于“生成默认配置”功能，生成数据库或 ES 的初始 properties 内容
 */
object DefaultConfigTemplates {

    /**
     * 数据库模式默认配置
     */
    fun databaseDefaultProperties(): Properties {
        val p = Properties()
        p.setProperty("type", "database")
        p.setProperty("alias", "default")
        p.setProperty("package_name", "com.example")
        p.setProperty("src_dir", "src/main/java")
        p.setProperty("className", "com.mysql.cj.jdbc.Driver")
        p.setProperty("dburl", "jdbc:mysql://localhost:3306/your_db?useUnicode=true&characterEncoding=UTF-8")
        p.setProperty("username", "root")
        p.setProperty("password", "")
        p.setProperty("generate_all", "yes")
        p.setProperty("enable_merge", "yes")
        return p
    }

    /**
     * ES 模式默认配置
     */
    fun esDefaultProperties(): Properties {
        val p = Properties()
        p.setProperty("type", "elasticsearch_mapping")
        p.setProperty("alias", "es-default")
        p.setProperty("package_name", "com.example")
        p.setProperty("src_dir", "src/main/java")
        p.setProperty("url", "http://localhost:9200")
        p.setProperty("index_list", "my_index")
        p.setProperty("primary_key_fields", "id")
        p.setProperty("generate_all", "yes")
        p.setProperty("enable_merge", "yes")
        return p
    }

    /**
     * 根据是否为 ES 模式返回对应默认 Properties
     */
    fun forMode(isEs: Boolean): Properties = if (isEs) esDefaultProperties() else databaseDefaultProperties()
}
