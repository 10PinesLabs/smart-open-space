import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  val kotlinVersion = "1.9.22"
  id("org.springframework.boot") version "2.7.18"
  id("io.spring.dependency-management") version "1.1.5"
  id("org.flywaydb.flyway") version "9.22.3"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.spring") version kotlinVersion
  kotlin("plugin.jpa") version kotlinVersion
  kotlin("plugin.allopen") version kotlinVersion
  jacoco
}

group = "com.sos"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
  mavenCentral()
}

dependencyManagement {
  dependencies {
    dependency("com.h2database:h2:2.1.210")
  }
}

dependencies {
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation("org.flywaydb:flyway-core")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  implementation("com.google.guava:guava:32.0.0-android")
  implementation("net.sargue:mailgun:1.10.0")
  implementation("org.glassfish.jersey.inject:jersey-hk2")
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
    jvmTarget = "21"
  }
}


flyway {
  driver = "org.postgresql.Driver"
  url = System.getenv("JDBC_DATABASE_URL")
  user = System.getenv("JDBC_DATABASE_USERNAME")
  password = System.getenv("JDBC_DATABASE_PASSWORD")
  baselineOnMigrate = true
  locations = arrayOf("filesystem:src/main/resources/db/migration")
}

tasks.jacocoTestReport {
  reports {
    xml.required.value(true)
    html.required.value(false)
  }
}

val testCoverage by tasks.registering {
  group = "verification"
  description = "Runs the unit tests with coverage."
  dependsOn(":test", ":jacocoTestReport")
  tasks.findByName("jacocoTestReport")?.mustRunAfter(tasks.findByName("test"))
}
