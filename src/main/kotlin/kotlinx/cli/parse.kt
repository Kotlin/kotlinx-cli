package kotlinx.cli

fun CommandLineInterface.parseArgs(vararg args: String) {
    CommandLineParser(this).parse(args)
}

fun CommandLineInterface.parse(args: Array<out String>) {
    CommandLineParser(this).parse(args)
}

fun CommandLineInterface.parse(args: List<String>) {
    CommandLineParser(this).parse(args)
}