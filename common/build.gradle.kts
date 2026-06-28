plugins {
    id("com.google.protobuf") version "0.10.0"
}

val protoVersion = "4.35.1"
val grpcVersion = "1.82.1"

protobuf {
    protoc {
        // Download from repositories
        artifact = "com.google.protobuf:protoc:$protoVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc") {}
            }
        }
    }
}

dependencies {
    // Match this version family with your protoc version (4.x -> 4.x)
    api("com.google.protobuf:protobuf-java:$protoVersion")

    runtimeOnly("io.grpc:grpc-netty-shaded:$grpcVersion")
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")

    api("org.slf4j:slf4j-api:2.0.18")
    api("ch.qos.logback:logback-classic:1.5.36")
}