plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://libraries.minecraft.net/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    compileOnly("com.google.guava:guava:30.1-jre")
    compileOnly("com.mojang:authlib:1.5.25")
    implementation("org.yaml:snakeyaml:1.28");
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