import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
}

repositories {
    mavenCentral()
}

val junitVersion="5.9.3"
val http4kVersion="4.43.0.0"

dependencies {
    implementation(platform("org.http4k:http4k-bom:4.43.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-incubator")

    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.http4k:http4k-testing-strikt")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("io.strikt:strikt-core:0.34.0")
    testImplementation("org.testcontainers:testcontainers:1.18.1")
    testImplementation("org.testcontainers:mongodb:1.18.1")

    implementation("org.litote.kmongo:kmongo:4.9.0")

    implementation(platform("dev.forkhandles:forkhandles-bom:2.5.0.0"))
    implementation("dev.forkhandles:result4k")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
