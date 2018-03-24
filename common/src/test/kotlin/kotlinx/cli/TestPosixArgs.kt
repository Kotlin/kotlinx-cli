package kotlinx.cli

import kotlin.test.Test
import kotlin.test.assertEquals

class TestPosixArgs {
    @Test fun testPosixArgs() {
        var a = 0
        var b = 0
        val c = mutableListOf<String>()

        val cli = CommandLineInterface("test", shortTagPrefix = "-")
        cli.flagAction("-a", "aaa") { a++ }
        cli.flagAction("-b", "bbb") { b++ }
        cli.flagValueAction("-c", "C", "ccc") { c.add(it); }

        cli.parseArgs("-aabbcabab")

        assertEquals(2, a)
        assertEquals(2, b)
        assertEquals(listOf("abab"), c)
    }
}