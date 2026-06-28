plugins {
    id("com.google.protobuf") version "0.10.0"
}

dependencies {
    implementation(project(":common"))
    implementation("io.lettuce:lettuce-core:7.6.0.RELEASE")
    implementation("org.apache.commons:commons-pool2:2.13.1")
}