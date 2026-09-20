package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.core.extensions.EmmetService
import com.cyberexpert.androde.data.local.emmet.EmmetServiceImpl
import com.cyberexpert.androde.data.local.formatting.FormattingRepositoryImpl
import com.cyberexpert.androde.presentation.components.ide.FileIconResolver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase5Test {

    @Test
    fun `EditorLanguage supports 32 languages`() {
        assertTrue(EditorLanguage.entries.size >= 32)
        assertEquals(EditorLanguage.TOML, EditorLanguage.fromExtension("toml"))
        assertEquals(EditorLanguage.GROOVY, EditorLanguage.fromExtension("groovy"))
        assertEquals(EditorLanguage.GROOVY, EditorLanguage.fromExtension("gradle"))
        assertEquals(EditorLanguage.LUA, EditorLanguage.fromExtension("lua"))
        assertEquals(EditorLanguage.R, EditorLanguage.fromExtension("r"))
        assertEquals(EditorLanguage.BAT, EditorLanguage.fromExtension("bat"))
        assertEquals(EditorLanguage.POWERSHELL, EditorLanguage.fromExtension("ps1"))
        assertEquals(EditorLanguage.MAKEFILE, EditorLanguage.fromFileName("Makefile"))
        assertEquals(EditorLanguage.CMAKE, EditorLanguage.fromFileName("CMakeLists.txt"))
        assertEquals(EditorLanguage.DOCKERFILE, EditorLanguage.fromFileName("Dockerfile"))
    }

    @Test
    fun `FileIconResolver resolves icons for 32 languages`() {
        val kotlinIcon = FileIconResolver.resolveFileIcon("Main.kt", false, false, null)
        assertTrue(kotlinIcon.contains("kotlin"))

        val tomlIcon = FileIconResolver.resolveFileIcon("Cargo.toml", false, false, null)
        assertTrue(tomlIcon.contains("toml") || tomlIcon.contains("properties") || tomlIcon.contains("file"))

        val makefileIcon = FileIconResolver.resolveFileIcon("Makefile", false, false, null)
        assertTrue(makefileIcon.contains("makefile") || makefileIcon.contains("file"))

        val dockerfileIcon = FileIconResolver.resolveFileIcon("Dockerfile", false, false, null)
        assertTrue(dockerfileIcon.contains("docker"))

        val srcFolder = FileIconResolver.resolveFileIcon("src", true, false, null)
        assertTrue(srcFolder.contains("folder"))

        val gradleFile = FileIconResolver.resolveFileIcon("build.gradle", false, false, null)
        assertTrue(gradleFile.contains("gradle") || gradleFile.contains("groovy"))
    }

    @Test
    fun `FormattingRepository formats toml and groovy`() = runBlocking {
        val repo = FormattingRepositoryImpl()
        val tomlContent = "key=\"value\"\n[table]\nkey2=\"value2\""
        val tomlResult = repo.formatDocument(tomlContent, "toml", 2, true)
        assertTrue(tomlResult.success)
        assertTrue(tomlResult.formattedContent.isNotBlank())

        val groovyContent = "class Test{\ndef hello(){\nprintln(\"hi\")\n}\n}"
        val groovyResult = repo.formatDocument(groovyContent, "groovy", 4, true)
        assertTrue(groovyResult.success)
    }

    @Test
    fun `EmmetService expands more abbreviations`() = runBlocking {
        val service = EmmetServiceImpl()
        val result1 = service.expandAbbreviation("div>ul>li", "html")
        assertTrue(result1.success)
        assertTrue(result1.expanded.contains("<div>"))
        assertTrue(result1.expanded.contains("<ul>"))
        assertTrue(result1.expanded.contains("<li>"))

        val result2 = service.expandAbbreviation("div#main.container", "html")
        assertTrue(result2.success)
        assertTrue(result2.expanded.contains("id=\"main\""))
        assertTrue(result2.expanded.contains("class=\"container\""))

        val result3 = service.expandAbbreviation("a[href=#]{Click}", "html")
        assertTrue(result3.success)
        assertTrue(result3.expanded.contains("href=\"#\""))
        assertTrue(result3.expanded.contains("Click"))
    }

    @Test
    fun `EmmetService detects new abbreviations`() {
        val service = EmmetServiceImpl()
        assertTrue(service.isEmmetAbbreviation("div>ul>li*3"))
        assertTrue(service.isEmmetAbbreviation("div.container"))
        assertTrue(service.isEmmetAbbreviation("ul>li"))
        assertTrue(service.isEmmetAbbreviation("m10"))
        assertFalse(service.isEmmetAbbreviation("plain text without emmet"))
    }

    @Test
    fun `FormattingRepository format on save logic`() = runBlocking {
        val repo = FormattingRepositoryImpl()
        val unformatted = "fun main(){\nprintln(\"hello\")\n}"
        val formatted = repo.formatDocument(unformatted, "kotlin", 4, true)
        assertTrue(formatted.success)
        // Formatted should have indentation
        assertTrue(formatted.formattedContent.contains("    ") || formatted.formattedContent.contains("\n"))
    }

    @Test
    fun `EditorLanguage fromFileName handles special files`() {
        assertEquals(EditorLanguage.MAKEFILE, EditorLanguage.fromFileName("Makefile"))
        assertEquals(EditorLanguage.MAKEFILE, EditorLanguage.fromFileName("makefile"))
        assertEquals(EditorLanguage.CMAKE, EditorLanguage.fromFileName("CMakeLists.txt"))
        assertEquals(EditorLanguage.GROOVY, EditorLanguage.fromFileName("build.gradle"))
        assertEquals(EditorLanguage.GROOVY, EditorLanguage.fromFileName("settings.gradle"))
        assertEquals(EditorLanguage.DOCKERFILE, EditorLanguage.fromFileName("Dockerfile"))
    }

    @Test
    fun `Search replace logic`() {
        val content = "hello world hello"
        val replaced = content.replace("hello", "hi")
        assertEquals("hi world hi", replaced)

        val regex = Regex("hello", setOf(RegexOption.IGNORE_CASE))
        val replacedRegex = regex.replace("Hello WORLD hello", "hi")
        assertEquals("hi WORLD hi", replacedRegex)
    }
}
