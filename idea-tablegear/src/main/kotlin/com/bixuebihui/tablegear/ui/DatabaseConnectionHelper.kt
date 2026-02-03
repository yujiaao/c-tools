package com.bixuebihui.tablegear.ui

import com.bixuebihui.dbcon.DatabaseConfig
import com.bixuebihui.tablegen.TableGen
import com.bixuebihui.tablegen.TableUtils
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.util.*

/**
 * 数据库连接辅助类
 * 用于连接数据库并获取表列表
 */
class DatabaseConnectionHelper(private val model: TableGenConfigModel) {
    
    private var connection: Connection? = null
    private var metaData: DatabaseMetaData? = null
    
    /**
     * 测试数据库连接
     */
    fun testConnection(): Boolean {
        return try {
            val props = createDatabaseProperties()
            val dbConfig = DatabaseConfig.newInstance(props)
            val helper = TableGen.getDbHelper(dbConfig)
            connection = helper.getConnection()
            metaData = connection?.metaData
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 获取所有表名（包括表和视图）
     */
    fun getAllTables(): List<TableInfo> {
        val tables = mutableListOf<TableInfo>()
        
        try {
            val meta = metaData ?: throw IllegalStateException("数据库未连接")
            val catalog = model.getProperty("catalog")
            val schema = model.getProperty("schema")
            val tableOwner = model.getProperty("table_owner")
            
            // 获取所有表（包括视图）
            val tableNames = TableUtils.getTableData(
                meta, catalog, schema, tableOwner, 
                emptyMap(), emptyMap() // 不应用过滤，获取所有表
            )
            
            // 获取表类型信息
            val tableTypes = arrayOf("TABLE", "VIEW")
            val rs = meta.getTables(catalog, schema, null, tableTypes)
            
            val tableTypeMap = mutableMapOf<String, String>()
            while (rs.next()) {
                val tableName = rs.getString("TABLE_NAME")
                val tableType = rs.getString("TABLE_TYPE")
                tableTypeMap[tableName] = tableType
            }
            rs.close()
            
            // 构建表信息列表
            tableNames.forEach { tableName ->
                val tableType = tableTypeMap[tableName] ?: "TABLE"
                tables.add(TableInfo(tableName, tableType == "VIEW"))
            }
            
            // 按名称排序
            tables.sortBy { it.name }
            
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("获取表列表失败: ${e.message}", e)
        }
        
        return tables
    }
    
    /**
     * 关闭数据库连接
     */
    fun close() {
        try {
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection = null
            metaData = null
        }
    }
    
    /**
     * 创建数据库连接所需的Properties
     */
    private fun createDatabaseProperties(): Properties {
        val props = Properties()
        model.getProperty("className")?.let { props.setProperty("className", it) }
        model.getProperty("dburl")?.let { props.setProperty("dburl", it) }
        model.getProperty("username")?.let { props.setProperty("username", it) }
        model.getProperty("password")?.let { props.setProperty("password", it) }
        return props
    }
    
    /**
     * 表信息数据类
     */
    data class TableInfo(
        val name: String,
        val isView: Boolean = false
    )
}

