plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "io.github.strive-run"
version = "0.0.1-SNAPSHOT"

repositories {
    maven(url = "https://maven.aliyun.com/nexus/content/groups/public")
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-starter-parent:3.0.5"))

    implementation("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.slf4j:slf4j-api")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "configcenter-spring-boot-starter"
                description = "A concise description of my library"
                url = "https://github.com/strive-run/configcenter-spring-boot-starter"
                packaging = "jar"
                properties = mapOf(
                )
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "gao.wei.strive"
                        name = "gao.wei.strive"
                        email = "gao.wei.strive@qq.com"
                    }
                }
                scm {
                    connection = "scm:git:git@github.com:strive-run/configcenter-spring-boot-starter.git"
                    developerConnection = "scm:git:git@github.com:strive-run/configcenter-spring-boot-starter.git"
                    url = "https://github.com/strive-run/configcenter-spring-boot-starter"
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.properties["myNexusUsername"].toString())
            password.set(project.properties["myNexusPassword"].toString())
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
