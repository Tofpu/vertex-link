plugins {
    id("java")
    id("info.solidsoft.pitest") version("1.19.0")
}

subprojects {
    apply { plugin("java") }
    apply { plugin("java-library") }
    apply { plugin("info.solidsoft.pitest") }

    group = "io.github.tofpu"
    version = "0.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:6.0.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        testImplementation("org.assertj:assertj-core:3.27.7")
        testImplementation("org.mockito:mockito-core:5.23.0")
    }

    pitest {
        setReportDir(file("build/pitest"))
        junit5PluginVersion = "1.2.3"
    }

    tasks.test {
        useJUnitPlatform()
    }
}