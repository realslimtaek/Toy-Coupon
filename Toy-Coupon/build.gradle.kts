plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.toy"
version = "0.0.1-SNAPSHOT"
description = "쿠폰 발급 토이 프로젝트"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-r2dbc")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.asyncer:r2dbc-mysql")
	implementation("tools.jackson.module:jackson-module-kotlin")

	// healthCheck
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.redisson:redisson-spring-boot-starter:4.2.0")

	//rabbitMQ
	implementation("org.springframework.amqp:spring-rabbit")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-r2dbc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	ignoreFailures = true
	useJUnitPlatform()
}
