package kotlinx.cli

import org.junit.Test

class TestAntExample {
    @Test fun testAntExample() {
        /*
            This example demonstrates how to build a "real life" CLI, like the one of 'ant' command
            (see https://commons.apache.org/proper/commons-cli/usage.html),
            and to extend CLI API to your liking.

            ant [options] [target [target2 [target3] ...]]
              Options:
              -help                  print this message
              -projecthelp           print project help information
              -version               print the version information and exit
              -quiet                 be extra quiet
              -verbose               be extra verbose
              -debug                 print debugging information
              -emacs                 produce logging information without adornments
              -logfile <file>        use given file for log
              -logger <classname>    the class which is to perform logging
              -listener <classname>  add an instance of class as a project listener
              -buildfile <file>      use given buildfile
              -D<property>=<value>   use value for given property
              -find <file>           search for buildfile towards the root of the
                                     filesystem and use it
         */

        val cli = CommandLineInterface(
                "ant",
                usage = "ant [options] [target [target2 [target3] ...]]",
                shortTagPrefix = "-"
        )

        cli.onFlag("-projectHelp", "print project help information").once { printProjectHelp() }

        cli.onFlag("-version", "print the version information and exit").once { printVersion() }

        val verbosity by sharedOption(Verbosity.NORMAL) {
            cli.onFlag("-quiet", "be extra quiet").storeShared(Verbosity.QUIET)
            cli.onFlag("-verbose", "be extra verbose").storeShared(Verbosity.VERBOSE)
        }

        val printDebugInfo by cli.onFlag("-debug", "print debugging information").storeTrue()

        val noAdornments by cli.onFlag("-emacs", "produce logging information without adornments").storeTrue()

        val logFile by cli.onFlagValue("-logfile", "<file>", "use given file for log").store()

        val logger by cli.onFlagValue("-logger", "<classname>", "the class which is to perform logging").store()

        val listener by cli.onFlagValue("-listener", "<listener>", "add an instance of class as a project listener").store()

        val buildFile by cli.onFlagValue("-buildfile", "<file>", "use given buildfile").store()

        val extraProperties by cli.onFlagValue("-D", "<property>=<value>", "use value for given property")
                .toKeyValuePair().storeToMap()

        val findFile by cli.onFlagValue("-find", "<file>", "search for buildfile towards the root of the filesystem and use it").store()

        try {
            cli.parseArgs()
        }
        catch (e: HelpPrintedException) {
            // exit
        }
    }

    private fun Event<String>.toKeyValuePair() =
            map {
                val (key, value) = it.split('=', limit = 2)
                key to value
            }

    private fun Event<Pair<String, String>>.storeToMap(): ArgumentValue<Map<String, String>> {
        val map = hashMapOf<String, String>()
        add { (key, value) -> map[key] = value }
        return ArgumentStorage(map)
    }

    private fun printProjectHelp() {}

    private fun printVersion() {}

    enum class Verbosity {
        QUIET, NORMAL, VERBOSE
    }
}