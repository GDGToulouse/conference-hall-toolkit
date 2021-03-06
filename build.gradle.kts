plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.41")
    application
}

repositories {
    jcenter()
}

fun moshi(module: String, version: String = "1.8.0"): Any =
    "com.squareup.moshi:moshi-$module:$version"

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(moshi("adapters"))
    implementation(moshi("kotlin"))

    implementation("com.github.ajalt:clikt:2.1.0")
    implementation("com.github.ajalt:mordant:1.2.1")
    implementation("com.opencsv:opencsv:4.6")
}

application {
    // Define the main class for the application.
    mainClassName = "devfest.bim.MainKt"
}

// Create fat jar
tasks {
  register("fatJar", Jar::class.java) {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
      attributes("Main-Class" to application.mainClassName)
    }
    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies: ${it.name}") }
        .map { if (it.isDirectory) it else zipTree(it) })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
  }
}

