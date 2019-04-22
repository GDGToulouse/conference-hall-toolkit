package devfest.bim

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.net.URL


class Events(eventId: String, apiKey: String) {

    private val adapter: JsonAdapter<Event> =
        Moshi.Builder()
            .build()
            .adapter(Event::class.java)

    val event: Event by lazy {
        // val data = File("/home/mpa/oss/workspace/devfest/2019/export-api.json").readText()
        val url = "https://conference-hall.io/api/v1/event/$eventId?key=$apiKey"
        val data = URL(url).readText()
        adapter.fromJson(data) ?: throw IllegalStateException("Event $eventId not found!")
    }

    fun Talk.category(): Category? =
        event.categories.find { it.id == this.categories }

    fun Talk.format(): Format? =
        event.formats.find { it.id == this.formats }

    fun Talk.speakers(): String =
        this.speakers
            .map { speakerId -> event.speakers.find { it.uid == speakerId } }
            .joinToString(" and ")

}