package devfest.bim

import java.text.Normalizer


fun String.normalize(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .toLowerCase()
        .replace(Regex("[\\s]"), "-")
        .replace(Regex("[^\\p{ASCII}]"), "")
        .replace(Regex("[\\W]"), "_")

fun String.nullIfEmpty(): String? =
    if (isEmpty()) null else this

fun String.levenshtein(other: String): Int =
    when {
        this == other -> 0
        (this == "")  -> other.length
        (other == "") -> this.length
        else          -> {
            val initialRow = (0 until other.length + 1)
                .map { it }
                .toList()

            (0 until this.length)
                .fold(initialRow) { previous, u ->
                    (0 until other.length)
                        .fold(listOf(u + 1)) { row, v ->
                            row + minOf(
                                row.last() + 1,
                                previous[v + 1] + 1,
                                previous[v] + (if (this[u] == other[v]) 0 else 1)
                            )
                        }
                }.last()
        }
    }

fun String.similarity(other: String): Double =
    1.0 - this.toLowerCase().levenshtein(other.toLowerCase()) / maxOf(this.length, other.length).toDouble()