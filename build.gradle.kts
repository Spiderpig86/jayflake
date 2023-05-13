plugins {
    id("java-library")
    id("com.diffplug.spotless") version "6.18.0"
    id("maven-publish")
    id("signing")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:31.1-jre")


    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation("org.mockito:mockito-junit-jupiter:3.6.28")
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
}


signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}