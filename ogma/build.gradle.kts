plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.github.javafaker:javafaker:1.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")

    testImplementation("io.kotest:kotest-assertions:4.0.7")
    testImplementation("io.kotest:kotest-assertions-core:4.0.7")

    testImplementation("com.squareup.okhttp3:mockwebserver3:5.0.0-alpha.2")
    testRuntimeOnly("com.squareup.okhttp3:mockwebserver3-junit5:5.0.0-alpha.2")
}