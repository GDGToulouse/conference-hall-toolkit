plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.30")
    application
}

repositories {
    jcenter()
}

fun moshi(module: String, version: String = "1.5.0"): Any =
    "com.squareup.moshi:moshi-$module:$version"

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(moshi("adapters"))
    implementation(moshi("kotlin"))

    implementation("com.github.ajalt:clikt:1.7.0")

}

application {
    // Define the main class for the application.
    mainClassName = "devfest.bim.MainKt"
}

// Create fat jar
tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = application.mainClassName

    from(
        configurations.runtime.get().files.map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
}
