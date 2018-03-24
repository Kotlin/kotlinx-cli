package kotlinx.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class TestPositionalArguments {
    class TestCli1 : CommandLineInterface("test") {
        val arg1 by positionalArgument("ARG1", "help for arg1", 1)
        val arg2 by positionalArgument("ARG2", "")
    }

    @Test fun testSimplePositionalArguments0() {
        TestCli1().run {
            assertFails {
                parseArgs()
            }
        }
    }

    @Test fun testSimplePositionalArguments1() {
        TestCli1().run {
            parseArgs("x1")
            assertEquals("x1", arg1)
            assertEquals(null, arg2)
        }
    }

    @Test fun testSimplePositionalArguments2() {
        TestCli1().run {
            parseArgs("x1", "x2")
            assertEquals("x1", arg1)
            assertEquals("x2", arg2)
        }
    }

    @Test fun testSimplePositionalArguments3() {
        TestCli1().run {
            assertFails {
                parseArgs("x1", "x2", "x3")
            }
        }
    }


    class TestCli2 : CommandLineInterface("test") {
        val arg1 by positionalArgument("X1", "integer", 0, minArgs = 1, mapping = String::toInt)
        val arg2 by positionalArgument("X2", "double", 0.0, minArgs = 1, mapping = String::toDouble)
        val otherArgs by positionalArgumentsList("Xs", "case-insensitive strings", mapping = String::toLowerCase)
    }

    @Test fun testMappedPositionalArgumentsErr() {
        TestCli2().run {
            assertFails {
                parseArgs()
            }
            assertFails {
                parseArgs("1")
            }
        }
    }

    @Test fun testMappedPositionalArguments() {
        TestCli2().run {
            parseArgs("1", "2", "ABC", "DEF")
            assertEquals(1, arg1)
            assertEquals(2.0, arg2)
            assertEquals(listOf("abc", "def"), otherArgs)
        }
    }


    class TestCli3 : CommandLineInterface("test") {
        var accumulator = 0

        init {
            positionalAction("ARG", "integers", maxArgs = Int.MAX_VALUE) { accumulator += it.toInt() }
        }
    }

    @Test fun testPositionalActions() {
        TestCli3().run {
            parseArgs("1", "2", "3")
            assertEquals(6, accumulator)
        }
    }


    class TestCli4 : CommandLineInterface("test") {
        val accumulator by foldPositionalArguments("ARG", "integers", 0) { value, arg -> value + arg.toInt() }
    }

    @Test fun testFoldArguments() {
        TestCli4().run {
            parseArgs("1", "10", "100")
            assertEquals(111, accumulator)
        }
    }
}