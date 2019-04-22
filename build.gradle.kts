plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.30")
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

//    implementation ("com.opencsv:opencsv:4.0")
}

// Create fat jar
tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "devfest.bim.CheckKt"

    from(
        configurations.runtime.get().files.map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
}
