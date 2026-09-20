package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.cyberexpert.androde.core.extensions.IconTheme

/**
 * Real file icon resolver using icon theme mappings - Phase 14 with 130 languages.
 * Similar to VS Code's icon theme resolution, maps file extensions/names to icons and colors.
 * Uses vscode_icons.json and material_icons.json mappings for 130 file types.
 * Production-ready with fallback to extension-based icons for 130 langs.
 */

data class FileIcon(
    val icon: ImageVector,
    val tint: Color,
    val iconId: String
)

object FileIconResolver {

    fun resolveFileIcon(
        fileName: String,
        isDirectory: Boolean = false,
        isExpanded: Boolean = false,
        iconTheme: IconTheme? = null
    ): String {
        if (iconTheme != null) {
            iconTheme.fileNames[fileName]?.let { return it }
            iconTheme.fileNames[fileName.lowercase()]?.let { return it }
            if (isDirectory) {
                val folderKey = fileName.lowercase()
                if (isExpanded) {
                    iconTheme.folderNamesExpanded[folderKey]?.let { return it }
                }
                iconTheme.folderNames[folderKey]?.let { return it }
            }
            val ext = fileName.substringAfterLast('.', "").lowercase()
            if (ext.isNotEmpty()) {
                iconTheme.fileExtensions[ext]?.let { return it }
            }
            val langId = getLanguageIdForFile(fileName)
            if (langId != null) {
                iconTheme.languageIds[langId]?.let { return it }
            }
        }
        return getFallbackIconId(fileName, isDirectory)
    }

