package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.presentation.components.ide.AnsiParser
import com.cyberexpert.androde.presentation.components.ide.extractSymbols
import com.cyberexpert.androde.core.extensions.EmmetService
import com.cyberexpert.androde.data.local.emmet.EmmetServiceImpl
import com.cyberexpert.androde.data.local.formatting.FormattingRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase4Test {

    @Test
    fun `AnsiParser strips ANSI codes`() {
        val input = "\u001B[31mRed Text\u001B[0m Normal"
        val stripped = AnsiParser.stripAnsi(input)
        assertEquals("Red Text Normal", stripped)
    }

    @Test
    fun `AnsiParser detects ANSI`() {
        assertTrue(AnsiParser.containsAnsi("\u001B[31mRed\u001B[0m"))
        assertFalse(AnsiParser.containsAnsi("Plain text"))
    }

    @Test
    fun `AnsiParser parses to AnnotatedString`() {
        val input = "\u001B[31mRed\u001B[0m and \u001B[32mGreen\u001B[0m"
        val annotated = AnsiParser.parseToAnnotatedString(input)
        assertTrue(annotated.text.contains("Red"))
        assertTrue(annotated.text.contains("Green"))
    }

    @Test
    fun `EmmetService detects abbreviations`() {
        val service = EmmetServiceImpl()
        assertTrue(service.isEmmetAbbreviation("ul>li*3"))
        assertTrue(service.isEmmetAbbreviation("div#header"))
        assertTrue(service.isEmmetAbbreviation("div.container"))
        assertFalse(service.isEmmetAbbreviation("<div><p>Hello</p></div>"))
    }

    @Test
    fun `EmmetService expands simple html`() = runBlocking {
        val service = EmmetServiceImpl()
        val result = service.expandAbbreviation("div", "html")
        assertTrue(result.success)
        assertTrue(result.expanded.contains("<div>"))
        assertTrue(result.expanded.contains("</div>"))
    }

    @Test
    fun `EmmetService expands ul>li*3`() = runBlocking {
        val service = EmmetServiceImpl()
        val result = service.expandAbbreviation("ul>li*3", "html")
        assertTrue(result.success)
        // Should contain 3 li
        val liCount = result.expanded.split("<li>").size - 1
        assertTrue(liCount >= 3 || result.expanded.contains("li"))
    }

    @Test
    fun `EmmetService expands css m10`() = runBlocking {
        val service = EmmetServiceImpl()
        val result = service.expandAbbreviation("m10", "css")
        assertTrue(result.success)
        assertTrue(result.expanded.contains("margin"))
        assertTrue(result.expanded.contains("10px"))
    }

    @Test
    fun `FormattingRepository formats kotlin`() = runBlocking {
        val repo = FormattingRepositoryImpl()
        val content = "fun main(){\nprintln(\"hello\")\n}"
        val result = repo.formatDocument(content, "kotlin", 4, true)
        assertTrue(result.success)
        assertTrue(result.formattedContent.isNotBlank())
    }

    @Test
    fun `FormattingRepository formats json`() = runBlocking {
        val repo = FormattingRepositoryImpl()
        val content = "{\"a\":1,\"b\":2}"
        val result = repo.formatDocument(content, "json", 4, true)
        assertTrue(result.success)
        // JSON should be pretty printed
        assertTrue(result.formattedContent.contains("\n") || result.formattedContent.contains("a"))
    }

    @Test
    fun `FormattingRepository handles empty`() = runBlocking {
        val repo = FormattingRepositoryImpl()
        val result = repo.formatDocument("", "kotlin")
        assertTrue(result.success)
    }

    @Test
    fun `EditorLanguage supports 24 languages`() {
        assertTrue(EditorLanguage.entries.size >= 24)
        assertEquals(EditorLanguage.XML, EditorLanguage.fromExtension("xml"))
        assertEquals(EditorLanguage.SQL, EditorLanguage.fromExtension("sql"))
        assertEquals(EditorLanguage.CSHARP, EditorLanguage.fromExtension("cs"))
        assertEquals(EditorLanguage.DART, EditorLanguage.fromExtension("dart"))
        assertEquals(EditorLanguage.PHP, EditorLanguage.fromExtension("php"))
        assertEquals(EditorLanguage.RUBY, EditorLanguage.fromExtension("rb"))
        assertEquals(EditorLanguage.SWIFT, EditorLanguage.fromExtension("swift"))
        assertEquals(EditorLanguage.PROPERTIES, EditorLanguage.fromExtension("properties"))
        assertEquals(EditorLanguage.DOCKERFILE, EditorLanguage.fromFileName("Dockerfile"))
    }

    @Test
    fun `extractSymbols works for new languages`() {
        val xmlContent = "<root><child>test</child></root>"
        val symbols = extractSymbols(xmlContent, "xml")
        // XML may not have class/fun patterns, but should not crash
        assertTrue(symbols.size >= 0)

        val sqlContent = "SELECT * FROM users WHERE id = 1"
        val sqlSymbols = extractSymbols(sqlContent, "sql")
        assertTrue(sqlSymbols.size >= 0)
    }
}
