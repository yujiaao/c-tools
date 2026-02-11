package com.bixuebihui.tablegear.ui

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.progress.ProgressIndicator
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 根据用户配置的 JDBC 驱动类名，在未打包时提示并可下载驱动到插件可加载位置并加载。
 * 驱动 JAR 保存在 IDE 配置目录下 tablegear/jdbc-drivers，并在 registry 中记录 className -> jar 路径。
 */
object JdbcDriverDownloadService {

    private const val MAVEN_CENTRAL = "https://repo1.maven.org/maven2"
    private const val REGISTRY_FILE = "registry.properties"

    /** 驱动类名 -> (显示名称, groupId, artifactId, version) */
    private val DRIVER_MAVEN = mapOf(
        "com.mysql.cj.jdbc.Driver" to DriverMavenInfo("MySQL Connector/J (8.x)", "com.mysql", "mysql-connector-j", "8.2.0"),
        "com.mysql.jdbc.Driver" to DriverMavenInfo("MySQL Connector/J (5.x)", "mysql", "mysql-connector-java", "8.0.33"),
        "org.postgresql.Driver" to DriverMavenInfo("PostgreSQL JDBC", "org.postgresql", "postgresql", "42.7.1"),
        "com.microsoft.sqlserver.jdbc.SQLServerDriver" to DriverMavenInfo("Microsoft SQL Server JDBC", "com.microsoft.sqlserver", "mssql-jdbc", "12.4.2"),
        "oracle.jdbc.OracleDriver" to DriverMavenInfo("Oracle JDBC (ojdbc8)", "com.oracle.database.jdbc", "ojdbc8", "21.9.0.0"),
        "oracle.jdbc.driver.OracleDriver" to DriverMavenInfo("Oracle JDBC (legacy)", "com.oracle.database.jdbc", "ojdbc8", "21.9.0.0"),
    )

    private data class DriverMavenInfo(
        val displayName: String,
        val groupId: String,
        val artifactId: String,
        val version: String
    )

    private val loaderCache = ConcurrentHashMap<String, ClassLoader>()
    private var registry: Properties? = null
    private val registryLock = Any()

    /** 获取驱动 JAR 存放目录（插件可读、可写） */
    fun getDriversDir(): File {
        return try {
            val optionsPath = PathManager.getOptionsPath()
            File(optionsPath, "tablegear/jdbc-drivers").also { if (!it.exists()) it.mkdirs() }
        } catch (e: Exception) {
            File(System.getProperty("user.home", "."), ".tablegear/jdbc-drivers").also { if (!it.exists()) it.mkdirs() }
        }
    }

    private fun getRegistryFile(): File = File(getDriversDir(), REGISTRY_FILE)

    private fun loadRegistry(): Properties {
        synchronized(registryLock) {
            if (registry != null) return registry!!
            val p = Properties()
            val f = getRegistryFile()
            if (f.exists()) {
                f.inputStream().use { p.load(it) }
            }
            registry = p
            return p
        }
    }

    private fun saveRegistry() {
        synchronized(registryLock) {
            val p = registry ?: return
            getRegistryFile().outputStream().use { p.store(it, "TableGear JDBC driver class -> jar path") }
        }
    }

    /** 是否支持根据该类名下载驱动 */
    fun isDownloadable(driverClassName: String): Boolean = DRIVER_MAVEN.containsKey(driverClassName.trim())

    /** 获取用于提示的下载说明（驱动显示名 + Maven 坐标） */
    fun getDownloadInfo(driverClassName: String): String? {
        val info = DRIVER_MAVEN[driverClassName.trim()] ?: return null
        return "${info.displayName} (${info.groupId}:${info.artifactId}:${info.version})"
    }

    /** 已下载的 JAR 路径（若已注册） */
    fun getDownloadedJarPath(driverClassName: String): String? =
        loadRegistry().getProperty(driverClassName.trim())

    /**
     * 下载驱动 JAR 到插件目录并注册。
     * @return 成功返回 JAR 文件，失败抛出异常或返回 Result 失败
     */
    fun downloadDriver(driverClassName: String, indicator: ProgressIndicator? = null): File {
        val info = DRIVER_MAVEN[driverClassName.trim()]
            ?: throw IllegalArgumentException("不支持的驱动类型，无法自动下载: $driverClassName")

        val path = "$MAVEN_CENTRAL/${info.groupId.replace('.', '/')}/${info.artifactId}/${info.version}/${info.artifactId}-${info.version}.jar"
        val url = URL(path)

        indicator?.text = "正在下载 ${info.displayName}..."

        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 30000
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "TableGear-Plugin/1.0")

        val code = conn.responseCode
        if (code != HttpURLConnection.HTTP_OK) {
            // 尝试无分类器的 jar（部分 artifact 可能不带 version 后缀）
            conn.disconnect()
            throw RuntimeException("下载失败: HTTP $code - $path")
        }

        val dir = getDriversDir()
        val jarFile = File(dir, "${info.artifactId}-${info.version}.jar")

        conn.inputStream.use { input ->
            indicator?.text = "保存到 ${jarFile.name}..."
            FileOutputStream(jarFile).use { output ->
                val buf = ByteArray(8192)
                var total = 0L
                val len = conn.contentLengthLong
                var n: Int
                while (input.read(buf).also { n = it } != -1) {
                    output.write(buf, 0, n)
                    total += n
                    if (len > 0 && indicator != null) {
                        indicator.fraction = total.toDouble() / len
                    }
                }
            }
        }
        conn.disconnect()

        // 注册到 registry
        synchronized(registryLock) {
            val p = loadRegistry()
            p.setProperty(driverClassName.trim(), jarFile.absolutePath)
            saveRegistry()
            loaderCache.remove(driverClassName.trim())
        }
        return jarFile
    }

    /**
     * 获取用于加载该驱动类的 ClassLoader。若插件已打包该驱动则返回 null（使用调用方 ClassLoader 即可）；
     * 若已下载则返回包含该 JAR 的 URLClassLoader；若未下载且支持下载则返回 null（由 UI 提示下载）。
     */
    fun getClassLoaderFor(driverClassName: String, parent: ClassLoader): ClassLoader? {
        val key = driverClassName.trim()
        loaderCache[key]?.let { return it }

        val jarPath = loadRegistry().getProperty(key) ?: return null
        val file = File(jarPath)
        if (!file.exists()) {
            synchronized(registryLock) {
                loadRegistry().remove(key)
                saveRegistry()
                registry = null
            }
            return null
        }

        val loader = java.net.URLClassLoader(arrayOf(file.toURI().toURL()), parent)
        loaderCache[key] = loader
        return loader
    }

    /**
     * 确保驱动类已加载。优先用插件 ClassLoader；若失败且该驱动已下载则用已下载的 JAR 的 ClassLoader 加载。
     * @return 实际用于加载的 ClassLoader，若加载失败则抛出 ClassNotFoundException
     */
    fun ensureDriverLoaded(driverClassName: String, pluginClassLoader: ClassLoader): ClassLoader {
        try {
            Class.forName(driverClassName, true, pluginClassLoader)
            return pluginClassLoader
        } catch (e: ClassNotFoundException) {
            // 尝试已下载的驱动
            val loader = getClassLoaderFor(driverClassName, pluginClassLoader) ?: throw e
            Class.forName(driverClassName, true, loader)
            return loader
        }
    }

    /** 供 UI 打开驱动所在目录（资源管理器） */
    fun getDriversDirPath(): String = getDriversDir().absolutePath
}
