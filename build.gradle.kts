plugins {
    java
    kotlin("jvm") version "1.4.31"
}

allprojects {

    group = "eu.thesimplecloud"
    version = "3.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
    }

    repositories {
        maven {
            setUrl("https://repo.thesimplecloud.eu/artifactory/list/gradle-release-local/")
        }
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("com.google.inject:guice:5.0.1")
        testImplementation("junit", "junit", "4.12")
    }
}
