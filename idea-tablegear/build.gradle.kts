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
}

intellij {
    version.set("2022.1") // 使用 2022.1 进行编译和测试（支持 JDK 11）
    type.set("IC") // IntelliJ IDEA Community Edition
    plugins.set(listOf("com.intellij.java"))
    
    // 可选：如果本地已安装 IDEA，可以指定路径避免下载
    // localPath.set("C:/Program Files/JetBrains/IntelliJ IDEA Community Edition 2022.1")
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
    testImplementation(kotlin("test"))
    // kotlin.test uses JUnit 5 assertions which require opentest4j
    testImplementation("org.opentest4j:opentest4j:1.3.0")
}

