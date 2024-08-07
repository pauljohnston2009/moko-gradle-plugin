/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    `kotlin-dsl`
    id("org.gradle.maven-publish")
    id("signing")
    id("java-gradle-plugin")
}

group = "dev.icerock.moko"
version = libs.versions.mokoGradlePluginVersion.get()

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    api(libs.androidGradlePlugin)
    api(libs.kotlinGradlePlugin)
    api(libs.mobileMultiplatformGradlePlugin)
    api(libs.detektGradlePlugin)
    api(libs.nexusPublishGradlePlugin)
}

gradlePlugin {
    plugins {
        create("android-app") {
            id = "dev.icerock.moko.gradle.android.application"
            implementationClass = "dev.icerock.moko.gradle.AndroidAppPlugin"
        }
        create("android-library") {
            id = "dev.icerock.moko.gradle.android.library"
            implementationClass = "dev.icerock.moko.gradle.AndroidLibraryPlugin"
        }
        create("android-base") {
            id = "dev.icerock.moko.gradle.android.base"
            implementationClass = "dev.icerock.moko.gradle.AndroidBasePlugin"
        }
        create("android-publication") {
            id = "dev.icerock.moko.gradle.android.publication"
            implementationClass = "dev.icerock.moko.gradle.AndroidPublicationPlugin"
        }

        create("detekt") {
            id = "dev.icerock.moko.gradle.detekt"
            implementationClass = "dev.icerock.moko.gradle.DetektPlugin"
        }

        create("multiplatform-mobile") {
            id = "dev.icerock.moko.gradle.multiplatform.mobile"
            implementationClass = "dev.icerock.moko.gradle.KmmLibraryPlugin"
        }
        create("multiplatform-all") {
            id = "dev.icerock.moko.gradle.multiplatform.all"
            implementationClass = "dev.icerock.moko.gradle.KmpLibraryPlugin"
        }

        create("publication") {
            id = "dev.icerock.moko.gradle.publication"
            implementationClass = "dev.icerock.moko.gradle.PublicationPlugin"
        }
        create("publication-nexus") {
            id = "dev.icerock.moko.gradle.publication.nexus"
            implementationClass = "dev.icerock.moko.gradle.NexusPublicationPlugin"
        }

        create("publication-hosts") {
            id = "dev.icerock.moko.gradle.publication.hosts"
            implementationClass = "dev.icerock.moko.gradle.HostsPublicationPlugin"
        }

        create("stubjavadoc") {
            id = "dev.icerock.moko.gradle.stub.javadoc"
            implementationClass = "dev.icerock.moko.gradle.StubJavaDocPlugin"
        }

        create("tests") {
            id = "dev.icerock.moko.gradle.tests"
            implementationClass = "dev.icerock.moko.gradle.TestsReportPlugin"
        }

        create("jvm") {
            id = "dev.icerock.moko.gradle.jvm"
            implementationClass = "dev.icerock.moko.gradle.JvmPlugin"
        }
    }
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            pom {
                name.set("MOKO gradle plugin")
                description.set("This is a Gradle plugin with common build logic for all MOKO libraries.")
                url.set("https://github.com/icerockdev/moko-gradle-plugin")
                licenses {
                    license {
                        name.set("Apache-2.0")
                        distribution.set("repo")
                        url.set("https://github.com/icerockdev/moko-gradle-plugin/blob/master/LICENSE.md")
                    }
                }

                developers {
                    developer {
                        id.set("Alex009")
                        name.set("Aleksey Mikhailov")
                        email.set("aleksey.mikhailov@icerockdev.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/icerockdev/moko-gradle-plugin.git")
                    developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-gradle-plugin.git")
                    url.set("https://github.com/icerockdev/moko-gradle-plugin")
                }
            }
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
        String(Base64.getDecoder().decode(base64Key))
    }

    if (signingKeyId != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
