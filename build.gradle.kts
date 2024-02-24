import java.net.URI

plugins {
    id("java-library")
    id("com.diffplug.spotless") version "6.19.0"
    id("maven-publish")
    id("signing")
}

group = "io.github.spiderpig86"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:32.1.2-jre")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("org.mockito:mockito-core:5.+")
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        target("src/**/*.java") // configure the files to apply the formatting to
        googleJavaFormat() // apply the Google Java formatter
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "jayflake"
            version = "0.1"
            from(components["java"])

            pom {
                name.set("Jayflake")
                description.set("Snowflake ids for Java.")
                url.set("https://github.com/Spiderpig86/jayflake")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Spiderpig86/jayflake/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("spiderpig86")
                        name.set("Stanley Lim")
                    }
                }

                scm {
                    connection.set("scm:git:git:https://github.com/Spiderpig86/jayflake.git")
                    developerConnection.set("scm:git:ssh://github.com:Spiderpig86/jayflake.git")
                    url.set("https://github.com/Spiderpig86/jayflake")
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSHR"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.properties["ossrhUsername"].toString()
                password = project.properties["ossrhPassword"].toString()
            }
        }
    }
}


signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

// For debugging
task("printProperties") {
    println(project.findProperty("signing"))
    println(project.findProperty("ossrhUsername").toString())
}
