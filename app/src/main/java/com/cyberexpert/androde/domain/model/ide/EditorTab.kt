package com.cyberexpert.androde.domain.model.ide

import java.io.File

/**
 * Represents an open editor tab, similar to VS Code editor groups.
 */
data class EditorTab(
    val id: String,
    val file: File,
    val fileName: String = file.name,
    val filePath: String = file.absolutePath,
    val content: String = "",
    val originalContent: String = "",
    val language: EditorLanguage = EditorLanguage.fromExtension(file.extension),
    val isDirty: Boolean = false,
    val isPinned: Boolean = false,
    val cursorLine: Int = 0,
    val cursorColumn: Int = 0,
    val scrollOffset: Int = 0,
    val encoding: String = "UTF-8",
    val eol: String = "LF"
) {
    val isUntitled: Boolean get() = filePath.startsWith("untitled:")

    fun withContent(newContent: String): EditorTab {
        return copy(
            content = newContent,
            isDirty = newContent != originalContent
        )
    }

    fun asSaved(): EditorTab {
        return copy(
            originalContent = content,
            isDirty = false
        )
    }
}

/**
 * Language mode, similar to VS Code language detection - Phase 12 with 110 languages.
 * Uses file extension to determine language for Sora Editor with 110 TextMate grammars.
 */
