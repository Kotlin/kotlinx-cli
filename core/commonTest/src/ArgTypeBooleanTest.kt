package kotlinx.cli

import kotlin.test.*

class ArgTypeBooleanTest {
    private fun message(value: String, name: String) =
        "Option $name is expected to be boolean value. $value is provided."

    @Test
    fun testConvert() {
        //@formatter:off
        assertEquals(true, ArgType.Boolean.convert("true", ""))
        assertEquals(true, ArgType.Boolean.convert("True", ""))
        assertEquals(true, ArgType.Boolean.convert("TRUE", ""))
        assertEquals(true, ArgType.Boolean.convert("tRuE", ""))
        assertEquals(true, ArgType.Boolean.convert("true", "OptionName"))
        assertEquals(true, ArgType.Boolean.convert("True", "OptionName"))
        assertEquals(true, ArgType.Boolean.convert("TRUE", "OptionName"))
        assertEquals(true, ArgType.Boolean.convert("tRuE", "OptionName"))

        assertEquals(false, ArgType.Boolean.convert("false", ""))
        assertEquals(false, ArgType.Boolean.convert("False", ""))
        assertEquals(false, ArgType.Boolean.convert("FALSE", ""))
        assertEquals(false, ArgType.Boolean.convert("fAlSe", ""))
        assertEquals(false, ArgType.Boolean.convert("false", "OptionName"))
        assertEquals(false, ArgType.Boolean.convert("False", "OptionName"))
        assertEquals(false, ArgType.Boolean.convert("FALSE", "OptionName"))
        assertEquals(false, ArgType.Boolean.convert("fAlSe", "OptionName"))

        assertFailsWith<ParsingException>(message("", "")) { ArgType.Boolean.convert("", "") }
        assertFailsWith<ParsingException>(message("0", "")) { ArgType.Boolean.convert("0", "") }
        assertFailsWith<ParsingException>(message("1", "")) { ArgType.Boolean.convert("1", "") }
        assertFailsWith<ParsingException>(message("yes", "")) { ArgType.Boolean.convert("yes", "") }
        assertFailsWith<ParsingException>(message("no", "")) { ArgType.Boolean.convert("no", "") }
        assertFailsWith<ParsingException>(message("on", "")) { ArgType.Boolean.convert("on", "") }
        assertFailsWith<ParsingException>(message("off", "")) { ArgType.Boolean.convert("off", "") }
        assertFailsWith<ParsingException>(message("hello", "")) { ArgType.Boolean.convert("hello", "") }
        assertFailsWith<ParsingException>(message("IntellĲ", "")) { ArgType.Boolean.convert("IntellĲ", "") }

        assertFailsWith<ParsingException>(message("", "OptionName")) { ArgType.Boolean.convert("", "OptionName") }
        assertFailsWith<ParsingException>(message("0", "OptionName")) { ArgType.Boolean.convert("0", "OptionName") }
        assertFailsWith<ParsingException>(message("1", "OptionName")) { ArgType.Boolean.convert("1", "OptionName") }
        assertFailsWith<ParsingException>(message("yes", "OptionName")) { ArgType.Boolean.convert("yes", "OptionName") }
        assertFailsWith<ParsingException>(message("no", "OptionName")) { ArgType.Boolean.convert("no", "OptionName") }
        assertFailsWith<ParsingException>(message("on", "OptionName")) { ArgType.Boolean.convert("on", "OptionName") }
        assertFailsWith<ParsingException>(message("off", "OptionName")) { ArgType.Boolean.convert("off", "OptionName") }
        assertFailsWith<ParsingException>(message("hello", "OptionName")) { ArgType.Boolean.convert("hello", "OptionName") }
        assertFailsWith<ParsingException>(message("IntellĲ", "")) { ArgType.Boolean.convert("IntellĲ", "OptionName") }
        //@formatter:on
    }
}