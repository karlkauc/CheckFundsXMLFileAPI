plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.2"
    id("com.github.ben-manes.versions") version "0.43.0"
}

version = "0.1"
group = "org.fundsxml"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.19.0")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")
    implementation("io.micronaut:micronaut-validation")

    implementation("net.sf.saxon:Saxon-HE:11.4") {
        exclude(group = "xml-apis", module = "xml-apis")
    }
}


application {
    mainClass.set("org.fundsxml.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
    targetCompatibility = JavaVersion.toVersion("11")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.fundsxml.*")
    }
}



