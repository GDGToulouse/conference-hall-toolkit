package devfest.bim

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.net.URL
import java.text.Normalizer


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

    private fun String.normalize(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .toLowerCase()
            .replace(Regex("[\\s]"), "-")
            .replace(Regex("[^\\p{ASCII}]"), "")
            .replace(Regex("[\\W]"), "_")

    fun Speaker.key(): String =
        this.toString().toLowerCase().normalize()

    fun Speaker.socials(): List<Social> =
        emptyList<Social>() +
                (if (twitter?.isNotBlank() == true) listOf(Social.twitter(twitter)) else emptyList()) +
                if (github?.isNotBlank() == true) listOf(Social.github(github)) else emptyList()

    fun Talk.key(): String =
        this.title.toLowerCase().normalize()

    fun Talk.category(): Category? =
        event.categories.find { it.id == this.categories }

    fun Talk.format(): Format? =
        event.formats.find { it.id == this.formats }

    fun Talk.speakers(): List<Speaker> =
        this.speakers.mapNotNull { speakerId ->
            event.speakers.find { it.uid == speakerId }
        }

    fun Talk.speakerNames(): String =
        this.speakers().joinToString(" and ")

}