package com.bixuebihui.tablegear.ui

import com.bixuebihui.dbcon.DatabaseConfig
import com.bixuebihui.tablegear.TableGearBundle
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
        try {
            // 检查 Java 版本
            val javaVersion = System.getProperty("java.version")
            val majorVersion = javaVersion.split(".").getOrNull(0)?.toIntOrNull() ?: 
                              javaVersion.split(".").getOrNull(1)?.toIntOrNull() ?: 0
            
            if (majorVersion < 11) {
                throw RuntimeException(
                    TableGearBundle.message("dbHelper.javaIncompatible.simple", javaVersion)
                )
            }
            
            val props = createDatabaseProperties()
            
            // 验证必要的配置
            val className = props.getProperty("className")
            val dburl = props.getProperty("dburl")
            
            if (className.isNullOrBlank()) {
                throw IllegalArgumentException(TableGearBundle.message("dbHelper.classNameRequired"))
            }
            if (dburl.isNullOrBlank()) {
                throw IllegalArgumentException(TableGearBundle.message("dbHelper.dburlRequired"))
            }
            
            // 优先用插件 ClassLoader；若未打包则尝试从“已下载驱动”目录加载（见 JdbcDriverDownloadService）
            val pluginLoader = this.javaClass.classLoader
            val driverLoader = try {
                JdbcDriverDownloadService.ensureDriverLoaded(className, pluginLoader)
            } catch (e: ClassNotFoundException) {
                val downloadHint = if (JdbcDriverDownloadService.isDownloadable(className)) {
                    TableGearBundle.message("dbHelper.driverDownloadHint")
                } else ""
                throw RuntimeException(
                    TableGearBundle.message("dbHelper.driverLoadFailed", className, downloadHint),
                    e
                )
            }
            val prevLoader = Thread.currentThread().contextClassLoader
            try {
                Thread.currentThread().contextClassLoader = driverLoader
                val dbConfig = DatabaseConfig.newInstance(props)
                val helper = TableGen.getDbHelper(dbConfig)

                // 创建连接
                connection = helper.getConnection()

                // 验证连接是否有效
                if (connection == null) {
                    throw RuntimeException(TableGearBundle.message("dbHelper.nullConnection"))
                }

                if (connection!!.isClosed) {
                    throw RuntimeException(TableGearBundle.message("dbHelper.connectionClosed"))
                }

                // 获取元数据（这会验证连接是否真正可用）
                metaData = connection!!.metaData

                // 简单验证：尝试获取数据库产品名称
                try {
                    metaData?.databaseProductName
                } catch (e: Exception) {
                    throw RuntimeException(TableGearBundle.message("dbHelper.connectionInvalid", e.message ?: "unknown"), e)
                }

                return true
            } finally {
                Thread.currentThread().contextClassLoader = prevLoader
            }
        } catch (e: UnsupportedClassVersionError) {
            // Java 版本不兼容错误
            val errorMessage = e.message ?: ""
            val requiredVersion = if (errorMessage.isNotEmpty()) {
                val match = Regex("class file version (\\d+)").find(errorMessage)
                match?.groupValues?.get(1)?.let { version ->
                    when (version.toInt()) {
                        61 -> "Java 17"
                        55 -> "Java 11"
                        else -> TableGearBundle.message("dbHelper.versionLabel", version)
                    }
                } ?: TableGearBundle.message("common.unknown")
            } else {
                TableGearBundle.message("common.unknown")
            }
            throw RuntimeException(
                TableGearBundle.message("dbHelper.javaIncompatible.detail", errorMessage, requiredVersion, System.getProperty("java.version")),
                e
            )
        } catch (e: Exception) {
            // 重新抛出异常以便上层可以获取详细错误信息
            val errorMsg = buildString {
                append(TableGearBundle.message("dbHelper.connectFailedHeader"))
                append(TableGearBundle.message("dbHelper.errorType", e.javaClass.simpleName))
                append(TableGearBundle.message("dbHelper.errorMessage", e.message ?: "unknown"))
                
                if (e.cause != null) {
                    append(TableGearBundle.message("dbHelper.errorCause", e.cause?.message ?: "unknown"))
                }
                
                append(TableGearBundle.message("dbHelper.checkList"))
                
                // 如果是 ClassNotFoundException，提供额外提示
                if (e is ClassNotFoundException) {
                    append(TableGearBundle.message("dbHelper.classNotFoundHint"))
                }
                
                append(TableGearBundle.message("dbHelper.detailError"))
                append(e.stackTraceToString())
            }
            throw RuntimeException(errorMsg, e)
        }
    }
    
    /**
     * 获取所有表名（包括表和视图）
     */
    fun getAllTables(): List<TableInfo> {
        val tables = mutableListOf<TableInfo>()
        
        try {
            val meta = metaData ?: throw IllegalStateException(TableGearBundle.message("dbHelper.notConnected"))
            val catalog = model.getProperty("catalog")
            val schema = model.getProperty("schema")
            val tableOwner = model.getProperty("table_owner")
            
            // 获取所有表（包括视图）
            val tableNames = TableUtils.getTableData(
                meta, catalog, schema, tableOwner, 
                null, null // 不应用过滤，获取所有表
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
            throw RuntimeException(TableGearBundle.message("dbHelper.loadTablesFailed", e.message ?: "unknown"), e)
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
    
    data class ColumnInfo(
        val name: String,
        val type: String,
        val nullable: Boolean,
        val autoIncrement: Boolean
    )
    
    /**
     * 获取指定表的字段列表
     */
    fun getTableColumns(tableName: String): List<ColumnInfo> {
        if (connection == null || metaData == null) {
            throw IllegalStateException(TableGearBundle.message("dbHelper.notConnectedPleaseConnect"))
        }
        
        val columns = mutableListOf<ColumnInfo>()
        try {
            val catalog = model.getProperty("catalog")
            val schema = model.getProperty("schema")
            
            val rs = metaData!!.getColumns(
                catalog?.takeIf { it.isNotBlank() },
                schema?.takeIf { it.isNotBlank() },
                tableName,
                "%"
            )
            
            while (rs.next()) {
                val columnName = rs.getString("COLUMN_NAME")
                val typeName = rs.getString("TYPE_NAME")
                val nullable = rs.getInt("NULLABLE") == java.sql.DatabaseMetaData.columnNullable
                val isAutoIncrement = rs.getString("IS_AUTOINCREMENT")?.equals("YES", ignoreCase = true) ?: false
                
                columns.add(ColumnInfo(
                    name = columnName,
                    type = typeName,
                    nullable = nullable,
                    autoIncrement = isAutoIncrement
                ))
            }
            rs.close()
        } catch (e: Exception) {
            throw RuntimeException(TableGearBundle.message("dbHelper.loadColumnsFailed", tableName, e.message ?: "unknown"), e)
        }
        
        return columns
    }
}

