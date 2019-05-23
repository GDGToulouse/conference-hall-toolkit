package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

// ---------------
// Go to https://docs.google.com/forms/d/1OzB5Y8f8mHt4WP2EH28hD3iFABVebrdb44BHvhfjm3o/edit#responses
// then download as CSV
// ---------------

object GenerateSponsors : CliktCommand(name = "sponsor", help = "Generate sponsor content") {

    private val sponsorsFile: File by argument(help = "the sponsor CSV file")
        .file(exists = true, readable = true, fileOkay = true)
        .default(File("Formulaire Sponsors.csv"))

    override fun run() {

        val parentFile = sponsorsFile.absoluteFile.parentFile

        val selected = Sponsor.all(sponsorsFile)

        fun Sponsor.socialsDetails(): String {

            return if (socials.isEmpty()) ""
            else
                "socials:\n" +
                        socials.joinToString("\n") {
                            """  - icon: ${it.type}
                              |    link: ${it.link}
                              |    name: ${it.name}""".trimMargin()
                        }
        }

        fun Sponsor.jobsDetails(): String =
            if (jobs.isEmpty()) ""
            else jobs.fold("jobs:\n") { acc, job ->
                acc + """
                    |  - title: ${job.title}
                    |    url: ${job.url}
                    |    contact: ${job.contact}
                    |    city: ${job.city}
                    |""".trimMargin()
            }

        fun Sponsor.toContent(draft: Boolean = false): String =
            """---
                  |title: $name
                  |type: partner
                  |category: $category
                  |order: $order
                  |logo: /images/partners/logo-$key.$logoExtension
                  |lang: ${if (lang == "Français") "fr" else "en"}
                  |why: "$why"
                  |${socialsDetails()}
                  |${jobsDetails()}
                  |draft: $draft
                  |---
                  |$description
                  |""".trimMargin()

        // Dir
        val sponsorDir = parentFile.resolve("content").resolve("partners")
        if (sponsorDir.mkdirs()) {
            Logger.info { "Create folder $sponsorDir" }
        }
        selected
            .forEach { sponsor ->
                val dir = sponsorDir.resolve(sponsor.category)
                if (dir.mkdirs()) {
                    Logger.info { "Create folder $dir" }
                }
                val file = dir.resolve("${sponsor.key}.md")
                file.writeText(sponsor.toContent(false))
                Logger.info { "Created file $file for ${sponsor.name}" }
            }
    }

}
