plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "xyz.beriholic"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.39.0")
    implementation("cn.hutool:hutool-all:5.8.35")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.9")
    annotationProcessor("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.53")
    implementation("com.auth0:java-jwt:4.3.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("com.influxdb:influxdb-client-java:7.2.0")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.influxdb:influxdb-client-java:6.6.0")
    implementation("com.jcraft:jsch:0.1.55")
}

tasks.withType<Test> {
    useJUnitPlatform()
}