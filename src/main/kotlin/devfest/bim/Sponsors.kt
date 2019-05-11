package devfest.bim

import com.opencsv.CSVReaderBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File

// -----------------
// See https://docs.google.com/spreadsheets/d/1CqEdaFVFQzXWbahS0Vc-Ugt2FZ2abd0NAzzq5ruBwNI/edit#gid=0
// To edit data
// -----------------

data class Job(
    val title: String,
    val url: String,
    val contact: String,
    val city: String
)

data class ManualSponsor(
    val name: String,
    val category: String,
    val logoExtension: String,
    val jobs: List<Job>
) {

    companion object {
        private val all: List<ManualSponsor> by lazy {
            val adapter: JsonAdapter<List<ManualSponsor>> = Moshi.Builder()
                .build()
                .adapter(Types.newParameterizedType(List::class.java, ManualSponsor::class.java))

            val json = File("manual-sponsor.json").readText()
            adapter.fromJson(json) ?: throw IllegalStateException("Fail to read 'manual-sponsor.json'")
        }

        fun findByName(name: String): ManualSponsor? =
            all.filter { it.name.similarity(name) > .5 }
                .maxBy { it.name.similarity(name) }
    }
}

data class Sponsor(
    val timestamp: String,
    val name: String,
    val logo: String,
    val twitter: String?,
    val facebook: String?,
    val linkedin: String?,
    val values: String,
    val lang: String,
    val tweetIntro: String?,
    val meetBooth: String?,
    val specialThanks: String?,
    val description: String,
    val website: String?,
    val why: String?,
    val contact: String
) {

    val key: String
        get() = name.toLowerCase().normalize()

    val socials: List<Social> by lazy {
        (emptyList<Social>() +
                (facebook?.let { listOf(Social.facebook(it)) } ?: emptyList()) +
                (twitter?.let { listOf(Social.twitter(it)) } ?: emptyList()) +
                (linkedin?.let { listOf(Social.linkedIn(it)) } ?: emptyList()))
            .filter { it.name.isNotEmpty() }
            .filter { it.name.toLowerCase() != "compte non actif"}
    }

    val category: String by lazy {
        ManualSponsor.findByName(name)
            ?.category
            ?.toLowerCase()
            ?: throw IllegalStateException("No '$name' found into manual-sponsor.json !!")
    }
    val logoExtension: String by lazy {
        ManualSponsor.findByName(name)
            ?.logoExtension
            ?: "png"
    }
    val jobs: List<Job> by lazy {
        ManualSponsor.findByName(name)
            ?.jobs
            ?: throw IllegalStateException("No '$name' found into manual-sponsor.json !!")
    }

    companion object {

        fun all(file: File): List<Sponsor> =
            CSVReaderBuilder(file.reader())
                .withSkipLines(1)
                .build()
                .readAll()
                .map {
                    Sponsor(
                        it[0],
                        it[1],
                        it[2],
                        it[3].nullIfEmpty(),
                        it[4].nullIfEmpty(),
                        it[5].nullIfEmpty(),
                        it[6],
                        it[7],
                        it[8].nullIfEmpty(),
                        it[9].nullIfEmpty(),
                        it[10].nullIfEmpty(),
                        it[11],
                        it[12].nullIfEmpty(),
                        it[13].nullIfEmpty(),
                        it[14]
                    )
                }
    }
}