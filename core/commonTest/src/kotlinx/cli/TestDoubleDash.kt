package kotlinx.cli

import kotlin.test.*

class TestDoubleDash {
    @Test fun testDoubleDash() {
        val cli = CommandLineInterface("testDoubleDash")
        val posArgs by cli.positionalArgumentsList("X", "xs")
        cli.flagAction("--foo", "foo") {
            throw AssertionError("Action should not be invoked")
        }

        cli.parseArgs("1", "--", "--foo", "--foo")
        assertEquals(listOf("1", "--foo", "--foo"), posArgs)
    }
}