plugins {
    java
    id("org.graalvm.buildtools.native") version "0.9.16"
}

group = "io.mikael.px2"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.mikael.px2.Main"
    }
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("io.mikael.px2.Main")
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(17))
                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
            })
        }
    }
}