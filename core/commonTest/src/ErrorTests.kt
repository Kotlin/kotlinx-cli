/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

@file:Suppress("UNUSED_VARIABLE")

package kotlinx.cli

import kotlin.test.*


internal fun ArgParser.avoidProcessExit() = apply { outputAndTerminate = { message, _ ->  error(message) } }

class ErrorTests {

    @Test
    fun testExtraArguments() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val addendums by argParser.argument(ArgType.Int, "addendums", description = "Addendums").multiple(2)
        val output by argParser.argument(ArgType.String, "output", "Output file")
        val debugMode by argParser.option(ArgType.Boolean, "debug", "d", "Debug mode")
        val exception = assertFailsWith<IllegalStateException> { argParser.parse(
                arrayOf("2", "-d", "3", "out.txt", "something", "else", "in", "string")) }
        assertTrue("Too many arguments! Couldn't process argument something" in exception.message!!)
    }

    @Test
    fun testUnknownOption() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val output by argParser.option(ArgType.String, "output", "o", "Output file")
        val input by argParser.option(ArgType.String, "input", "i", "Input file")
        val exception = assertFailsWith<IllegalStateException> {
            argParser.parse(arrayOf("-o", "out.txt", "-d", "-i", "input.txt"))
        }
        assertTrue("Unknown option -d" in exception.message!!)
    }

    @Test
    fun testWrongFormat() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val number by argParser.option(ArgType.Int, "number", description = "Integer number")
        val exception = assertFailsWith<IllegalStateException> {
            argParser.parse(arrayOf("--number", "out.txt"))
        }
        assertTrue("Option number is expected to be integer number. out.txt is provided." in exception.message!!)
    }

    enum class RenderEnum {
        TEXT,
        HTML;
    }

    @Test
    fun testWrongChoice() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val useShortForm by argParser.option(ArgType.Boolean, "short", "s", "Show short version of report").default(false)
        val renders by argParser.option(ArgType.Choice<RenderEnum>(),
                "renders", "r", "Renders for showing information").multiple().default(listOf(RenderEnum.TEXT))
        val exception = assertFailsWith<IllegalStateException> {
            argParser.parse(arrayOf("-r", "xml"))
        }
        assertTrue("Option renders is expected to be one of [text, html]. xml is provided." in exception.message!!)
    }

    @Test
    fun testWrongEnumChoice() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val sources by argParser.option(ArgType.Choice<DataSourceEnum>(),
                "sources", "s", "Data sources").multiple().default(listOf(DataSourceEnum.PRODUCTION))
        val exception = assertFailsWith<IllegalStateException> {
            argParser.parse(arrayOf("-s", "debug"))
        }
        assertTrue("Option sources is expected to be one of [local, staging, production]. debug is provided." in exception.message!!)
    }

    object KeyValue: ArgType<Pair<String, String>>(true) {
        override val description: kotlin.String
            get() {
                return "{ Value should have format <key>=<value> }"
            }

        override fun convert(value: kotlin.String, name: kotlin.String) =
            if (!value.contains('=')) {
                throw ParsingException("Option $name should have value described with format <key>=<value>, but got $value")
            } else {
                val (key, parameterValue) = value.split('=', limit = 2)
                key.trim() to parameterValue.trim()
            }
    }

    @Test
    fun testCustomOptionType() {
        val argParser = ArgParser("testParser").avoidProcessExit()
        val useShortForm by argParser.option(ArgType.Boolean, shortName = "s", description = "Show short version of report").default(false)
        val option by argParser.option(KeyValue, shortName = "op", description = "Additional options").required()
        val output by argParser.option(ArgType.String, shortName = "o", description = "Output file")

        val exception = assertFailsWith<IllegalStateException> {
            argParser.parse(arrayOf("-op", "-Xrender"))
        }
        assertTrue("Option option should have value described with format <key>=<value>, but got -Xrender" in exception.message!!)
    }
}
