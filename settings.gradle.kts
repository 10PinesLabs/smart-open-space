rootProject.name = "smart-open-space"

pluginManagement {
    plugins {
        id ("java")
        id ( "war")
        id ("org.springframework.boot") version "2.4.5"
        id ("io.spring.dependency-management") version "1.0.11.RELEASE"
        id ("org.siouan.frontend-jdk11") version "6.0.0"
    }
    repositories {
        gradlePluginPortal()
    }
}

include("back", "front")