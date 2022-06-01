import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.2.2.RELEASE"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
  kotlin("jvm") version "1.3.61"
  kotlin("plugin.spring") version "1.3.61"
  kotlin("plugin.jpa") version "1.3.61"
  kotlin("plugin.allopen") version "1.3.61"
  jacoco
}

group = "com.sos"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.flywaydb:flyway-core")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  // implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  runtimeOnly("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-test"){
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

allOpen {
  annotation("javax.persistence.Entity")
  annotation("javax.persistence.Embeddable")
  annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
    html.isEnabled = false
  }
}

val testCoverage by tasks.registering {
  group = "verification"
  description = "Runs the unit tests with coverage."
  dependsOn(":test", ":jacocoTestReport")
  tasks.findByName("jacocoTestReport")?.mustRunAfter(tasks.findByName("test"))
}

tasks.register<Copy>("processFrontendResources") {
  // Directory containing the artifacts in the frontend project
  var frontendBuildDir = file("${project(":front").buildDir}/../dist")
  // Directory where the frontend artifacts must be copied to be packaged alltogether with the backend by the "war"
  // plugin.
  var frontendResourcesDir = file("${project.buildDir}/resources/main/public")

  group = "Frontend"
  description = "Process frontend resources"
  dependsOn(project(":front").tasks.named("assembleFrontend"))

  from(frontendBuildDir)
  into(frontendResourcesDir)
}

tasks.named("processResources") {
  dependsOn(tasks.named("processFrontendResources"))
}