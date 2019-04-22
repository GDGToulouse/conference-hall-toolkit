package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File


class Check : CliktCommand(help = "Check new Talks with data issues") {
    private val eventId: String by option("-e", "--event", help = "the event Id").required()
    private val apiKey: String by option("-k", "--api-key", help = "the api key").required()
    private val alreadyKnown: File by argument(help = "a file with talk id per line that already have a data issue")
        .file(exists = true, readable = true, fileOkay = true)
        .default(File("KNOWN_TALKS_WITH_DATA_ISSUE.txt"))

    override fun run() =
        with(Events(eventId, apiKey)) {
            val talksWithDataIssue =
                alreadyKnown.readLines()
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

            val newTalksWithIssue = event.talks
                .filterNot { talksWithDataIssue.contains(it.id) }
                .filter { it.category() == null || it.format() == null }

            if (newTalksWithIssue.isEmpty())
                println("No new talk with data issue")
            else
                newTalksWithIssue.forEachIndexed { idx, talk ->
                    println("$idx / ⚠️ NEW TALK WITH DATA ISSUE [${talk.id}] ${talk.title}\n\tspeakers=${talk.speakers()}")
                }
        }
}
