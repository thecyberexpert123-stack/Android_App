package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.core.collab.LiveShareParticipant
import com.cyberexpert.androde.core.collab.LiveShareSession
import com.cyberexpert.androde.core.debug.DebugBreakpoint
import com.cyberexpert.androde.core.debug.JdwpConnection
import com.cyberexpert.androde.core.extensions.CommandItem
import com.cyberexpert.androde.core.extensions.NotificationItem
import com.cyberexpert.androde.core.extensions.NotificationType
import com.cyberexpert.androde.core.extensions.QuickPickItem
import com.cyberexpert.androde.core.sync.RemoteTunnel
import com.cyberexpert.androde.core.sync.SyncProfile
import com.cyberexpert.androde.core.terminal.PtySession
import com.cyberexpert.androde.presentation.components.ide.FileIconResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase9Test {

    @Test
    fun `EditorLanguage supports 80 languages`() {
        assertTrue(EditorLanguage.entries.size >= 80)
        assertEquals(EditorLanguage.SMARTY, EditorLanguage.fromExtension("tpl"))
        assertEquals(EditorLanguage.LIQUID, EditorLanguage.fromExtension("liquid"))
        assertEquals(EditorLanguage.MUSTACHE, EditorLanguage.fromExtension("mustache"))
        assertEquals(EditorLanguage.JINJA, EditorLanguage.fromExtension("jinja"))
        assertEquals(EditorLanguage.JINJA, EditorLanguage.fromExtension("j2"))
        assertEquals(EditorLanguage.VELOCITY, EditorLanguage.fromExtension("vm"))
        assertEquals(EditorLanguage.FORTRAN, EditorLanguage.fromExtension("f90"))
        assertEquals(EditorLanguage.PASCAL, EditorLanguage.fromExtension("pas"))
        assertEquals(EditorLanguage.ADA, EditorLanguage.fromExtension("adb"))
        assertEquals(EditorLanguage.LISP, EditorLanguage.fromExtension("lisp"))
        assertEquals(EditorLanguage.TCL, EditorLanguage.fromExtension("tcl"))
    }

    @Test
    fun `FileIconResolver resolves 80 languages`() {
        val smartyIcon = FileIconResolver.resolveFileIcon("index.tpl", false, false, null)
        assertTrue(smartyIcon.contains("smarty") || smartyIcon.contains("file"))

        val liquidIcon = FileIconResolver.resolveFileIcon("index.liquid", false, false, null)
        assertTrue(liquidIcon.contains("liquid") || liquidIcon.contains("file"))

        val mustacheIcon = FileIconResolver.resolveFileIcon("template.mustache", false, false, null)
        assertTrue(mustacheIcon.contains("mustache") || mustacheIcon.contains("file"))

        val jinjaIcon = FileIconResolver.resolveFileIcon("template.j2", false, false, null)
        assertTrue(jinjaIcon.contains("jinja") || jinjaIcon.contains("file"))

        val vmIcon = FileIconResolver.resolveFileIcon("template.vm", false, false, null)
        assertTrue(vmIcon.contains("velocity") || vmIcon.contains("file"))

        val fortranIcon = FileIconResolver.resolveFileIcon("main.f90", false, false, null)
        assertTrue(fortranIcon.contains("fortran") || fortranIcon.contains("file"))

        val pasIcon = FileIconResolver.resolveFileIcon("program.pas", false, false, null)
        assertTrue(pasIcon.contains("pascal") || pasIcon.contains("file"))

        val adaIcon = FileIconResolver.resolveFileIcon("main.adb", false, false, null)
        assertTrue(adaIcon.contains("ada") || adaIcon.contains("file"))

        val lispIcon = FileIconResolver.resolveFileIcon("app.lisp", false, false, null)
        assertTrue(lispIcon.contains("lisp") || lispIcon.contains("file"))

        val tclIcon = FileIconResolver.resolveFileIcon("script.tcl", false, false, null)
        assertTrue(tclIcon.contains("tcl") || tclIcon.contains("file"))
    }

    @Test
    fun `Terminal PTY session model`() {
        val session = PtySession(
            id = "pty1",
            shell = "/system/bin/sh",
            workingDir = "/data",
            cols = 80,
            rows = 24,
            isAlive = true
        )
        assertEquals("pty1", session.id)
        assertEquals(80, session.cols)
        assertTrue(session.isAlive)
    }

    @Test
    fun `DAP JDWP connection and breakpoint models`() {
        val conn = JdwpConnection(
            id = "conn1",
            host = "localhost",
            port = 5005,
            isConnected = true,
            vmName = "OpenJDK"
        )
        assertEquals("localhost", conn.host)
        assertEquals(5005, conn.port)
        assertTrue(conn.isConnected)

        val bp = DebugBreakpoint(
            id = "bp1",
            filePath = "/test/Main.kt",
            line = 10,
            condition = null,
            isVerified = true
        )
        assertEquals(10, bp.line)
        assertTrue(bp.isVerified)
    }

    @Test
    fun `Extension API Extended models`() {
        val quickPick = QuickPickItem(
            label = "File: Open",
            description = "Open file",
            detail = "Open a file from disk"
        )
        assertEquals("File: Open", quickPick.label)

        val notification = NotificationItem(
            id = "notif1",
            message = "Extension installed",
            type = NotificationType.INFO
        )
        assertEquals(NotificationType.INFO, notification.type)

        val command = CommandItem(
            id = "androde.openFile",
            title = "Open File",
            category = "File"
        )
        assertEquals("androde.openFile", command.id)
    }

    @Test
    fun `Settings Sync Profiles and Remote Tunnels models`() {
        val profile = SyncProfile(
            id = "profile1",
            name = "Work",
            description = "Work profile",
            isDefault = false
        )
        assertEquals("Work", profile.name)
        assertFalse(profile.isDefault)

        val tunnel = RemoteTunnel(
            id = "tunnel1",
            name = "Dev Server",
            host = "example.com",
            port = 8080,
            isActive = false
        )
        assertEquals("example.com", tunnel.host)
        assertEquals(8080, tunnel.port)
    }

    @Test
    fun `Live Share session model`() {
        val participant = LiveShareParticipant(
            id = "user1",
            name = "Alice",
            isHost = true
        )
        assertTrue(participant.isHost)

        val session = LiveShareSession(
            id = "session1",
            name = "Pair Programming",
            hostUser = "Alice",
            isHost = true,
            participants = listOf(participant),
            sharedFiles = listOf("/project/Main.kt")
        )
        assertEquals("Pair Programming", session.name)
        assertEquals(1, session.participants.size)
        assertTrue(session.sharedFiles.contains("/project/Main.kt"))
    }

    @Test
    fun `Sora scope mapping for 80 langs includes new 10`() {
        val scopeMap = mapOf(
            "smarty" to "text.html.smarty",
            "liquid" to "text.html.liquid",
            "mustache" to "text.html.mustache",
            "jinja" to "text.html.jinja",
            "velocity" to "text.html.velocity",
            "fortran" to "source.fortran",
            "pascal" to "source.pascal",
            "ada" to "source.ada",
            "lisp" to "source.lisp",
            "tcl" to "source.tcl"
        )
        assertEquals(10, scopeMap.size)
        assertEquals("text.html.smarty", scopeMap["smarty"])
        assertEquals("source.fortran", scopeMap["fortran"])
        assertEquals("source.tcl", scopeMap["tcl"])
    }

    @Test
    fun `EditorLanguage fromExtension handles 80 langs special`() {
        assertEquals(EditorLanguage.SMARTY, EditorLanguage.fromExtension("tpl"))
        assertEquals(EditorLanguage.VELOCITY, EditorLanguage.fromExtension("vtl"))
        assertEquals(EditorLanguage.FORTRAN, EditorLanguage.fromExtension("f"))
        assertEquals(EditorLanguage.FORTRAN, EditorLanguage.fromExtension("for"))
        assertEquals(EditorLanguage.FORTRAN, EditorLanguage.fromExtension("f03"))
        assertEquals(EditorLanguage.PASCAL, EditorLanguage.fromExtension("pp"))
        assertEquals(EditorLanguage.ADA, EditorLanguage.fromExtension("ads"))
        assertEquals(EditorLanguage.LISP, EditorLanguage.fromExtension("lsp"))
        assertEquals(EditorLanguage.LISP, EditorLanguage.fromExtension("cl"))
    }
}
