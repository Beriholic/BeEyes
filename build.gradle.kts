plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "cv.beriholic"
version = "0.0.1-SNAPSHOT"
description = "BeEyes"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val jimmerVersion by extra {
    "0.9.113"
}
val mapstructVersion by extra {
    "1.6.3"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("org.apache.commons:commons-lang3:3.19.0")
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter:${jimmerVersion}")
    annotationProcessor("org.babyfish.jimmer:jimmer-apt:${jimmerVersion}")
    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.40.0")
    implementation("cn.dev33:sa-token-redis-jackson:1.40.0")
    implementation("cn.hutool:hutool-all:5.8.41")
    implementation("org.springframework.boot:spring-boot-starter-aop")


}

tasks.withType<Test> {
    useJUnitPlatform()
}
