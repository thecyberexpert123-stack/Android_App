package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.presentation.components.ide.extractSymbols
import com.cyberexpert.androde.presentation.components.ide.SymbolKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OutlineTest {

    @Test
    fun `extractSymbols finds kotlin class and function`() {
        val content = """
            class MyClass {
                fun myFunction() {
                    val x = 1
                }
                private fun privateFunc() {}
            }
        """.trimIndent()
        val symbols = extractSymbols(content, "kotlin")
        assertTrue(symbols.any { it.name == "MyClass" && it.kind == SymbolKind.CLASS })
        assertTrue(symbols.any { it.name == "myFunction" && it.kind == SymbolKind.FUNCTION })
    }

    @Test
    fun `extractSymbols finds java class`() {
        val content = """
            public class TestJava {
                public void doSomething() {}
            }
        """.trimIndent()
        val symbols = extractSymbols(content, "java")
        assertTrue(symbols.any { it.name == "TestJava" })
    }

    @Test
    fun `extractSymbols finds python def`() {
        val content = """
            class MyPythonClass:
                def my_method(self):
                    pass
            def top_level():
                pass
        """.trimIndent()
        val symbols = extractSymbols(content, "python")
        assertTrue(symbols.any { it.name == "MyPythonClass" })
        assertTrue(symbols.any { it.name == "my_method" })
        assertTrue(symbols.any { it.name == "top_level" })
    }

    @Test
    fun `extractSymbols handles empty content`() {
        val symbols = extractSymbols("", "kotlin")
        assertEquals(0, symbols.size)
    }

    @Test
    fun `extractSymbols distinct by name and line`() {
        val content = """
            fun duplicate() {}
            fun duplicate() {}
        """.trimIndent()
        val symbols = extractSymbols(content, "kotlin")
        // Should have 2 entries with different lines
        assertEquals(2, symbols.size)
    }

    @Test
    fun `extractSymbols javascript class and function`() {
        val content = """
            class MyJSClass {
                constructor() {}
            }
            function myFunc() {}
            const arrow = () => {}
        """.trimIndent()
        val symbols = extractSymbols(content, "javascript")
        assertTrue(symbols.any { it.name == "MyJSClass" })
        assertTrue(symbols.any { it.name == "myFunc" })
    }
}
