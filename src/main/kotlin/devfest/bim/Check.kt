package devfest.bim

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.File
import java.net.URL


// list of talk ID with already known data issue (missing category/format):
private val talksWithDataIssue =
    File("KNOWN_TALKS_WITH_DATA_ISSUE.txt").readLines()
        .filter { it.isNotBlank() }

private val adapter: JsonAdapter<Event> =
    Moshi.Builder()
        .build()
        .adapter(Event::class.java)

fun loadEvent(eventId: String, apiKey: String): Event {
    // val data = File("/home/mpa/oss/workspace/devfest/2019/export-api.json").readText()
    val url = "https://conference-hall.io/api/v1/event/$eventId?key=$apiKey"
    val data = URL(url).readText()
    return adapter.fromJson(data) ?: throw IllegalStateException("Event $eventId not found!")
}

fun main(args: Array<String>) {
    if (args.size < 2) throw IllegalArgumentException("EventID and API key must be provided as command line args")
    val (eventId, apiKey) = args

    val event = loadEvent(eventId, apiKey)

    fun Talk.category(): Category? =
        event.categories.find { it.id == this.categories }

    fun Talk.format(): Format? =
        event.formats.find { it.id == this.formats }

    fun Talk.speakers(): String =
        this.speakers
            .map { speakerId -> event.speakers.find { it.uid == speakerId } }
            .joinToString(" and ")

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
