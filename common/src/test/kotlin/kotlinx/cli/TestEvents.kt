package kotlinx.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class TestEvents {
    @Test fun testEvents() {
        val cli = CommandLineInterface("test")
        val arg1 by cli.onArgument("ARG1", "arg1 help").store("")
        val args by cli.onRemainingArguments("ARGS", "args help").map { it.toInt() }.addToList()
        val flag by cli.onFlag("-flag", "flag help").storeTrue()
        val foo by cli.onFlagValue("-foo", "FOO", "foo help").map { it.toLowerCase() }.store()
        val bar by cli.onFlagValue("-bar", "BAR", "bar help").map { it.toInt() }.store()

        cli.parseArgs("1", "2", "3", "-flag", "-foo", "FOO", "-bar", "42")
        assertEquals("1", arg1)
        assertEquals(listOf(2, 3), args)
        assertEquals(true, flag)
        assertEquals("foo", foo)
        assertEquals(42, bar)
    }
}