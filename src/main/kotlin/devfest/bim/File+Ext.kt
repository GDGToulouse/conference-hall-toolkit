package devfest.bim

import java.io.File


operator fun File.div(name: String): File =
        resolve(name)