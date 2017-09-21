package kotlinx.cli

interface HelpPrinter {
    fun begin() {}
    fun printText(text: String)
    fun printSeparator()
    fun printEntry(helpEntry: String, description: String)
    fun end() {}
}

fun HelpPrinter.printSectionOrNothing(text: String?) {
    if (text == null) return
    printText(text)
    printSeparator()
}

fun HelpPrinter.printSection(text: String) {
    printText(text)
    printSeparator()
}

interface HelpEntry {
    fun printHelp(helpPrinter: HelpPrinter)
}


class SimpleHelpPrinter(private val syntaxWidth: Int) : HelpPrinter {
    override fun printText(text: String) {
        println(text)
    }

    override fun printSeparator() {
        println()
    }

    override fun printEntry(helpEntry: String, description: String) {
        if (helpEntry.length <= syntaxWidth) {
            println("  ${helpEntry.padEnd(syntaxWidth)}  $description")
        }
        else {
            println("  $helpEntry")
            println("  ${"".padEnd(syntaxWidth)}  $description")
        }
    }
}


fun CommandLineInterface.helpSeparator() {
    addHelpEntry(object : HelpEntry {
        override fun printHelp(helpPrinter: HelpPrinter) {
            helpPrinter.printSeparator()
        }
    })
}