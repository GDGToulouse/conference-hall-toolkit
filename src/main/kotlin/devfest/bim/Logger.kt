package devfest.bim

import com.github.ajalt.mordant.TermColors
import java.time.LocalTime


object Logger {
    private val t = TermColors()
    var enabled: Boolean = true

    fun info(block: () -> String) {
        log(t.blue("INFO"), block)
    }

    fun warn(block: () -> String) {
        log(t.yellow("WARN"), block)
    }

    fun error(block: () -> String) {
        log(t.yellow("ERROR"), block)
    }

    private fun log(level: String, block: () -> String) {
        if (enabled) println("[$level] ${LocalTime.now()} - ${block()}")
    }


}