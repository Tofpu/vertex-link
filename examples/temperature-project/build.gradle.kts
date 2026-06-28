plugins {
    id("java")
}

subprojects {
    apply { plugin("java") }
    apply { plugin("java-library") }

    val commonDep = ":examples:temperature-project:temperature-project-common"
    if (this != project(commonDep)) {
        dependencies {
            implementation(project(commonDep))
        }
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.18")
        implementation("ch.qos.logback:logback-classic:1.5.36")
    }
}