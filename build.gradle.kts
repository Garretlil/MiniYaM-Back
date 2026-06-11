plugins {
	kotlin("jvm") version "1.9.10"
	kotlin("plugin.spring") version "1.9.10"
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Web
	implementation("org.springframework.boot:spring-boot-starter-web")
	// Spring JPA + база (H2 для теста)
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")
	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	// BCrypt
	implementation("org.springframework.boot:spring-boot-starter-security")
	// Mp3
	implementation("com.mpatric:mp3agic:0.9.1")
	// Тесты
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "17"
	kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

tasks.withType<Test> {
	useJUnitPlatform()
}