package kotlinx.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class TestGnuLongArgs {
    @Test fun testGnuLongArgs() {
        val cli = CommandLineInterface("test", longTagPrefixes = listOf("--"), longTagValueDelimiter = "=")
        val a by cli.flagValueArgument("--a", "A", "value of a")
        val b by cli.flagValueArgument("--b", "B", "value of b")

        cli.parseArgs("--a=1=1", "--b=2")
        assertEquals("1=1", a)
        assertEquals("2", b)
    }
}