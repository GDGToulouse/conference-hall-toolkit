package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

object GenerateSpeakers : CliktCommand(name = "gen", help = "Generate speakers and sessions content") {

    data class Selection(val event: Event, val talkId: String, val feature: Boolean) {

        val talk: Talk? =
            event.talks.find { it.id == talkId }

        companion object {
            // FIXME parse line to retrieve: room, slot(hour), slides, videoId
            fun fromLine(event: Event, line: String): Selection {
                val cells = line.split(' ')
                val talkId = cells[0]
                val feature = "true" == cells[1]
                return Selection(event, talkId, feature)
            }
        }
    }

    private val eventId: String by option("-e", "--event", help = "the event Id").required()
    private val apiKey: String by option("-k", "--api-key", help = "the api key").required()
    private val selectedTalks: File by argument(help = "a file with talk id per line that have been selected")
        .file(exists = true, readable = true, fileOkay = true)
        .default(File("selected.txt"))

    override fun run() {
        with(Events(eventId, apiKey)) {
            val parentFile = selectedTalks.absoluteFile.parentFile


            val selected =
                selectedTalks.readLines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { Selection.fromLine(event, it) }


            selected.filter { it.talk == null }
                .forEachIndexed { idx, (id, _) ->
                    Logger.error { "⚠️ $idx / talk not found: $id" }
                }

            // Talks
            fun Talk.toContent(draft: Boolean = false): String =
                """---
                  |id: $id
                  |key: ${key()}
                  |title: "$title"
                  |level: $level
                  |formats: ${format()?.name ?: ""}
                  |tags:
                  |  - ${category()?.name ?: ""}
                  |speakers:
                  |${speakers().joinToString(separator = "\n") { "  - ${it.key()}" }}
                  |presentation:
                  |videoId:
                  |draft: $draft
                  |---
                  |$abstract
                  |""".trimMargin()

            // Dir
            val talksDir = parentFile.resolve("content").resolve("sessions")
            if (talksDir.mkdirs()) {
                Logger.info { "Create folder $talksDir" }
            }
            selected
                .mapNotNull { it.talk }
                .forEach { talk: Talk ->
                    val file = talksDir.resolve("${talk.key()}.md")
                    file.writeText(talk.toContent(true))
                    Logger.info { "Created file $file for ${talk.title}" }
                }

            // Speakers
            val speakers = selected
                .flatMap { selection ->
                    selection.talk
                        ?.speakers()
                        ?.map { selection.feature to it }
                        ?: emptyList()
                }
                .distinctBy { it.second }

            fun Speaker.toContent(feature: Boolean): String =
                """---
                  |id: $uid
                  |key: ${key()}
                  |feature: $feature
                  |name: "$displayName"
                  |company: "$company"
                  |city: "$city"
                  |photoURL: "$photoURL"
                  |socials:
                  |${socials().joinToString("\n") {
                    """  - icon: ${it.type}
                      |    link: ${it.link}
                      |    name: ${it.name}""".trimMargin()
                }}
                  |---
                  |$bio
                  |""".trimMargin()

            val speakersDir = parentFile.resolve("content").resolve("speakers")
            if (speakersDir.mkdirs()) {
                Logger.info { "Create folder $speakersDir" }
            }
            speakers.forEach { (feature, speaker) ->
                val file = speakersDir.resolve("${speaker.key()}.md")
                file.writeText(speaker.toContent(feature))
                Logger.info { "Created file $file for ${speaker.displayName}" }
            }


        }
    }

}