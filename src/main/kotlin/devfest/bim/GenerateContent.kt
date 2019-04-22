package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

object GenerateContent : CliktCommand(name = "gen", help = "Generate content") {

    private val eventId: String by option("-e", "--event", help = "the event Id").required()
    private val apiKey: String by option("-k", "--api-key", help = "the api key").required()
    private val selectedTalks: File by argument(help = "a file with talk id per line that have been selected")
        .file(exists = true, readable = true, fileOkay = true)
        .default(File("selected.txt"))

    override fun run() {
        with(Events(eventId, apiKey)) {

            val parentFile = selectedTalks.absoluteFile.parentFile

            // FIXME parse line to retrieve: room, slot(hour), slides, videoId
            val selected =
                selectedTalks.readLines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { id -> id to event.talks.find { it.id == id } }


            selected.filter { it.second == null }
                .forEachIndexed { idx, (id, _) ->
                    println("⚠️ $idx / talk not found: $id")
                }

            // Talks
            val talks = selected.mapNotNull { it.second }
            fun Talk.toContent(): String =
                """---
                  |id: $id
                  |key: ${key()}
                  |title: $title
                  |level: $level
                  |formats: ${format()?.name?:""}
                  |tags:
                  |  - ${category()?.name?:""}
                  |speakers:
                  |${speakers().joinToString(separator = "\n") { "  - ${it.key()}"}}
                  |presentation:
                  |videoId:
                  |---
                  |$abstract
                  |""".trimMargin()

            // Dir
            val talksDir = parentFile.resolve("content").resolve("sessions")
            if (talksDir.mkdirs()) {
                println("Create folder $talksDir")
            }
            talks.forEach { talk: Talk ->
                val file = talksDir.resolve("${talk.key()}.md")
                file.writeText(talk.toContent())
            }


            // Speakers
            val speakers = talks
                .flatMap { it.speakers() }
                .distinct()

            fun Speaker.toContent(): String =
                """---
                  |id: $uid
                  |key: ${key()}
                  |name: $displayName
                  |company: $company
                  |city: $city
                  |photoURL: $photoURL
                  |socials:
                  |${socials().joinToString("\n") { """  - icon: ${it.type}
                                                      |    link: ${it.link}
                                                      |    name: ${it.name}""".trimMargin()}}
                  |---
                  |$bio
                  |""".trimMargin()

            val speakersDir = parentFile.resolve("content").resolve("speakers")
            if (speakersDir.mkdirs()) {
                println("Create folder $speakersDir")
            }
            speakers.forEach { speaker: Speaker ->
                val file = speakersDir.resolve("${speaker.key()}.md")
                file.writeText(speaker.toContent())
            }


        }
    }

}