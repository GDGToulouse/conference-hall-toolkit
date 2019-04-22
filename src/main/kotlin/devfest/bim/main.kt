package devfest.bim

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) =
    All()
        .subcommands(Check, Stats)
        .main(args)

class All : CliktCommand() {
    override fun run() = Unit
}