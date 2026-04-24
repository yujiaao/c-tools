plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.22" // 与 IntelliJ Platform 2022.1 兼容的 Kotlin 版本
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.bixuebihui"
version = "1.0.3"

repositories {
    mavenCentral()
    // 可选：添加国内镜像加速（如果需要）
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    // 添加本地 Maven 仓库以使用 c-tablegen 模块
    mavenLocal()
}

intellij {
    version.set("2022.1") // 使用 2022.1 进行编译和测试（支持 JDK 11）
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf("com.intellij.java"))
    
    // 可选：如果本地已安装 IDEA，可以指定路径避免下载
    // localPath.set("C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2022.1")
}

// 使用插件声明的依赖优先，避免被 IDE 内置版本覆盖（Gradle 7+ implementation 需此项）
configurations.all {
    resolutionStrategy.sortArtifacts(ResolutionStrategy.SortOrder.DEPENDENCY_FIRST)
}

// 排除 Kotlin stdlib 的运行时依赖，使用 IntelliJ Platform 提供的版本
// 注意：测试运行时需要 Kotlin stdlib，所以只从主运行时排除
configurations {
    runtimeClasspath {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    }
    // testRuntimeClasspath 需要 Kotlin stdlib 来运行测试，所以不排除
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    register("checkI18nHardcodedLiterals") {
        group = "verification"
        description = "Scan Kotlin UI code for hardcoded literals."

        doLast {
            val sourceDir = file("src/main/kotlin")
            val whitelistFile = file("scripts/i18n-literal-whitelist.txt")
            val patterns = listOf(
                "JBLabel literal" to Regex("""JBLabel\(\s*"[^"]+""""),
                "JButton literal" to Regex("""JButton\(\s*"[^"]+""""),
                "Messages.show* literal" to Regex("""Messages\.show\w+\([^)]*"[^"]+"""")
            )
            val whitelist = if (whitelistFile.exists()) {
                whitelistFile.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
                    .map { it.replace('\\', '/') }
                    .toSet()
            } else {
                emptySet()
            }

            val issues = mutableListOf<String>()
            sourceDir.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    val relativePath = file.relativeTo(projectDir).path.replace('\\', '/')
                    file.readLines().forEachIndexed { index, rawLine ->
                        val location = "$relativePath:${index + 1}"
                        if (location in whitelist) {
                            return@forEachIndexed
                        }
                        val line = rawLine.trim()
                        if (line.startsWith("//") || rawLine.contains("TableGearBundle.message(") || line == "JBLabel(\"\")" || line == "JButton(\"\")") {
                            return@forEachIndexed
                        }
                        patterns.forEach { (name, regex) ->
                            if (regex.containsMatchIn(rawLine)) {
                                issues.add("$location: $name: $line")
                            }
                        }
                    }
                }

            if (issues.isNotEmpty()) {
                throw GradleException(
                    buildString {
                        appendLine("Found hardcoded UI literals. Use TableGearBundle.message(...):")
                        issues.forEach { appendLine(it) }
                    }
                )
            }
            logger.lifecycle("checkI18nHardcodedLiterals passed.")
        }
    }

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221") // 从 2022.1 版本开始支持（对应 2022.1，兼容更早版本）
        untilBuild.set("255.*") // 支持到 255.* 版本
    }

    withType<Test> {
        useJUnit()
    }

    named("check") {
        dependsOn("checkI18nHardcodedLiterals")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    // MySQL JDBC 驱动：插件内“测试连接”/连接数据库需要，否则 ClassNotFoundException: com.mysql.cj.jdbc.Driver
    implementation("com.mysql:mysql-connector-j:8.2.0")

    // 依赖 tablegen 模块以使用数据库连接功能
    // 注意：需要先构建 c-tablegen 模块 (mvn install) 才能使用
    // 如果 c-tablegen 已发布到本地 Maven 仓库，使用以下方式：
    implementation("com.github.yujiaao:tablegen:1.12.3.0") {
        // 排除所有日志相关的依赖，避免与 IDEA Platform 的日志系统冲突
        // IDEA Platform 使用自己的日志系统，不需要 log4j
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "org.slf4j", module = "log4j-over-slf4j")
        exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-jul")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-log4j2")
        // 排除传递依赖中的日志相关模块
        exclude(group = "org.slf4j")
        exclude(group = "org.apache.logging.log4j")

        // 第一轮瘦身：排除与 IDEA 插件主流程无关的大体积传递依赖
        // 文档/报表相关
        exclude(group = "org.apache.poi")
        exclude(group = "com.itextpdf")
        exclude(group = "org.ccil.cowan.tagsoup")
        exclude(group = "net.sourceforge.jexcelapi")
        exclude(group = "bsf", module = "bsf")
        exclude(group = "org.mozilla", module = "rhino")
        exclude(group = "org.joda", module = "joda-time")

        // 缓存/消息/Redis 相关
        exclude(group = "org.jgroups", module = "jgroups")
        exclude(group = "redis.clients", module = "jedis")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-data-redis")
        exclude(group = "org.springframework.data", module = "spring-data-redis")
        exclude(group = "io.lettuce", module = "lettuce-core")
        exclude(group = "io.netty")
        exclude(group = "io.projectreactor", module = "reactor-core")
        exclude(group = "org.reactivestreams", module = "reactive-streams")

        // 其它可按需下载或非核心依赖
        exclude(group = "com.microsoft.sqlserver", module = "mssql-jdbc")
        exclude(group = "org.bouncycastle")

        // 第二轮瘦身：按点名目标继续精细排除
        exclude(group = "org.springframework", module = "spring-webmvc")
        exclude(group = "com.github.ben-manes.caffeine", module = "caffeine")
        exclude(group = "joda-time", module = "joda-time")

        // 第三轮瘦身（保留 httpclient）：继续尝试移除非核心传递依赖
        exclude(group = "org.springframework", module = "spring-web")
        exclude(group = "org.hibernate.validator", module = "hibernate-validator")
        exclude(group = "org.apache.commons", module = "commons-configuration2")
        exclude(group = "com.foundationdb", module = "fdb-sql-parser")

        // 第四轮（低风险优先）
        exclude(group = "org.springframework", module = "spring-context-support")
        exclude(group = "javax.mail", module = "javax.mail-api")
    }
    
    testImplementation(kotlin("test"))
    // kotlin.test uses JUnit 5 assertions which require opentest4j
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}

