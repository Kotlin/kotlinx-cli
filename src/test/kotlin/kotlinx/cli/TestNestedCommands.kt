package kotlinx.cli

import org.junit.Test

class TestNestedCommands {
    @Test fun testNestedCommands() {
        val cli = CommandLineInterface("testNestedCommands")

        val commonFlag by cli.flagArgument("--common", "common flag for foo and bar")

        val fooCmd = CommandLineInterface("foo")
        val fooX by fooCmd.positionalArgument("X", "X argument for foo")
        val fooY by fooCmd.positionalArgument("Y", "Y argument for foo")
        cli.command(fooCmd, "foo subcommand", { foo(commonFlag, fooX, fooY) })

        val barCmd = CommandLineInterface("bar")
        val barX by barCmd.positionalArgument("X", "X argument for bar")
        val barFlag by barCmd.flagArgument("--flag", "flag argument for bar")
        cli.command(barCmd, "bar subcommand", { bar(commonFlag, barX, barFlag) })

        cli.printHelp()

        cli.parseArgs("foo", "42")
        cli.parseArgs("bar", "--flag")
    }

    private fun foo(commonFlag: Boolean, fooX: String?, fooY: String?) {
        println("foo: $commonFlag $fooX $fooY")
    }

    private fun bar(commonFlag: Boolean, barX: String?, barFlag: Boolean) {
        println("bar: $commonFlag $barX $barFlag")
    }
}