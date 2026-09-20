package com.cyberexpert.androde.domain.model.ide

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FileNodeTest {

    @Test
    fun `FileNode fromFile creates correct node for file`() {
        val file = File("/tmp/test.kt")
        val node = FileNode.fromFile(file, depth = 0, showHidden = false)
        assertEquals("test.kt", node?.name)
        assertEquals("kt", node?.extension)
        assertFalse(node?.isDirectory ?: true)
    }

    @Test
    fun `FileNode fromFile hides hidden files when showHidden false`() {
        val hiddenFile = File("/tmp/.hidden")
        val node = FileNode.fromFile(hiddenFile, depth = 0, showHidden = false)
        assertEquals(null, node)
    }

    @Test
    fun `FileNode fromFile shows hidden when showHidden true`() {
        val hiddenFile = File("/tmp/.hidden")
        val node = FileNode.fromFile(hiddenFile, depth = 0, showHidden = true)
        assertEquals(".hidden", node?.name)
    }

    @Test
    fun `EditorLanguage fromExtension detects kotlin`() {
        val lang = EditorLanguage.fromExtension("kt")
        assertEquals(EditorLanguage.KOTLIN, lang)
    }

    @Test
    fun `EditorLanguage fromExtension defaults to plaintext`() {
        val lang = EditorLanguage.fromExtension("unknown123")
        assertEquals(EditorLanguage.PLAINTEXT, lang)
    }

    @Test
    fun `EditorTab withContent tracks dirty`() {
        val file = File("/tmp/test.kt")
        val tab = EditorTab(
            id = "1",
            file = file,
            content = "original",
            originalContent = "original"
        )
        assertFalse(tab.isDirty)
        val dirtyTab = tab.withContent("modified")
        assertTrue(dirtyTab.isDirty)
        assertEquals("modified", dirtyTab.content)
    }

    @Test
    fun `Project fromFile detects git repo`() {
        val tempDir = createTempDir()
        val gitDir = File(tempDir, ".git")
        gitDir.mkdirs()
        val project = Project.fromFile(tempDir)
        assertTrue(project.isGitRepository)
        tempDir.deleteRecursively()
    }

    @Test
    fun `BuiltinCommands has 20+ commands`() {
        assertTrue(BuiltinCommands.all.size >= 20)
        assertTrue(BuiltinCommands.all.any { it.id == "workbench.action.showCommands" })
    }
}
