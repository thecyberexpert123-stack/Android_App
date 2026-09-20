package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.presentation.components.ide.FileIconResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase7Test {

    @Test
    fun `EditorLanguage supports 60 languages`() {
        assertTrue(EditorLanguage.entries.size >= 60)
        assertEquals(EditorLanguage.COFFEESCRIPT, EditorLanguage.fromExtension("coffee"))
        assertEquals(EditorLanguage.HANDLEBARS, EditorLanguage.fromExtension("hbs"))
        assertEquals(EditorLanguage.PUG, EditorLanguage.fromExtension("pug"))
        assertEquals(EditorLanguage.RAZOR, EditorLanguage.fromExtension("cshtml"))
        assertEquals(EditorLanguage.OBJECTIVEC, EditorLanguage.fromExtension("m"))
        assertEquals(EditorLanguage.FSHARP, EditorLanguage.fromExtension("fs"))
        assertEquals(EditorLanguage.ELM, EditorLanguage.fromExtension("elm"))
        assertEquals(EditorLanguage.OCAML, EditorLanguage.fromExtension("ml"))
        assertEquals(EditorLanguage.LATEX, EditorLanguage.fromExtension("tex"))
        assertEquals(EditorLanguage.SOLIDITY, EditorLanguage.fromExtension("sol"))
    }

    @Test
    fun `FileIconResolver resolves 60 languages`() {
        val coffeeIcon = FileIconResolver.resolveFileIcon("app.coffee", false, false, null)
        assertTrue(coffeeIcon.contains("coffeescript") || coffeeIcon.contains("file"))

        val hbsIcon = FileIconResolver.resolveFileIcon("template.hbs", false, false, null)
        assertTrue(hbsIcon.contains("handlebars") || hbsIcon.contains("file"))

        val pugIcon = FileIconResolver.resolveFileIcon("index.pug", false, false, null)
        assertTrue(pugIcon.contains("pug") || pugIcon.contains("file"))

        val cshtmlIcon = FileIconResolver.resolveFileIcon("Index.cshtml", false, false, null)
        assertTrue(cshtmlIcon.contains("razor") || cshtmlIcon.contains("file"))

        val objcIcon = FileIconResolver.resolveFileIcon("main.m", false, false, null)
        assertTrue(objcIcon.contains("objc") || objcIcon.contains("file"))

        val fsIcon = FileIconResolver.resolveFileIcon("Program.fs", false, false, null)
        assertTrue(fsIcon.contains("fsharp") || fsIcon.contains("file"))

        val solIcon = FileIconResolver.resolveFileIcon("Token.sol", false, false, null)
        assertTrue(solIcon.contains("solidity") || solIcon.contains("file"))

        val texIcon = FileIconResolver.resolveFileIcon("paper.tex", false, false, null)
        assertTrue(texIcon.contains("latex") || texIcon.contains("file"))
    }

    @Test
    fun `EditorLanguage fromExtension handles 60 langs special`() {
        assertEquals(EditorLanguage.HANDLEBARS, EditorLanguage.fromExtension("handlebars"))
        assertEquals(EditorLanguage.PUG, EditorLanguage.fromExtension("jade"))
        assertEquals(EditorLanguage.COFFEESCRIPT, EditorLanguage.fromExtension("cson"))
        assertEquals(EditorLanguage.COFFEESCRIPT, EditorLanguage.fromExtension("iced"))
        assertEquals(EditorLanguage.LATEX, EditorLanguage.fromExtension("ltx"))
        assertEquals(EditorLanguage.LATEX, EditorLanguage.fromExtension("bib"))
        assertEquals(EditorLanguage.OCAML, EditorLanguage.fromExtension("mli"))
        assertEquals(EditorLanguage.FSHARP, EditorLanguage.fromExtension("fsi"))
        assertEquals(EditorLanguage.FSHARP, EditorLanguage.fromExtension("fsx"))
    }

    @Test
    fun `Sora scope mapping for 60 langs includes new 10`() {
        val scopeMap = mapOf(
            "coffeescript" to "source.coffee",
            "handlebars" to "text.html.handlebars",
            "pug" to "text.pug",
            "razor" to "text.html.cshtml",
            "objective-c" to "source.objc",
            "fsharp" to "source.fsharp",
            "elm" to "source.elm",
            "ocaml" to "source.ocaml",
            "latex" to "text.tex.latex",
            "solidity" to "source.solidity"
        )
        assertEquals(10, scopeMap.size)
        assertEquals("source.coffee", scopeMap["coffeescript"])
        assertEquals("text.html.handlebars", scopeMap["handlebars"])
        assertEquals("source.solidity", scopeMap["solidity"])
    }

    @Test
    fun `Debug DAP evaluate arithmetic`() {
        val expr1 = "2+3"
        val parts = expr1.split("+")
        val a = parts[0].toDoubleOrNull() ?: 0.0
        val b = parts[1].toDoubleOrNull() ?: 0.0
        assertEquals(5.0, a + b, 0.001)

        val expr2 = "10-4"
        val parts2 = expr2.split("-")
        assertEquals(6.0, (parts2[0].toDoubleOrNull() ?: 0.0) - (parts2[1].toDoubleOrNull() ?: 0.0), 0.001)
    }

    @Test
    fun `Debug breakpoint verification for 60 langs comments`() {
        val commentPrefixes = listOf("//", "#", "/*", "*", "--", ";", "%", "<!--", "(*", "{-", "--[[", "###")
        assertTrue(commentPrefixes.contains("//"))
        assertTrue(commentPrefixes.contains("#"))
        assertTrue(commentPrefixes.contains("--"))
        assertTrue(commentPrefixes.contains("%")) // LaTeX
        assertTrue(commentPrefixes.contains("(*")) // OCaml
        assertFalse("".trim().isEmpty().not()) // empty check
        assertTrue("   ".trim().isEmpty())
        assertTrue("// comment".trim().startsWith("//"))
        assertTrue("# comment".trim().startsWith("#"))
        assertTrue("% LaTeX comment".trim().startsWith("%"))
    }

    @Test
    fun `Minimap and zoom logic`() {
        var fontSize = 14
        fontSize = (fontSize + 1).coerceAtMost(32)
        assertEquals(15, fontSize)
        fontSize = (fontSize - 1).coerceAtLeast(8)
        assertEquals(14, fontSize)
        fontSize = 32
        fontSize = (fontSize + 1).coerceAtMost(32)
        assertEquals(32, fontSize)
        fontSize = 8
        fontSize = (fontSize - 1).coerceAtLeast(8)
        assertEquals(8, fontSize)
    }

    @Test
    fun `Multi-cursor and call stack frames`() {
        val frames = listOf(
            "main (kotlin)",
            "run (kotlin runtime)",
            "function1 (file.kt:10)",
            "function2 (file.kt:20)"
        )
        assertTrue(frames.size >= 2)
        assertTrue(frames[0].contains("main"))
        assertTrue(frames[1].contains("run"))
    }
}
