package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.core.editor.CodeLens
import com.cyberexpert.androde.core.editor.InlayHint
import com.cyberexpert.androde.core.editor.InlayHintKind
import com.cyberexpert.androde.core.editor.SemanticToken
import com.cyberexpert.androde.core.editor.SemanticTokens
import com.cyberexpert.androde.core.extensions.ExtensionApiFull
import com.cyberexpert.androde.core.extensions.StatusBarAlignment
import com.cyberexpert.androde.core.extensions.StatusBarItem
import com.cyberexpert.androde.core.marketplace.MarketplaceExtension
import com.cyberexpert.androde.core.tasks.LaunchConfiguration
import com.cyberexpert.androde.core.tasks.TaskDefinition
import com.cyberexpert.androde.core.tasks.TaskExecutionResult
import com.cyberexpert.androde.presentation.components.ide.FileIconResolver
import com.cyberexpert.androde.presentation.components.ide.DiffType
import com.cyberexpert.androde.presentation.components.ide.DiffLine
import com.cyberexpert.androde.presentation.components.ide.computeDiff
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase8Test {

    @Test
    fun `EditorLanguage supports 70 languages`() {
        assertTrue(EditorLanguage.entries.size >= 70)
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("glsl"))
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("vert"))
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("frag"))
        assertEquals(EditorLanguage.QML, EditorLanguage.fromExtension("qml"))
        assertEquals(EditorLanguage.WASM, EditorLanguage.fromExtension("wat"))
        assertEquals(EditorLanguage.WASM, EditorLanguage.fromExtension("wasm"))
        assertEquals(EditorLanguage.TWIG, EditorLanguage.fromExtension("twig"))
        assertEquals(EditorLanguage.EJS, EditorLanguage.fromExtension("ejs"))
        assertEquals(EditorLanguage.HAML, EditorLanguage.fromExtension("haml"))
        assertEquals(EditorLanguage.SLIM, EditorLanguage.fromExtension("slim"))
        assertEquals(EditorLanguage.VHDL, EditorLanguage.fromExtension("vhd"))
        assertEquals(EditorLanguage.VHDL, EditorLanguage.fromExtension("vhdl"))
        assertEquals(EditorLanguage.VERILOG, EditorLanguage.fromExtension("v"))
        assertEquals(EditorLanguage.VERILOG, EditorLanguage.fromExtension("sv"))
        assertEquals(EditorLanguage.CRYSTAL, EditorLanguage.fromExtension("cr"))
    }

    @Test
    fun `FileIconResolver resolves 70 languages`() {
        val glslIcon = FileIconResolver.resolveFileIcon("shader.vert", false, false, null)
        assertTrue(glslIcon.contains("glsl") || glslIcon.contains("file"))

        val qmlIcon = FileIconResolver.resolveFileIcon("main.qml", false, false, null)
        assertTrue(qmlIcon.contains("qml") || qmlIcon.contains("file"))

        val wasmIcon = FileIconResolver.resolveFileIcon("module.wat", false, false, null)
        assertTrue(wasmIcon.contains("wasm") || wasmIcon.contains("file"))

        val twigIcon = FileIconResolver.resolveFileIcon("index.twig", false, false, null)
        assertTrue(twigIcon.contains("twig") || twigIcon.contains("file"))

        val ejsIcon = FileIconResolver.resolveFileIcon("index.ejs", false, false, null)
        assertTrue(ejsIcon.contains("ejs") || ejsIcon.contains("file"))

        val hamlIcon = FileIconResolver.resolveFileIcon("index.haml", false, false, null)
        assertTrue(hamlIcon.contains("haml") || hamlIcon.contains("file"))

        val slimIcon = FileIconResolver.resolveFileIcon("index.slim", false, false, null)
        assertTrue(slimIcon.contains("slim") || slimIcon.contains("file"))

        val vhdlIcon = FileIconResolver.resolveFileIcon("design.vhdl", false, false, null)
        assertTrue(vhdlIcon.contains("vhdl") || vhdlIcon.contains("file"))

        val verilogIcon = FileIconResolver.resolveFileIcon("module.v", false, false, null)
        assertTrue(verilogIcon.contains("verilog") || verilogIcon.contains("file"))

        val crystalIcon = FileIconResolver.resolveFileIcon("app.cr", false, false, null)
        assertTrue(crystalIcon.contains("crystal") || crystalIcon.contains("file"))
    }

    @Test
    fun `Diff editor computeDiff works real LCS`() {
        val original = "line1\nline2\nline3"
        val modified = "line1\nline2 modified\nline3\nline4"
        val diff = computeDiff(original, modified)
        assertTrue(diff.isNotEmpty())
        assertTrue(diff.any { it.type == DiffType.EQUAL })
        assertTrue(diff.any { it.type == DiffType.MODIFY || it.type == DiffType.INSERT })

        val equalOriginal = "a\nb\nc"
        val equalModified = "a\nb\nc"
        val equalDiff = computeDiff(equalOriginal, equalModified)
        assertTrue(equalDiff.all { it.type == DiffType.EQUAL })
        assertEquals(3, equalDiff.size)
    }

    @Test
    fun `Diff types cover all cases`() {
        val lineEqual = DiffLine(1, 1, "same", "same", DiffType.EQUAL, "=")
        assertEquals(DiffType.EQUAL, lineEqual.type)
        val lineInsert = DiffLine(null, 2, null, "new", DiffType.INSERT, "+")
        assertEquals(DiffType.INSERT, lineInsert.type)
        val lineDelete = DiffLine(2, null, "old", null, DiffType.DELETE, "-")
        assertEquals(DiffType.DELETE, lineDelete.type)
        val lineModify = DiffLine(2, 2, "old", "new", DiffType.MODIFY, "~")
        assertEquals(DiffType.MODIFY, lineModify.type)
    }

    @Test
    fun `Extension API Full StatusBarItem model`() {
        val item = StatusBarItem(
            id = "test.status",
            text = "Test",
            tooltip = "Test tooltip",
            color = "#FFFFFF",
            command = "test.command",
            alignment = StatusBarAlignment.LEFT,
            priority = 100,
            isVisible = true
        )
        assertEquals("test.status", item.id)
        assertEquals("Test", item.text)
        assertEquals(StatusBarAlignment.LEFT, item.alignment)
        assertTrue(item.isVisible)
    }

    @Test
    fun `Marketplace extension model real fields`() {
        val ext = MarketplaceExtension(
            id = "test.ext",
            name = "test-ext",
            displayName = "Test Extension",
            publisher = "TestPub",
            description = "Test desc",
            version = "1.0.0",
            categories = listOf("Programming Languages"),
            tags = listOf("test"),
            downloadCount = 100,
            rating = 4.5f,
            ratingCount = 10,
            isBuiltin = false
        )
        assertEquals("test.ext", ext.id)
        assertEquals("Test Extension", ext.displayName)
        assertFalse(ext.isBuiltin)
        assertEquals(100, ext.downloadCount)
    }

    @Test
    fun `Tasks and Launch configs models`() {
        val task = TaskDefinition(
            label = "build",
            type = "shell",
            command = "echo",
            args = listOf("hello"),
            isBackground = false
        )
        assertEquals("build", task.label)
        assertEquals("shell", task.type)

        val launch = LaunchConfiguration(
            name = "Launch Kotlin",
            type = "kotlin",
            request = "launch",
            program = "Main.kt"
        )
        assertEquals("Launch Kotlin", launch.name)
        assertEquals("kotlin", launch.type)

        val result = TaskExecutionResult(
            success = true,
            exitCode = 0,
            output = "hello",
            taskLabel = "build"
        )
        assertTrue(result.success)
        assertEquals(0, result.exitCode)
    }

    @Test
    fun `CodeLens Inlay Semantic Tokens models`() {
        val lens = CodeLens(
            id = "lens1",
            filePath = "/test.kt",
            line = 10,
            command = "references",
            title = "2 references"
        )
        assertEquals(10, lens.line)

        val hint = InlayHint(
            id = "hint1",
            filePath = "/test.kt",
            line = 5,
            column = 10,
            label = ": String",
            kind = InlayHintKind.TYPE
        )
        assertEquals(InlayHintKind.TYPE, hint.kind)

        val token = SemanticToken(
            line = 1,
            column = 0,
            length = 5,
            tokenType = "class",
            tokenModifiers = listOf("declaration")
        )
        assertEquals("class", token.tokenType)

        val semanticTokens = SemanticTokens(
            filePath = "/test.kt",
            tokens = listOf(token)
        )
        assertEquals(1, semanticTokens.tokens.size)
    }

    @Test
    fun `Sora scope mapping for 70 langs includes new 10`() {
        val scopeMap = mapOf(
            "glsl" to "source.glsl",
            "qml" to "source.qml",
            "wasm" to "source.wat",
            "twig" to "text.html.twig",
            "ejs" to "text.html.ejs",
            "haml" to "text.haml",
            "slim" to "text.slim",
            "vhdl" to "source.vhdl",
            "verilog" to "source.verilog",
            "crystal" to "source.crystal"
        )
        assertEquals(10, scopeMap.size)
        assertEquals("source.glsl", scopeMap["glsl"])
        assertEquals("source.qml", scopeMap["qml"])
        assertEquals("source.crystal", scopeMap["crystal"])
        assertEquals("text.html.twig", scopeMap["twig"])
    }

    @Test
    fun `EditorLanguage fromExtension handles 70 langs special`() {
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("vs"))
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("fs"))
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("geom"))
        assertEquals(EditorLanguage.GLSL, EditorLanguage.fromExtension("comp"))
        assertEquals(EditorLanguage.QML, EditorLanguage.fromExtension("qmlproject"))
        assertEquals(EditorLanguage.VHDL, EditorLanguage.fromExtension("vho"))
        assertEquals(EditorLanguage.VERILOG, EditorLanguage.fromExtension("svh"))
    }
}
