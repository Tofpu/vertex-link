dependencies {
    implementation(project(":common"))
    api("com.h2database:h2-mvstore:2.4.240") // to log sensor data to disk

    implementation(platform("tools.jackson:jackson-bom:3.2.0"))
    implementation("tools.jackson.core:jackson-core") // for serialization and deserialization
    implementation("tools.jackson.core:jackson-databind")
}