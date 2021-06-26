dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    testCompile("junit", "junit", "4.12")

    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")

    implementation("io.ktor:ktor-server-core:1.6.0")
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("io.ktor:ktor-auth:1.6.0")
    implementation("io.ktor:ktor-auth-jwt:1.6.0")

}
