plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.google.guava:guava:30.1-jre")
}

tasks.processResources {
    expand("version" to project.version)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "me.hyfe.helper"
            artifactId = "core"
            version = "1.0.0"

            from(components["java"])
        }
    }
}