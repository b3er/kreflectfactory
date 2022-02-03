import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

dependencies {
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

group = "com.github.b3er.kreflectfactory"
version = "0.9.3"
plugins.withId("maven-publish") {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                pom {
                    url.set("https://github.com/b3er/kreflectfactory")
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                }
            }
        }
    }
}
