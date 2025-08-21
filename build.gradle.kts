plugins {
    kotlin("jvm") version "2.1.21"
}

group = "com.sreeram.tlv"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Add this to fix the JVM target compatibility
kotlin {
    jvmToolchain(21)
}


// Configure JAR creation
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Surfboard Payments"
        )
    }

    archiveClassifier.set("")
    archiveBaseName.set("tlv-library")

    // Include source files in JAR for debugging (optional)
    from(sourceSets.main.get().output)
}

// Create a sources JAR
tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
    archiveBaseName.set("tlv-library")
}

// Create a javadoc JAR (empty for Kotlin, but some repositories require it)
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    archiveBaseName.set("tlv-library")
}

// Make build depend on creating all JARs
tasks.build {
    dependsOn(tasks.jar, tasks["sourcesJar"], tasks["javadocJar"])
}