enum class EditorLanguage(
    val id: String,
    val displayName: String,
    val extensions: List<String>
) {
    PLAINTEXT("plaintext", "Plain Text", listOf("txt")),
    KOTLIN("kotlin", "Kotlin", listOf("kt", "kts")),
    JAVA("java", "Java", listOf("java")),
    JAVASCRIPT("javascript", "JavaScript", listOf("js", "jsx", "mjs", "cjs")),
    TYPESCRIPT("typescript", "TypeScript", listOf("ts", "tsx")),
    PYTHON("python", "Python", listOf("py", "pyw")),
    C("c", "C", listOf("c", "h")),
    CPP("cpp", "C++", listOf("cpp", "cc", "cxx", "hpp", "hh")),
    CSHARP("csharp", "C#", listOf("cs")),
    GO("go", "Go", listOf("go")),
    RUST("rust", "Rust", listOf("rs")),
    HTML("html", "HTML", listOf("html", "htm")),
    CSS("css", "CSS", listOf("css")),
    SCSS("scss", "SCSS", listOf("scss")),
    LESS("less", "Less", listOf("less")),
    STYLUS("stylus", "Stylus", listOf("styl", "stylus")),
    JSON("json", "JSON", listOf("json")),
    XML("xml", "XML", listOf("xml", "xsl", "xsd", "svg", "plist")),
    YAML("yaml", "YAML", listOf("yaml", "yml")),
    MARKDOWN("markdown", "Markdown", listOf("md", "markdown")),
    SHELL("shellscript", "Shell Script", listOf("sh", "bash", "zsh")),
    DART("dart", "Dart", listOf("dart")),
    SWIFT("swift", "Swift", listOf("swift")),
    PHP("php", "PHP", listOf("php", "phtml")),
    RUBY("ruby", "Ruby", listOf("rb", "rake", "gemspec")),
    SQL("sql", "SQL", listOf("sql")),
    PROPERTIES("properties", "Properties", listOf("properties", "conf", "cfg")),
    DOCKERFILE("dockerfile", "Dockerfile", listOf("dockerfile")),
    INI("ini", "INI", listOf("ini")),
    TOML("toml", "TOML", listOf("toml")),
    GROOVY("groovy", "Groovy", listOf("groovy", "gradle")),
    LUA("lua", "Lua", listOf("lua")),
    R("r", "R", listOf("r")),
    BAT("bat", "Batch", listOf("bat", "cmd")),
    POWERSHELL("powershell", "PowerShell", listOf("ps1", "psm1", "psd1")),
    MAKEFILE("makefile", "Makefile", listOf("makefile", "mk")),
    CMAKE("cmake", "CMake", listOf("cmake")),
    CLOJURE("clojure", "Clojure", listOf("clj", "cljs", "cljc", "edn")),
    ELIXIR("elixir", "Elixir", listOf("ex", "exs")),
    ERLANG("erlang", "Erlang", listOf("erl", "hrl")),
    HASKELL("haskell", "Haskell", listOf("hs", "lhs")),
    JULIA("julia", "Julia", listOf("jl")),
    SCALA("scala", "Scala", listOf("scala", "sc")),
    PERL("perl", "Perl", listOf("pl", "pm", "t")),
    VUE("vue", "Vue", listOf("vue")),
    SVELTE("svelte", "Svelte", listOf("svelte")),
    GRAPHQL("graphql", "GraphQL", listOf("graphql", "gql")),
    PROTO("proto", "Protocol Buffer", listOf("proto")),
    CSV("csv", "CSV", listOf("csv")),
    DIFF("diff", "Diff", listOf("diff", "patch")),
    GITIGNORE("gitignore", "Git Ignore", listOf("gitignore")),
    NGINX("nginx", "Nginx", listOf("nginx")),
    COFFEESCRIPT("coffeescript", "CoffeeScript", listOf("coffee", "cson", "iced")),
    HANDLEBARS("handlebars", "Handlebars", listOf("hbs", "handlebars")),
    PUG("pug", "Pug", listOf("pug", "jade")),
    RAZOR("razor", "Razor", listOf("cshtml", "razor")),
    OBJECTIVEC("objective-c", "Objective-C", listOf("m")),
    OBJECTIVECPP("objective-cpp", "Objective-C++", listOf("mm")),
    FSHARP("fsharp", "F#", listOf("fs", "fsi", "fsx")),
    ELM("elm", "Elm", listOf("elm")),
    OCAML("ocaml", "OCaml", listOf("ml", "mli")),
    LATEX("latex", "LaTeX", listOf("tex", "ltx", "sty", "cls")),
    SOLIDITY("solidity", "Solidity", listOf("sol")),
    GLSL("glsl", "GLSL", listOf("glsl", "vert", "frag", "geom", "comp", "vs", "fs")),
    QML("qml", "QML", listOf("qml", "qmlproject")),
    WASM("wasm", "WebAssembly", listOf("wat", "wasm")),
    TWIG("twig", "Twig", listOf("twig")),
    EJS("ejs", "EJS", listOf("ejs")),
    HAML("haml", "Haml", listOf("haml")),
    SLIM("slim", "Slim", listOf("slim")),
    VHDL("vhdl", "VHDL", listOf("vhd", "vhdl", "vho")),
    VERILOG("verilog", "Verilog", listOf("v", "sv", "svh")),
    CRYSTAL("crystal", "Crystal", listOf("cr")),
    SMARTY("smarty", "Smarty", listOf("tpl")),
    LIQUID("liquid", "Liquid", listOf("liquid")),
    MUSTACHE("mustache", "Mustache", listOf("mustache")),
    JINJA("jinja", "Jinja", listOf("jinja", "j2")),
    VELOCITY("velocity", "Velocity", listOf("vm", "vtl")),
    FORTRAN("fortran", "Fortran", listOf("f", "f90", "f95", "for", "f03", "f08")),
    PASCAL("pascal", "Pascal", listOf("pas", "p")),
    ADA("ada", "Ada", listOf("adb", "ads")),
    LISP("lisp", "Lisp", listOf("lisp", "lsp", "cl")),
    TCL("tcl", "Tcl", listOf("tcl")),
    ASM("asm", "Assembly", listOf("asm", "s", "nasm", "inc")),
    HACK("hack", "Hack", listOf("hack", "hh", "hhi")),
    APEX("apex", "Apex", listOf("apex", "cls", "trigger", "apxc", "apxt")),
    ABAP("abap", "ABAP", listOf("abap")),
    ACTIONSCRIPT("actionscript", "ActionScript", listOf("as")),
    PUPPET("puppet", "Puppet", listOf("pp", "puppet", "epp")),
    SMALLTALK("smalltalk", "Smalltalk", listOf("st")),
    RACKET("racket", "Racket", listOf("rkt", "rktl", "rktd")),
    SCHEME("scheme", "Scheme", listOf("scm", "ss", "sch", "sld")),
    NIM("nim", "Nim", listOf("nim", "nims", "nimble")),
    MATLAB("matlab", "MATLAB", listOf("matlab")),
    VB("vb", "Visual Basic", listOf("vb", "bas", "vbs")),
    XAML("xaml", "XAML", listOf("xaml")),
    RST("restructuredtext", "reStructuredText", listOf("rst", "rest")),
    LOG("log", "Log", listOf("log")),
    BICEP("bicep", "Bicep", listOf("bicep")),
    HCL("hcl", "HCL", listOf("tf", "hcl", "tfvars")),
    THRIFT("thrift", "Thrift", listOf("thrift")),
    JSONC("jsonc", "JSON with Comments", listOf("jsonc", "code-workspace")),
    SHADERLAB("shaderlab", "ShaderLab", listOf("shader", "cginc", "hlslinc")),
    HLSL("hlsl", "HLSL", listOf("hlsl", "fx", "fxh")),
    WGSL("wgsl", "WGSL", listOf("wgsl")),
    CUDA("cuda", "CUDA", listOf("cu", "cuh")),
    OPENCL("opencl", "OpenCL", listOf("opencl")),
    BIBTEX("bibtex", "BibTeX", listOf("bib")),
    GIT_COMMIT("git-commit", "Git Commit", listOf("git-commit")),
    GIT_REBASE("git-rebase", "Git Rebase", listOf("git-rebase")),
    DOCKERCOMPOSE("dockercompose", "Docker Compose", listOf("dockercompose"));

    companion object {
        fun fromExtension(ext: String): EditorLanguage {
            val lower = ext.lowercase()
            if (lower == "dockerfile" || lower == "") return DOCKERFILE
            if (lower == "makefile") return MAKEFILE
            if (lower == "cmakelists.txt") return CMAKE
            if (lower == "gitignore") return GITIGNORE
            if (lower == "cshtml" || lower == "razor") return RAZOR
            if (lower == "hbs" || lower == "handlebars") return HANDLEBARS
            if (lower == "jade") return PUG
            if (lower == "coffee" || lower == "cson" || lower == "iced") return COFFEESCRIPT
            if (lower == "sol") return SOLIDITY
            if (lower == "tex" || lower == "ltx") return LATEX
            if (lower == "fs" || lower == "fsi" || lower == "fsx") return FSHARP
            if (lower == "ml" || lower == "mli") return OCAML
            if (lower == "vert" || lower == "frag" || lower == "geom" || lower == "comp" || lower == "vs" || lower == "fs" || lower == "glsl") return GLSL
            if (lower == "qml" || lower == "qmlproject") return QML
            if (lower == "wat" || lower == "wasm") return WASM
            if (lower == "twig") return TWIG
            if (lower == "ejs") return EJS
            if (lower == "haml") return HAML
            if (lower == "slim") return SLIM
            if (lower == "vhd" || lower == "vhdl" || lower == "vho") return VHDL
            if (lower == "v" || lower == "sv" || lower == "svh") return VERILOG
            if (lower == "cr") return CRYSTAL
            if (lower == "tpl") return SMARTY
            if (lower == "liquid") return LIQUID
            if (lower == "mustache") return MUSTACHE
            if (lower == "jinja" || lower == "j2") return JINJA
            if (lower == "vm" || lower == "vtl") return VELOCITY
            if (lower == "f" || lower == "f90" || lower == "f95" || lower == "for" || lower == "f03" || lower == "f08") return FORTRAN
            if (lower == "pas" || lower == "p") return PASCAL
            if (lower == "pp" || lower == "puppet" || lower == "epp") return PUPPET
            if (lower == "adb" || lower == "ads") return ADA
            if (lower == "lisp" || lower == "lsp" || lower == "cl") return LISP
            if (lower == "tcl") return TCL
            if (lower == "asm" || lower == "nasm" || lower == "inc" || lower == "s") return ASM
            if (lower == "hack" || lower == "hh" || lower == "hhi") return HACK
            if (lower == "apex" || lower == "cls" || lower == "trigger" || lower == "apxc" || lower == "apxt") return APEX
            if (lower == "abap") return ABAP
            if (lower == "as") return ACTIONSCRIPT
            if (lower == "st") return SMALLTALK
            if (lower == "rkt" || lower == "rktl" || lower == "rktd") return RACKET
            if (lower == "scm" || lower == "ss" || lower == "sch" || lower == "sld") return SCHEME
            if (lower == "nim" || lower == "nims" || lower == "nimble") return NIM
            if (lower == "m" || lower == "matlab") return MATLAB
            if (lower == "vb" || lower == "bas" || lower == "vbs") return VB
            if (lower == "xaml") return XAML
            if (lower == "rst" || lower == "rest") return RST
            if (lower == "log") return LOG
            if (lower == "bicep") return BICEP
            if (lower == "tf" || lower == "hcl" || lower == "tfvars") return HCL
            if (lower == "thrift") return THRIFT
            if (lower == "jsonc" || lower == "code-workspace") return JSONC
            if (lower == "shader" || lower == "cginc" || lower == "hlslinc") return SHADERLAB
            if (lower == "hlsl" || lower == "fx" || lower == "fxh") return HLSL
            if (lower == "wgsl") return WGSL
            if (lower == "cu" || lower == "cuh") return CUDA
            if (lower == "opencl") return OPENCL
            if (lower == "bib") return BIBTEX
            if (lower == "git-commit") return GIT_COMMIT
            if (lower == "git-rebase") return GIT_REBASE
            if (lower == "dockercompose") return DOCKERCOMPOSE
            if (lower == "mm") return OBJECTIVECPP
            if (lower == "c" || lower == "h") return C
            return entries.find { lower in it.extensions } ?: PLAINTEXT
        }

        fun fromFileName(name: String): EditorLanguage {
            val lowerName = name.lowercase()
            if (name == "Dockerfile" || lowerName == "dockerfile") return DOCKERFILE
            if (name == "Makefile" || name == "makefile" || lowerName == "makefile") return MAKEFILE
            if (name == "CMakeLists.txt" || lowerName == "cmakelists.txt") return CMAKE
            if (name == ".gitignore" || lowerName == ".gitignore") return GITIGNORE
            if (name == "COMMIT_EDITMSG" || name == "MERGE_MSG" || lowerName.contains("commit_editmsg") || lowerName.contains("merge_msg")) return GIT_COMMIT
            if (name == "git-rebase-todo" || lowerName == "git-rebase-todo") return GIT_REBASE
            if (name == "docker-compose.yml" || name == "docker-compose.yaml" || name == "compose.yml" || name == "compose.yaml" || lowerName == "docker-compose.yml" || lowerName == "compose.yaml") return DOCKERCOMPOSE
            if (name == "build.gradle" || name == "settings.gradle" || name.endsWith(".gradle")) return GROOVY
            val ext = name.substringAfterLast('.', "")
            return if (ext == name) {
                fromExtension(name.lowercase())
            } else {
                fromExtension(ext)
            }
        }
    }
}
