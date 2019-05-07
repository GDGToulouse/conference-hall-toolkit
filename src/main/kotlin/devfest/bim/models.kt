package devfest.bim


data class Event(
    val id: String,
    val name: String,
    val categories: List<Category> = listOf(),
    val formats: List<Format> = listOf(),
    val talks: List<Talk> = listOf(),
    val speakers: List<Speaker> = listOf(),
    val conferenceDates: ConferenceDate,
    val state: String
)

data class ConferenceDate(
    val start: String,
    val end: String
)

data class Category(
    val name: String,
    val description: String,
    val id: String
) {
    override fun toString() = name
}

data class Format(
    val name: String,
    val description: String,
    val id: String
) {
    override fun toString() = name
}

data class Speaker(
    val uid: String,
    val displayName: String?,
    val bio: String,
    val company: String,
    val city: String,
    val photoURL: String,
    val twitter: String?,
    val github: String?
) {
    override fun toString() =
        if (displayName == null || displayName.isBlank()) uid else displayName
}

data class Social(
    val type: String,
    val name: String
) {
    val link: String
        get() = when (type) {
            "twitter" -> "https://twitter.com/$name"
            "github"  -> "https://github.com/$name"
            else      -> ""
        }

    companion object {
        fun twitter(handle: String): Social =
            Social(
                "twitter",
                if (handle.startsWith("@")) handle.substring(1) else handle
            )

        fun github(name: String): Social =
            Social(
                "github",
                if (name.startsWith("@")) name.substring(1) else name
            )
    }
}

data class Talk(
    val id: String,
    val title: String,
    val state: String,
    val level: String,
    val abstract: String,
    val categories: String,
    val formats: String,
    val speakers: List<String> = listOf(),
    val comments: String
)