    private fun getLanguageIdForFile(fileName: String): String? {
        val lowerName = fileName.lowercase()
        if (lowerName == "commit_editmsg" || lowerName == "merge_msg" || lowerName.contains("commit_editmsg")) return "git-commit"
        if (lowerName == "git-rebase-todo") return "git-rebase"
        if (lowerName == "docker-compose.yml" || lowerName == "docker-compose.yaml" || lowerName == "compose.yml" || lowerName == "compose.yaml") return "dockercompose"
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "js", "mjs", "cjs" -> "javascript"
            "jsx" -> "javascriptreact"
            "ts" -> "typescript"
            "tsx" -> "typescriptreact"
            "py", "pyw" -> "python"
            "c" -> "c"
            "h" -> "c"
            "cpp", "cc", "cxx", "hpp", "hh" -> "cpp"
            "html", "htm" -> "html"
            "css" -> "css"
            "scss" -> "scss"
            "less" -> "less"
            "styl", "stylus" -> "stylus"
            "json" -> "json"
            "jsonc", "code-workspace" -> "jsonc"
            "jsonl", "ndjson" -> "jsonl"
            "md", "markdown" -> "markdown"
            "yaml", "yml" -> "yaml"
            "sh", "bash", "zsh" -> "shellscript"
            "go" -> "go"
            "rs" -> "rust"
            "xml", "xsd", "svg", "plist" -> "xml"
            "xsl", "xslt" -> "xsl"
            "sql" -> "sql"
            "cs" -> "csharp"
            "dart" -> "dart"
            "php", "phtml" -> "php"
            "rb", "rake", "gemspec" -> "ruby"
            "swift" -> "swift"
            "properties", "conf", "cfg" -> "properties"
            "toml" -> "toml"
            "groovy", "gradle" -> "groovy"
            "lua" -> "lua"
            "r" -> "r"
            "bat", "cmd" -> "bat"
            "ps1", "psm1", "psd1" -> "powershell"
            "mk", "makefile" -> "makefile"
            "cmake" -> "cmake"
            "clj", "cljs", "cljc", "edn" -> "clojure"
            "ex", "exs" -> "elixir"
            "erl", "hrl" -> "erlang"
            "hs", "lhs" -> "haskell"
            "jl" -> "julia"
            "scala", "sc" -> "scala"
            "pl", "pm", "t" -> "perl"
            "vue" -> "vue"
            "svelte" -> "svelte"
            "graphql", "gql" -> "graphql"
            "proto" -> "proto"
            "csv" -> "csv"
            "diff", "patch" -> "diff"
            "gitignore" -> "gitignore"
            "nginx" -> "nginx"
            "coffee", "cson", "iced" -> "coffeescript"
            "hbs", "handlebars" -> "handlebars"
            "pug", "jade" -> "pug"
            "cshtml", "razor" -> "razor"
            "m" -> "objective-c"
            "mm" -> "objective-cpp"
            "fs", "fsi", "fsx" -> "fsharp"
            "elm" -> "elm"
            "ml", "mli" -> "ocaml"
            "tex", "ltx", "sty", "cls" -> "latex"
            "sol" -> "solidity"
            "glsl", "vert", "frag", "geom", "comp", "vs" -> "glsl"
            "qml", "qmlproject" -> "qml"
            "wat", "wasm" -> "wasm"
            "twig" -> "twig"
            "ejs" -> "ejs"
            "haml" -> "haml"
            "slim" -> "slim"
            "vhd", "vhdl", "vho" -> "vhdl"
            "v" -> "verilog"
            "sv", "svh" -> "systemverilog"
            "cr" -> "crystal"
            "tpl" -> "smarty"
            "liquid" -> "liquid"
            "mustache" -> "mustache"
            "jinja", "j2" -> "jinja"
            "vm", "vtl" -> "velocity"
            "f", "f90", "f95", "for", "f03", "f08" -> "fortran"
            "pas", "p" -> "pascal"
            "pp", "puppet", "epp" -> "puppet"
            "adb", "ads" -> "ada"
            "lisp", "lsp", "cl" -> "lisp"
            "tcl" -> "tcl"
            "asm", "s", "nasm", "inc" -> "asm"
            "hack", "hh", "hhi" -> "hack"
            "apex", "cls", "trigger", "apxc", "apxt" -> "apex"
            "abap" -> "abap"
            "as" -> "actionscript"
            "st" -> "smalltalk"
            "rkt", "rktl", "rktd" -> "racket"
            "scm", "ss", "sch", "sld" -> "scheme"
            "nim", "nims", "nimble" -> "nim"
            "matlab" -> "matlab"
            "vb", "bas", "vbs" -> "vb"
            "xaml" -> "xaml"
            "rst", "rest" -> "restructuredtext"
            "log" -> "log"
            "bicep" -> "bicep"
            "tf", "hcl", "tfvars" -> "hcl"
            "thrift" -> "thrift"
            "shader", "cginc", "hlslinc" -> "shaderlab"
            "hlsl", "fx", "fxh" -> "hlsl"
            "wgsl" -> "wgsl"
            "cu", "cuh" -> "cuda"
            "opencl" -> "opencl"
            "bib" -> "bibtex"
            "git-commit" -> "git-commit"
            "git-rebase" -> "git-rebase"
            "dockercompose" -> "dockercompose"
            "ignore", "dockerignore", "npmignore", "eslintignore" -> "ignore"
            "zig" -> "zig"
            "hx", "hxml" -> "haxe"
            "purs" -> "purescript"
            "re", "rei" -> "reason"
            "nix" -> "nix"
            "cob", "cbl", "cpy" -> "cobol"
            "d" -> "d"
            "odin" -> "odin"
            "gleam" -> "gleam"
            "res", "resi" -> "rescript"
            "astro" -> "astro"
            "mdx" -> "mdx"
            "prisma" -> "prisma"
            "cue" -> "cue"
            else -> null
        }
    }

    private fun getFallbackIconId(fileName: String, isDirectory: Boolean): String {
        if (isDirectory) return "_folder"
        val lower = fileName.lowercase()
        if (lower == "dockerfile") return "_file_docker"
        if (lower == "makefile" || lower == "cmakelists.txt") return "_file_makefile"
        if (lower == "build.gradle" || lower == "settings.gradle") return "_file_gradle"
        if (lower == ".gitignore") return "_file_git"
        if (lower == "commit_editmsg" || lower == "merge_msg") return "_file_git_commit"
        if (lower == "git-rebase-todo") return "_file_git_rebase"
        if (lower == "docker-compose.yml" || lower == "docker-compose.yaml" || lower == "compose.yml" || lower == "compose.yaml") return "_file_dockercompose"
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "kt", "kts" -> "_file_kotlin"
            "java" -> "_file_java"
            "js" -> "_file_js"
            "jsx" -> "_file_react"
            "ts" -> "_file_typescript"
            "tsx" -> "_file_react_ts"
            "py", "pyw" -> "_file_python"
            "c" -> "_file_c"
            "h" -> "_file_c_header"
            "cpp", "cc", "cxx" -> "_file_cpp"
            "hpp", "hh" -> "_file_hpp"
            "html", "htm" -> "_file_html"
            "css" -> "_file_css"
            "scss" -> "_file_sass"
            "less" -> "_file_less"
            "styl", "stylus" -> "_file_stylus"
            "json" -> "_file_json"
            "jsonc", "code-workspace" -> "_file_jsonc"
            "jsonl", "ndjson" -> "_file_jsonl"
            "md", "markdown" -> "_file_markdown"
            "yaml", "yml" -> "_file_yaml"
            "xml", "xsd", "svg", "plist" -> "_file_xml"
            "xsl", "xslt" -> "_file_xsl"
            "sql" -> "_file_sql"
            "sh", "bash", "zsh" -> "_file_shell"
            "go" -> "_file_go"
            "rs" -> "_file_rust"
            "cs" -> "_file_csharp"
            "dart" -> "_file_dart"
            "php", "phtml" -> "_file_php"
            "rb" -> "_file_ruby"
            "swift" -> "_file_swift"
            "properties", "ini", "conf", "cfg" -> "_file_properties"
            "toml" -> "_file_toml"
            "groovy", "gradle" -> "_file_groovy"
            "lua" -> "_file_lua"
            "r" -> "_file_r"
            "bat", "cmd" -> "_file_bat"
            "ps1", "psm1", "psd1" -> "_file_powershell"
            "mk" -> "_file_makefile"
            "cmake" -> "_file_cmake"
            "clj", "cljs", "cljc", "edn" -> "_file_clojure"
            "ex", "exs" -> "_file_elixir"
            "erl", "hrl" -> "_file_erlang"
            "hs", "lhs" -> "_file_haskell"
            "jl" -> "_file_julia"
            "scala", "sc" -> "_file_scala"
            "pl", "pm", "t" -> "_file_perl"
            "vue" -> "_file_vue"
            "svelte" -> "_file_svelte"
            "graphql", "gql" -> "_file_graphql"
            "proto" -> "_file_proto"
            "csv" -> "_file_csv"
            "diff", "patch" -> "_file_diff"
            "gitignore" -> "_file_git"
            "nginx" -> "_file_nginx"
            "dockerfile" -> "_file_docker"
            "coffee", "cson", "iced" -> "_file_coffeescript"
            "hbs", "handlebars" -> "_file_handlebars"
            "pug", "jade" -> "_file_pug"
            "cshtml", "razor" -> "_file_razor"
            "m" -> "_file_objc"
            "mm" -> "_file_objcpp"
            "fs", "fsi", "fsx" -> "_file_fsharp"
            "elm" -> "_file_elm"
            "ml", "mli" -> "_file_ocaml"
            "tex", "ltx", "sty", "cls" -> "_file_latex"
            "sol" -> "_file_solidity"
            "glsl", "vert", "frag", "geom", "comp", "vs" -> "_file_glsl"
            "qml", "qmlproject" -> "_file_qml"
            "wat", "wasm" -> "_file_wasm"
            "twig" -> "_file_twig"
            "ejs" -> "_file_ejs"
            "haml" -> "_file_haml"
            "slim" -> "_file_slim"
            "vhd", "vhdl", "vho" -> "_file_vhdl"
            "v" -> "_file_verilog"
            "sv", "svh" -> "_file_systemverilog"
            "cr" -> "_file_crystal"
            "tpl" -> "_file_smarty"
            "liquid" -> "_file_liquid"
            "mustache" -> "_file_mustache"
            "jinja", "j2" -> "_file_jinja"
            "vm", "vtl" -> "_file_velocity"
            "f", "f90", "f95", "for", "f03", "f08" -> "_file_fortran"
            "pas", "p" -> "_file_pascal"
            "pp", "puppet", "epp" -> "_file_puppet"
            "adb", "ads" -> "_file_ada"
            "lisp", "lsp", "cl" -> "_file_lisp"
            "tcl" -> "_file_tcl"
            "asm", "s", "nasm", "inc" -> "_file_asm"
            "hack", "hh", "hhi" -> "_file_hack"
            "apex", "cls", "trigger", "apxc", "apxt" -> "_file_apex"
            "abap" -> "_file_abap"
            "as" -> "_file_actionscript"
            "st" -> "_file_smalltalk"
            "rkt", "rktl", "rktd" -> "_file_racket"
            "scm", "ss", "sch", "sld" -> "_file_scheme"
            "nim", "nims", "nimble" -> "_file_nim"
            "matlab" -> "_file_matlab"
            "vb", "bas", "vbs" -> "_file_vb"
            "xaml" -> "_file_xaml"
            "rst", "rest" -> "_file_rst"
            "log" -> "_file_log"
            "bicep" -> "_file_bicep"
            "tf", "hcl", "tfvars" -> "_file_terraform"
            "thrift" -> "_file_thrift"
            "shader", "cginc", "hlslinc" -> "_file_shaderlab"
            "hlsl", "fx", "fxh" -> "_file_hlsl"
            "wgsl" -> "_file_wgsl"
            "cu", "cuh" -> "_file_cuda"
            "opencl" -> "_file_opencl"
            "bib" -> "_file_bibtex"
            "git-commit" -> "_file_git_commit"
            "git-rebase" -> "_file_git_rebase"
            "dockercompose" -> "_file_dockercompose"
            "xsl", "xslt" -> "_file_xsl"
            "ignore", "dockerignore", "npmignore", "eslintignore" -> "_file_ignore"
            "jsx" -> "_file_react"
            "tsx" -> "_file_react_ts"
            "zig" -> "_file_zig"
            "hx", "hxml" -> "_file_haxe"
            "purs" -> "_file_purescript"
            "re", "rei" -> "_file_reason"
            "nix" -> "_file_nix"
            "cob", "cbl", "cpy" -> "_file_cobol"
            "d" -> "_file_d"
            "odin" -> "_file_odin"
            "gleam" -> "_file_gleam"
            "res", "resi" -> "_file_rescript"
            "astro" -> "_file_astro"
            "mdx" -> "_file_mdx"
            "prisma" -> "_file_prisma"
            "cue" -> "_file_cue"
            "txt" -> "_file_text"
            else -> "_file"
        }
    }

    @Composable
    fun getIconAndColor(iconId: String): Pair<ImageVector, Color> {
        return when {
            iconId.contains("kotlin") -> Icons.Default.Code to MaterialTheme.colorScheme.primary
            iconId.contains("java") -> Icons.Default.Coffee to Color(0xFFB07219)
            iconId.contains("js") || iconId.contains("javascript") -> Icons.Default.Javascript to Color(0xFFF1E05A)
            iconId.contains("typescript") -> Icons.Default.Code to Color(0xFF2B7489)
            iconId.contains("python") -> Icons.Default.Code to Color(0xFF3572A5)
            iconId.contains("html") -> Icons.Default.Html to Color(0xFFE34C26)
            iconId.contains("css") || iconId.contains("sass") || iconId.contains("less") -> Icons.Default.DataObject to Color(0xFF563D7C)
            iconId.contains("json") -> Icons.Default.DataObject to Color(0xFF292929)
            iconId.contains("markdown") || iconId.contains("readme") -> Icons.Default.Description to Color(0xFF083FA1)
            iconId.contains("yaml") -> Icons.Default.Settings to Color(0xFFA97C50)
            iconId.contains("xml") || iconId.contains("xaml") -> Icons.Default.Code to Color(0xFF005A9C)
            iconId.contains("sql") -> Icons.Default.Storage to Color(0xFFE38C00)
            iconId.contains("shell") || iconId.contains("bat") || iconId.contains("powershell") -> Icons.Default.Terminal to Color(0xFF89E051)
            iconId.contains("go") -> Icons.Default.Code to Color(0xFF00ADD8)
            iconId.contains("rust") -> Icons.Default.Code to Color(0xFFDEA584)
            iconId.contains("cpp") || iconId.contains("c_") -> Icons.Default.Code to Color(0xFFF34B7D)
            iconId.contains("_c") && !iconId.contains("csharp") -> Icons.Default.Code to Color(0xFF555555)
            iconId.contains("csharp") || iconId.contains("razor") -> Icons.Default.Code to Color(0xFF178600)
            iconId.contains("dart") -> Icons.Default.Code to Color(0xFF00B4AB)
            iconId.contains("php") -> Icons.Default.Code to Color(0xFF4F5D95)
            iconId.contains("ruby") -> Icons.Default.Code to Color(0xFF701516)
            iconId.contains("swift") -> Icons.Default.Code to Color(0xFFFFAC45)
            iconId.contains("docker") -> Icons.Default.Storage to Color(0xFF384D54)
            iconId.contains("gradle") -> Icons.Default.Settings to Color(0xFF02303A)
            iconId.contains("git") -> Icons.Default.Code to Color(0xFFF05032)
            iconId.contains("properties") || iconId.contains("ini") || iconId.contains("toml") -> Icons.Default.Settings to MaterialTheme.colorScheme.onSurfaceVariant
            iconId.contains("makefile") || iconId.contains("cmake") -> Icons.Default.Settings to Color(0xFF427819)
            iconId.contains("groovy") -> Icons.Default.Code to Color(0xFF4298B8)
            iconId.contains("lua") -> Icons.Default.Code to Color(0xFF000080)
            iconId.contains("r") -> Icons.Default.Code to Color(0xFF198CE7)
            iconId.contains("clojure") -> Icons.Default.Code to Color(0xFF5881D8)
            iconId.contains("elixir") -> Icons.Default.Code to Color(0xFF6E4A7E)
            iconId.contains("erlang") -> Icons.Default.Code to Color(0xFFB83998)
            iconId.contains("haskell") -> Icons.Default.Code to Color(0xFF5E5086)
            iconId.contains("julia") -> Icons.Default.Code to Color(0xFF9558B2)
            iconId.contains("scala") -> Icons.Default.Code to Color(0xFFC22D40)
            iconId.contains("perl") -> Icons.Default.Code to Color(0xFF0298C3)
            iconId.contains("vue") -> Icons.Default.Code to Color(0xFF41B883)
            iconId.contains("svelte") -> Icons.Default.Code to Color(0xFFFF3E00)
            iconId.contains("graphql") -> Icons.Default.Code to Color(0xFFE535AB)
            iconId.contains("proto") -> Icons.Default.Code to Color(0xFF8D6E63)
            iconId.contains("csv") -> Icons.Default.DataObject to Color(0xFF4CAF50)
            iconId.contains("diff") -> Icons.Default.Code to Color(0xFFCE9178)
            iconId.contains("nginx") -> Icons.Default.Settings to Color(0xFF009639)
            iconId.contains("coffeescript") -> Icons.Default.Code to Color(0xFF244776)
            iconId.contains("handlebars") -> Icons.Default.Code to Color(0xFFF7931E)
            iconId.contains("pug") -> Icons.Default.Code to Color(0xFFA86454)
            iconId.contains("objc") && !iconId.contains("objcpp") -> Icons.Default.Code to Color(0xFF438EFF)
            iconId.contains("objcpp") -> Icons.Default.Code to Color(0xFF6866FB)
            iconId.contains("fsharp") -> Icons.Default.Code to Color(0xFFB845FC)
            iconId.contains("elm") -> Icons.Default.Code to Color(0xFF60B5CC)
            iconId.contains("ocaml") -> Icons.Default.Code to Color(0xFF3BE133)
            iconId.contains("latex") -> Icons.Default.Description to Color(0xFF3D6117)
            iconId.contains("solidity") -> Icons.Default.Code to Color(0xFF363636)
            iconId.contains("glsl") -> Icons.Default.Code to Color(0xFF5586A6)
            iconId.contains("qml") -> Icons.Default.Code to Color(0xFF44A51C)
            iconId.contains("wasm") -> Icons.Default.Code to Color(0xFF654FF0)
            iconId.contains("twig") -> Icons.Default.Code to Color(0xFFC1D026)
            iconId.contains("ejs") -> Icons.Default.Code to Color(0xFFA91E50)
            iconId.contains("haml") -> Icons.Default.Code to Color(0xFFECE2A6)
            iconId.contains("slim") -> Icons.Default.Code to Color(0xFF2A2A2A)
            iconId.contains("vhdl") -> Icons.Default.Code to Color(0xFF543978)
            iconId.contains("verilog") -> Icons.Default.Code to Color(0xFF848BF5)
            iconId.contains("crystal") -> Icons.Default.Code to Color(0xFF000100)
            iconId.contains("smarty") -> Icons.Default.Code to Color(0xFFF0C040)
            iconId.contains("liquid") -> Icons.Default.Code to Color(0xFF00A8E8)
            iconId.contains("mustache") -> Icons.Default.Code to Color(0xFFA62C2B)
            iconId.contains("jinja") -> Icons.Default.Code to Color(0xFFB41722)
            iconId.contains("velocity") -> Icons.Default.Code to Color(0xFF0096D6)
            iconId.contains("fortran") -> Icons.Default.Code to Color(0xFF4D41B1)
            iconId.contains("pascal") -> Icons.Default.Code to Color(0xFFE3F171)
            iconId.contains("ada") -> Icons.Default.Code to Color(0xFF02F88C)
            iconId.contains("lisp") -> Icons.Default.Code to Color(0xFF3FB68B)
            iconId.contains("tcl") -> Icons.Default.Code to Color(0xFFE4CC98)
            iconId.contains("asm") -> Icons.Default.Code to Color(0xFF6E4C13)
            iconId.contains("hack") -> Icons.Default.Code to Color(0xFF878787)
            iconId.contains("apex") -> Icons.Default.Code to Color(0xFF1797C0)
            iconId.contains("abap") -> Icons.Default.Code to Color(0xFFE8274B)
            iconId.contains("actionscript") -> Icons.Default.Code to Color(0xFFBD120F)
            iconId.contains("puppet") -> Icons.Default.Code to Color(0xFF302B6D)
            iconId.contains("smalltalk") -> Icons.Default.Code to Color(0xFF596706)
            iconId.contains("racket") -> Icons.Default.Code to Color(0xFF9A629A)
            iconId.contains("scheme") -> Icons.Default.Code to Color(0xFF1E4AEC)
            iconId.contains("nim") -> Icons.Default.Code to Color(0xFFD899FA)
            iconId.contains("matlab") -> Icons.Default.Code to Color(0xFF0076A8)
            iconId.contains("vb") -> Icons.Default.Code to Color(0xFF945DB7)
            iconId.contains("xaml") -> Icons.Default.Code to Color(0xFF0C54C2)
            iconId.contains("rst") -> Icons.Default.Description to Color(0xFF141414)
            iconId.contains("log") -> Icons.Default.Description to Color(0xFF000000)
            iconId.contains("bicep") -> Icons.Default.Code to Color(0xFF519ABA)
            iconId.contains("terraform") -> Icons.Default.Code to Color(0xFF5C4EE5)
            iconId.contains("thrift") -> Icons.Default.Code to Color(0xFFD12127)
            iconId.contains("jsonc") -> Icons.Default.DataObject to Color(0xFF292929)
            iconId.contains("shaderlab") -> Icons.Default.Code to Color(0xFF222C37)
            iconId.contains("hlsl") -> Icons.Default.Code to Color(0xFF007ACC)
            iconId.contains("wgsl") -> Icons.Default.Code to Color(0xFF1A5FB4)
            iconId.contains("cuda") -> Icons.Default.Code to Color(0xFF76B900)
            iconId.contains("opencl") -> Icons.Default.Code to Color(0xFFCC0000)
            iconId.contains("bibtex") -> Icons.Default.Description to Color(0xFF8A2BE2)
            iconId.contains("git_commit") -> Icons.Default.Code to Color(0xFFF05032)
            iconId.contains("git_rebase") -> Icons.Default.Code to Color(0xFFF05032)
            iconId.contains("dockercompose") -> Icons.Default.Storage to Color(0xFF0DB7ED)
            iconId.contains("xsl") -> Icons.Default.Code to Color(0xFFEB8A93)
            iconId.contains("ignore") -> Icons.Default.Code to Color(0xFF6B6B6B)
            iconId.contains("react") -> Icons.Default.Code to Color(0xFF61DAFB)
            iconId.contains("systemverilog") -> Icons.Default.Code to Color(0xFF848BF5)
            iconId.contains("zig") -> Icons.Default.Code to Color(0xFFEC915C)
            iconId.contains("haxe") -> Icons.Default.Code to Color(0xFFEA8220)
            iconId.contains("purescript") -> Icons.Default.Code to Color(0xFF14161A)
            iconId.contains("reason") -> Icons.Default.Code to Color(0xFFDB4D3D)
            iconId.contains("jsonl") -> Icons.Default.DataObject to Color(0xFF292929)
            iconId.contains("nix") -> Icons.Default.Code to Color(0xFF7EB5F6)
            iconId.contains("cobol") -> Icons.Default.Code to Color(0xFF2A2A2A)
            iconId.contains("_d") -> Icons.Default.Code to Color(0xFFB03931)
            iconId.contains("odin") -> Icons.Default.Code to Color(0xFF3882D2)
            iconId.contains("gleam") -> Icons.Default.Code to Color(0xFFFFAFF3)
            iconId.contains("rescript") -> Icons.Default.Code to Color(0xFFDB4D3D)
            iconId.contains("astro") -> Icons.Default.Code to Color(0xFFFF5D01)
            iconId.contains("mdx") -> Icons.Default.Code to Color(0xFFFCB32C)
            iconId.contains("prisma") -> Icons.Default.Code to Color(0xFF2D3748)
            iconId.contains("cue") -> Icons.Default.Code to Color(0xFF1E90FF)
            iconId.contains("folder_src") -> Icons.Default.Folder to Color(0xFF90A4AE)
            iconId.contains("folder") -> Icons.Default.Folder to MaterialTheme.colorScheme.primary
            iconId.contains("text") -> Icons.Default.Description to MaterialTheme.colorScheme.onSurfaceVariant
            else -> Icons.Default.Description to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }

    @Composable
    fun getFolderIconAndColor(iconId: String, isExpanded: Boolean): Pair<ImageVector, Color> {
        val base = when {
            iconId.contains("src") -> Icons.Default.Folder to Color(0xFF90A4AE)
            iconId.contains("app") -> Icons.Default.Folder to Color(0xFF8BC34A)
            iconId.contains("java") || iconId.contains("kotlin") -> Icons.Default.Folder to Color(0xFFB07219)
            iconId.contains("res") || iconId.contains("assets") -> Icons.Default.Folder to Color(0xFFFFC107)
            iconId.contains("gradle") || iconId.contains("build") -> Icons.Default.Folder to Color(0xFF607D8B)
            iconId.contains("git") -> Icons.Default.Folder to Color(0xFFF05032)
            iconId.contains("test") -> Icons.Default.Folder to Color(0xFF4CAF50)
            else -> Icons.Default.Folder to MaterialTheme.colorScheme.primary
        }
        return if (isExpanded) Icons.Default.FolderOpen to base.second else base
    }
}
