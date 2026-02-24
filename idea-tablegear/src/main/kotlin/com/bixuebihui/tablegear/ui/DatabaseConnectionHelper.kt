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
        try {
            // 检查 Java 版本
            val javaVersion = System.getProperty("java.version")
            val majorVersion = javaVersion.split(".").getOrNull(0)?.toIntOrNull() ?: 
                              javaVersion.split(".").getOrNull(1)?.toIntOrNull() ?: 0
            
            if (majorVersion < 11) {
                throw RuntimeException(
                    "Java 版本不兼容！当前版本: Java $javaVersion，需要 Java 11 或更高版本。\n" +
                    "请检查 IDEA 的 JDK 配置。"
                )
            }
            
            val props = createDatabaseProperties()
            
            // 验证必要的配置
            val className = props.getProperty("className")
            val dburl = props.getProperty("dburl")
            
            if (className.isNullOrBlank()) {
                throw IllegalArgumentException("JDBC驱动类名(className)不能为空")
            }
            if (dburl.isNullOrBlank()) {
                throw IllegalArgumentException("数据库URL(dburl)不能为空")
            }
            
            // 优先用插件 ClassLoader；若未打包则尝试从“已下载驱动”目录加载（见 JdbcDriverDownloadService）
            val pluginLoader = this.javaClass.classLoader
            val driverLoader = try {
                JdbcDriverDownloadService.ensureDriverLoaded(className, pluginLoader)
            } catch (e: ClassNotFoundException) {
                val downloadHint = if (JdbcDriverDownloadService.isDownloadable(className)) {
                    "\n\n该驱动支持在插件内一键下载，请在连接失败提示中选择「下载驱动」。"
                } else ""
                throw RuntimeException(
                    "无法加载JDBC驱动: $className\n\n" +
                    "请确保:\n" +
                    "1. JDBC驱动jar包已添加到项目依赖中\n" +
                    "2. 驱动类名拼写正确\n" +
                    "3. 驱动版本与数据库版本兼容" + downloadHint,
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
                    throw RuntimeException("无法创建数据库连接: 连接对象为null")
                }

                if (connection!!.isClosed) {
                    throw RuntimeException("数据库连接已关闭")
                }

                // 获取元数据（这会验证连接是否真正可用）
                metaData = connection!!.metaData

                // 简单验证：尝试获取数据库产品名称
                try {
                    metaData?.databaseProductName
                } catch (e: Exception) {
                    throw RuntimeException("数据库连接无效: ${e.message}", e)
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
                        else -> "版本 $version"
                    }
                } ?: "未知"
            } else {
                "未知"
            }
            throw RuntimeException(
                "Java 版本不兼容！\n\n" +
                "错误: $errorMessage\n\n" +
                "c-tablegen 模块需要 $requiredVersion, 但当前运行环境是 Java ${System.getProperty("java.version")}.\n\n" +
                "解决方案:\n" +
                "1. 确保 c-tablegen 模块使用 Java 11 编译: cd c-tablegen && mvn clean install -Dmaven.compiler.release=11\n" +
                "2. 或者升级 IDEA 到支持 Java 17 的版本\n" +
                "3. 检查 IDEA 的 JDK 配置",
                e
            )
        } catch (e: Exception) {
            // 重新抛出异常以便上层可以获取详细错误信息
            val errorMsg = buildString {
                append("数据库连接失败\n\n")
                append("错误类型: ${e.javaClass.simpleName}\n")
                append("错误信息: ${e.message}\n\n")
                
                if (e.cause != null) {
                    append("原因: ${e.cause?.message}\n\n")
                }
                
                append("请检查:\n")
                append("1. 数据库连接信息是否正确（驱动类名、URL、用户名、密码）\n")
                append("2. 数据库服务是否运行\n")
                append("3. 网络连接是否正常\n")
                append("4. JDBC 驱动是否正确加载\n")
                append("5. 防火墙设置是否允许连接\n\n")
                
                // 如果是 ClassNotFoundException，提供额外提示
                if (e is ClassNotFoundException) {
                    append("提示: 找不到 JDBC 驱动类，请确保:\n")
                    append("- JDBC 驱动 jar 包已添加到项目依赖中\n")
                    append("- 驱动类名拼写正确\n\n")
                }
                
                append("详细错误:\n")
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
            val meta = metaData ?: throw IllegalStateException("数据库未连接")
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
            throw IllegalStateException("数据库未连接，请先连接数据库")
        }
        
        val columns = mutableListOf<ColumnInfo>()
        try {
            val catalog = model.getProperty("catalog")
            val schema = model.getProperty("schema")
            
            val rs = metaData!!.getColumns(
                catalog?.takeIf { it.isNotBlank() } ?: null,
                schema?.takeIf { it.isNotBlank() } ?: null,
                tableName,
                "%"
            )
            
            while (rs.next()) {
                val columnName = rs.getString("COLUMN_NAME")
                val dataType = rs.getInt("DATA_TYPE")
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
            throw RuntimeException("获取表 $tableName 的字段列表失败: ${e.message}", e)
        }
        
        return columns
    }
}

