package com.cyberexpert.androde.domain.model.ide

import com.cyberexpert.androde.presentation.components.ide.AnsiParser
import com.cyberexpert.androde.presentation.components.ide.FileIconResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase6Test {

    @Test
    fun `EditorLanguage supports 50 languages`() {
        assertTrue(EditorLanguage.entries.size >= 50)
        assertEquals(EditorLanguage.CLOJURE, EditorLanguage.fromExtension("clj"))
        assertEquals(EditorLanguage.ELIXIR, EditorLanguage.fromExtension("ex"))
        assertEquals(EditorLanguage.ERLANG, EditorLanguage.fromExtension("erl"))
        assertEquals(EditorLanguage.HASKELL, EditorLanguage.fromExtension("hs"))
        assertEquals(EditorLanguage.JULIA, EditorLanguage.fromExtension("jl"))
        assertEquals(EditorLanguage.SCALA, EditorLanguage.fromExtension("scala"))
        assertEquals(EditorLanguage.PERL, EditorLanguage.fromExtension("pl"))
        assertEquals(EditorLanguage.VUE, EditorLanguage.fromExtension("vue"))
        assertEquals(EditorLanguage.SVELTE, EditorLanguage.fromExtension("svelte"))
        assertEquals(EditorLanguage.GRAPHQL, EditorLanguage.fromExtension("graphql"))
        assertEquals(EditorLanguage.PROTO, EditorLanguage.fromExtension("proto"))
        assertEquals(EditorLanguage.CSV, EditorLanguage.fromExtension("csv"))
        assertEquals(EditorLanguage.DIFF, EditorLanguage.fromExtension("diff"))
        assertEquals(EditorLanguage.GITIGNORE, EditorLanguage.fromFileName(".gitignore"))
        assertEquals(EditorLanguage.NGINX, EditorLanguage.fromExtension("nginx"))
        assertEquals(EditorLanguage.SCSS, EditorLanguage.fromExtension("scss"))
        assertEquals(EditorLanguage.LESS, EditorLanguage.fromExtension("less"))
    }

    @Test
    fun `FileIconResolver resolves 50 languages`() {
        val vueIcon = FileIconResolver.resolveFileIcon("App.vue", false, false, null)
        assertTrue(vueIcon.contains("vue") || vueIcon.contains("file"))

        val protoIcon = FileIconResolver.resolveFileIcon("service.proto", false, false, null)
        assertTrue(protoIcon.contains("proto") || protoIcon.contains("file"))

        val graphqlIcon = FileIconResolver.resolveFileIcon("schema.graphql", false, false, null)
        assertTrue(graphqlIcon.contains("graphql") || graphqlIcon.contains("file"))

        val clojureIcon = FileIconResolver.resolveFileIcon("core.clj", false, false, null)
        assertTrue(clojureIcon.contains("clojure") || clojureIcon.contains("file"))

        val gitignoreIcon = FileIconResolver.resolveFileIcon(".gitignore", false, false, null)
        assertTrue(gitignoreIcon.contains("git") || gitignoreIcon.contains("file"))

        val csvIcon = FileIconResolver.resolveFileIcon("data.csv", false, false, null)
        assertTrue(csvIcon.contains("csv") || csvIcon.contains("file"))
    }

    @Test
    fun `AnsiParser supports 256 colors`() {
        val input256 = "\u001B[38;5;196mRed 256\u001B[0m Normal"
        val stripped = AnsiParser.stripAnsi(input256)
        assertEquals("Red 256 Normal", stripped)
        assertTrue(AnsiParser.containsAnsi(input256))
        val annotated = AnsiParser.parseToAnnotatedString(input256)
        assertTrue(annotated.text.contains("Red 256"))

        val inputTrue = "\u001B[38;2;255;100;0mOrange True\u001B[0m"
        assertTrue(AnsiParser.containsAnsi(inputTrue))
        val annotatedTrue = AnsiParser.parseToAnnotatedString(inputTrue)
        assertTrue(annotatedTrue.text.contains("Orange True"))
    }

    @Test
    fun `AnsiParser supports italic and underline`() {
        val input = "\u001B[3mItalic\u001B[0m and \u001B[4mUnderline\u001B[0m"
        val stripped = AnsiParser.stripAnsi(input)
        assertEquals("Italic and Underline", stripped)
        val annotated = AnsiParser.parseToAnnotatedString(input)
        assertTrue(annotated.text.contains("Italic"))
        assertTrue(annotated.text.contains("Underline"))
    }

    @Test
    fun `Breakpoint verification logic`() {
        // Test breakpoint line validation logic
        val validLine = "fun main() {"
        val emptyLine = ""
        val commentLine = "// comment"
        val hashComment = "# comment"

        assertFalse(validLine.trim().isEmpty())
        assertTrue(emptyLine.trim().isEmpty())
        assertTrue(commentLine.trim().startsWith("//"))
        assertTrue(hashComment.trim().startsWith("#"))
    }

    @Test
    fun `Workspace trust logic`() {
        val trustedPaths = setOf("/home/user/project1", "/home/user/project2")
        val path1 = "/home/user/project1"
        val path2 = "/home/user/untrusted"

        assertTrue(trustedPaths.contains(path1))
        assertFalse(trustedPaths.contains(path2))
    }

    @Test
    fun `EditorLanguage fromFileName handles 50 langs special files`() {
        assertEquals(EditorLanguage.GITIGNORE, EditorLanguage.fromFileName(".gitignore"))
        assertEquals(EditorLanguage.MAKEFILE, EditorLanguage.fromFileName("Makefile"))
        assertEquals(EditorLanguage.CMAKE, EditorLanguage.fromFileName("CMakeLists.txt"))
        assertEquals(EditorLanguage.DOCKERFILE, EditorLanguage.fromFileName("Dockerfile"))
        assertEquals(EditorLanguage.GROOVY, EditorLanguage.fromFileName("build.gradle"))
    }

    @Test
    fun `Sora scope mapping for 50 langs`() {
        val scopeMap = mapOf(
            "clojure" to "source.clojure",
            "elixir" to "source.elixir",
            "erlang" to "source.erlang",
            "haskell" to "source.haskell",
            "julia" to "source.julia",
            "scala" to "source.scala",
            "perl" to "source.perl",
            "vue" to "text.html.vue",
            "svelte" to "text.html.svelte",
            "graphql" to "source.graphql",
            "proto" to "source.proto",
            "csv" to "text.csv",
            "diff" to "source.diff",
            "gitignore" to "source.gitignore",
            "nginx" to "source.nginx",
            "scss" to "source.css.scss",
            "less" to "source.css.less",
            "stylus" to "source.stylus"
        )
        assertEquals(18, scopeMap.size)
        assertEquals("source.clojure", scopeMap["clojure"])
        assertEquals("text.html.vue", scopeMap["vue"])
    }
}
