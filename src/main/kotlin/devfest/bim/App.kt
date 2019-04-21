package devfest.bim

import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter
import java.net.URL

data class Event(
        val id: String,
        val name: String,
        val categories: List<Category> = listOf(),
        val formats: List<Format> = listOf(),
        val talks: List<Talk> = listOf(),
        val speakers: List<Speaker> = listOf(),
        val conferenceDates: ConferenceDate,
        val address: String,
        val state: String)

data class ConferenceDate(
        val start: String,
        val end: String)

data class Category(
        val name: String,
        val description: String,
        val id: String) {

    override fun toString(): String {
        return name
    }
}

data class Format(
        val name: String,
        val description: String,
        val id: String) {
    override fun toString(): String {
        return name
    }
}

data class Speaker(
        val uid: String,
        val displayName: String,
        val bio: String,
        val company: String,
        val photoURL: String,
        val twitter: String,
        val github: String)

data class Talk(
        val id: String,
        val title: String,
        val state: String,
        val level: String,
        val abstract: String,
        val categories: String,
        val formats: String,
        val speakers: List<String> = listOf(),
        val comments: String) {
}

// list of talk ID with already known data issue (missing category/format):
private val KNOWN_TALKS_WITH_DATA_ISSUE = """
0HglDYrSXWJuwuceUIMg
0arw396JAObUJbY5Bdax
34KKtM1mjEigBwF7NYiz
3JfNO7UTnZrTjMUhWUwp
4cXBqqw4p10VvAbraE72
6zu2zZoxJUJe9UyZFXki
8R3gYW2VtIO1AR8AgsXU
92137UmOTNpTXfGO0vQL
I47D9yuenriF5kNkNbKy
MGsQXlP2KHM6Ckq5vLHN
N6HCWbKYkp818VKN9gfA
NnaBg7Jv2tQdmHRc5Lzn
O6suhphkZLhqaIVRZPbf
P4UK2bKyC5R75RrZwUPn
Rpwv5TRVHpr7gCbPBozS
TET0T3MGh3bE7tvlEyhC
UHHyNwKdc4bn3IGL5bwa
VZTjZWWSg4nGR58GdcOn
VZs4AtsXL4XB6QVOSZ26
W8X9M6MZIJaF6ULslBn2
Yd2XdwbtVftRfPA9x2JE
d62DMKQq5UUm8up3ka7K
dkEmC6Acadn1NCJTJl0Z
eVaQ2EmwbiE06qBidrB1
hcXcZV0ipk7eAvD5mE1X
jKnCOnZVFLOMJXcVSZHt
mFNDO830cBjI7mBvDBGw
q2b8SQV4qtVbw0MtzSbO
vfzX6Ur3QMTw9OI7irNx
yUhDc7XdC3mDm50wrz7r
"""

fun main(args: Array<String>) {

    if (args.size < 2) throw IllegalArgumentException("EventID and API key must be provided as command line args")

    val eventId = args.get(0)
    val apiKey = args.get(1)

    // val data = File("/home/mpa/oss/workspace/devfest/2019/export-api.json").readText()
    val data = URL("https://conference-hall.io/api/v1/event/"+ eventId +"?key="+apiKey).readText()

    val moshi = Moshi.Builder().build()
    val adapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    val event = adapter.fromJson(data)

    for (talk in event!!.talks) {
        val categorie = event.categories.find { c -> c.id == talk.categories }
        val format = event.formats.find { f -> f.id == talk.formats }

        val speakersList = event.speakers.filter { s -> talk.speakers.contains(s.uid) }.map { s -> if (!s.displayName.isNullOrEmpty()) s.displayName else s.uid }
        val speakers = when(speakersList.size)  {
            in Int.MIN_VALUE..0 -> "WTF!"
            1 -> speakersList.get(0)
            else -> speakersList.joinToString(" and " )
        }

        if ((categorie == null || format == null) && !KNOWN_TALKS_WITH_DATA_ISSUE.contains(talk.id)) {
            println("NEW TALK WITH DATA ISSUE uid=" + talk.id + " title=" + talk.title + " speakers=" + speakers)
        }
    }

}
