dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":api"))
    implementation(project(":api-impl"))
    implementation(project(":ignite"))

    api("com.fasterxml.jackson.core:jackson-core:2.12.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.2")
    api("org.json:json:20210307")

    implementation("io.ktor:ktor-client-cio:1.6.2")
    implementation("io.ktor:ktor-client-core:1.6.2")

    implementation("io.ktor:ktor-server-core:1.6.2")
    implementation("io.ktor:ktor-server-netty:1.6.2")
    implementation("io.ktor:ktor-auth:1.6.2")
    implementation("io.ktor:ktor-auth-jwt:1.6.2")

}
