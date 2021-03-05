plugins {
    java
}

buildscript {
    repositories {
        jcenter()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    group = "me.hyfe.helper"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    dependencies {

    }
}