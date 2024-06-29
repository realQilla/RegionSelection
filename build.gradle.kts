plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.qilla"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

configurations.create("shade")

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    implementation("net.kyori:adventure-api:4.17.0")
}

tasks {
    build {
        dependsOn("shadowJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        configurations = listOf(project.configurations.getByName("shade"))
        destinationDirectory.set(file("C:\\Users\\Richard\\Development\\Servers\\1.21.0\\plugins"))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}