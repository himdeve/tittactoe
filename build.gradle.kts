// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.1.2" apply false
    id("com.android.library") version "7.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("com.google.protobuf") version "0.8.18" apply false
}

ext["grpcKotlinVersion"] = "1.3.0" // CURRENT_GRPC_KOTLIN_VERSION
ext["grpcVersion"] = "1.46.0"
ext["protobufVersion"] = "3.20.1"
ext["coroutinesVersion"] = "1.6.2"