package com.cyberexpert.androde.domain.model.ide

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ExtensionTest {

    @Test
    fun `EditorLanguage has 20+ languages`() {
        assertTrue(EditorLanguage.entries.size >= 20)
    }

    @Test
    fun `EditorLanguage fromExtension handles all common`() {
        assertEquals(EditorLanguage.JAVA, EditorLanguage.fromExtension("java"))
        assertEquals(EditorLanguage.JAVASCRIPT, EditorLanguage.fromExtension("js"))
        assertEquals(EditorLanguage.PYTHON, EditorLanguage.fromExtension("py"))
        assertEquals(EditorLanguage.HTML, EditorLanguage.fromExtension("html"))
        assertEquals(EditorLanguage.CSS, EditorLanguage.fromExtension("css"))
        assertEquals(EditorLanguage.JSON, EditorLanguage.fromExtension("json"))
        assertEquals(EditorLanguage.MARKDOWN, EditorLanguage.fromExtension("md"))
        assertEquals(EditorLanguage.YAML, EditorLanguage.fromExtension("yaml"))
        assertEquals(EditorLanguage.SHELL, EditorLanguage.fromExtension("sh"))
    }

    @Test
    fun `EditorLanguage fromFileName works`() {
        assertEquals(EditorLanguage.KOTLIN, EditorLanguage.fromFileName("Main.kt"))
        assertEquals(EditorLanguage.PLAINTEXT, EditorLanguage.fromFileName("README"))
    }

    @Test
    fun `Project displayName fallback`() {
        val file = File("/tmp/myProject")
        val project = Project.fromFile(file)
        assertEquals("myProject", project.displayName)
    }

    @Test
    fun `FileNode isFile logic`() {
        val file = File("/tmp/test.txt")
        val node = FileNode(file, isDirectory = false)
        assertTrue(node.isFile)
        val dir = File("/tmp/dir")
        val dirNode = FileNode(dir, isDirectory = true)
        assertTrue(!dirNode.isFile)
    }
}
