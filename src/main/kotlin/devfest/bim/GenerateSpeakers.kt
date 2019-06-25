package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

object GenerateSpeakers : CliktCommand(name = "gen", help = "Generate speakers and sessions content") {

    data class Selection(
        val event: Event,
        val talkId: String,
        val feature: Boolean,
        val room: String,
        val slot: String
    ) {

        val talk: Talk? =
            event.talks.find { it.id == talkId }

        companion object {
            // FIXME parse line to retrieve: room, slot(hour), slides, videoId
            fun fromLine(event: Event, cells: List<String>): Selection {
                val (talkId, sFeature, room, slot) = cells
                val feature = "true" == sFeature
                return Selection(event, talkId, feature, room, slot)
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
                    .asSequence()
                    .drop(2)
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .map { it.split('\t').toList() }
//                    .onEach { println(it) }
                    .filter { it.size > 3 }
                    .map { Selection.fromLine(event, it) }
                    .toList()

            selected.filter { it.talk == null }
                .forEachIndexed { idx, (id, _) ->
                    Logger.error { "⚠️ $idx / talk not found: $id" }
                }

            // Talks
            fun Talk.toContent(draft: Boolean = false): String =
                """---
                  |id: $id
                  |key: ${key()}
                  |title: ${title.rawYaml()}
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
            val talksDir = parentFile / "content" / "sessions"
            if (talksDir.mkdirs()) {
                Logger.info { "Create folder $talksDir" }
            }
            selected
                .mapNotNull { it.talk }
                .forEach { talk: Talk ->
                    val file = talksDir / "${talk.key()}.md"
                    file.writeText(talk.toContent(true))
                    Logger.info { "Created file $file for ${talk.title}" }
                }

            // Create Yml
            val sessionYml = selected
                .mapNotNull { it.talk }
                .joinToString(prefix = "items:\n", separator = "\n") { talk ->
                    fun Speaker.yml(): String =
                        """- name: $displayName
                      |  photo: ${photoURL.rawYaml()}
                      |  twitter: ${twitter ?: ""}""".trimMargin()

                    """
                      |${talk.speakers().map { it.key() }.joinToString("_")}:
                      |  type: ${talk.format()?.name ?: ""}
                      |  category: ${talk.category()?.name ?: ""}
                      |  title: ${talk.title.rawYaml()}
                      |  speakers :
                      |${(talk.speakers().joinToString("\n") { it.yml() }).prependIndent("    ")}"""
                        .trimMargin()
                        .prependIndent("  ")
                }
            (parentFile / "content" / "sessions.yml").writeText(sessionYml)

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
                  |name: ${displayName?.rawYaml() ?: ""}
                  |company: ${company?.rawYaml() ?: ""}
                  |city: ${city?.rawYaml() ?: ""}
                  |photoURL: ${photoURL.rawYaml()}
                  |socials:
                  |${socials().joinToString("\n") {
                    """  - icon: ${it.type}
                      |    link: ${it.link}
                      |    name: ${it.name}""".trimMargin()
                }}
                  |---
                  |$bio
                  |""".trimMargin()

            val speakersDir = parentFile / "content" / "speakers"
            if (speakersDir.mkdirs()) {
                Logger.info { "Create folder $speakersDir" }
            }
            speakers.forEach { (feature, speaker) ->
                val file = speakersDir / "${speaker.key()}.md"
                file.writeText(speaker.toContent(feature))
                Logger.info { "Created file $file for ${speaker.displayName}" }
            }

        }
    }
}