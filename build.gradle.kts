plugins {
    kotlin("jvm") version "1.9.24"
    application
    java
}

group = "com.feather"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty:3.9.9.Final")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.yaml:snakeyaml:2.4")

    // Optional: Logging
    implementation("org.slf4j:slf4j-simple:2.0.12")

    // Plugin scanning
    implementation("org.reflections:reflections:0.9.12")
}

application {
    mainClass.set("com.feather.Launcher") // Replace with your server's actual entry point
}

kotlin {
    jvmToolchain(23) // or whatever version you are targeting
}
