plugins {
    application
    id("org.graalvm.buildtools.native") version "0.10.6"
    // id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.mikael.px2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.7")
    annotationProcessor("info.picocli:picocli-codegen:4.7.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.12.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

/*
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("px2csv")
    archiveClassifier.set("")
    archiveVersion.set("")
    minimize()
}
*/

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Aproject=${project.group}/${project.name}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
        vendor.set(JvmVendorSpec.matching("GraalVM Community"))
    }
}

application {
    mainClass.set("io.mikael.px2.Main")
}

graalvmNative {
    binaries.all {
        buildArgs.add("-H:+AddAllCharsets")
        // buildArgs.add("-R:MaxHeapSize=32m")
        buildArgs.add("-march=native")
    }
}

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}
