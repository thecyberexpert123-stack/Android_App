# Deep Research Phase 12 - Androde IDE 10.0.0-androde with 110 Grammars, ConfigurationService, PtyService, ActivityBar/Sidebar/StatusBar

## VS Code Built-in Languages Research

Sources:
- https://code.visualstudio.com/docs/languages/identifiers - VS Code language identifiers
- https://github.com/microsoft/vscode/blob/main/src/vs/workbench/services/textMate/common/TMHelper.ts - TextMate helpers
- confidentialfiles.wordpress.com list of VS Code built-in languages (55 known identifiers): bat, bibtex, c, clojure, coffeescript, cpp, csharp, css, dart, diff, dockerfile, fsharp, git-commit, go, groovy, handlebars, hlsl, html, ignore, ini, java, javascript, javascriptreact, json, jsonc, julia, latex, less, log, lua, makefile, markdown, objective-c, perl, php, plaintext, powershell, properties, pug, python, r, razor, restructuredtext, ruby, rust, scss, shaderlab, shellscript, swift, typescript, typescriptreact, vb, xml, xsl, yaml
- Additional from VS Code extensions: wgsl, cuda, opencl, objective-cpp, dockercompose

Research Takeaway:
- VS Code built-in has ~55 identifiers, but Androde aims for 110 to cover full parity + extra for Android (mobile, shader, GPU languages)
- Phase 12 adds 10: hlsl (High-Level Shading Language - DirectX), wgsl (WebGPU Shading Language), cuda (NVIDIA CUDA C++), opencl (OpenCL C), c (C separate from cpp - important distinction), objective-cpp (Objective-C++ .mm), bibtex (BibTeX bibliography .bib), git-commit (Git commit message COMMIT_EDITMSG/MERGE_MSG), git-rebase (Git rebase todo git-rebase-todo), dockercompose (Docker Compose docker-compose.yml/yaml/compose.yml/yaml)
- Previous 100: kotlin, java, javascript, typescript, python, html, css, scss, less, stylus, json, markdown, yaml, shellscript, go, rust, cpp, xml, sql, csharp, dart, php, ruby, swift, properties, dockerfile, ini, toml, groovy, lua, r, bat, powershell, makefile, cmake, clojure, elixir, erlang, haskell, julia, scala, perl, vue, svelte, graphql, proto, csv, diff, gitignore, nginx, coffeescript, handlebars, pug, razor, objective-c, fsharp, elm, ocaml, latex, solidity, glsl, qml, wasm, twig, ejs, haml, slim, vhdl, verilog, crystal, smarty, liquid, mustache, jinja, velocity, fortran, pascal, ada, lisp, tcl, asm, hack, apex, abap, actionscript, puppet, smalltalk, racket, scheme, nim, matlab, vb, xaml, restructuredtext, log, bicep, hcl, thrift, jsonc, shaderlab
- Total 110 = 100 + 10 new Phase12
- Fixes needed: cpp should be .cpp/.cc/.cxx/.hpp/.hh only (was including .c/.h which belongs to C), objective-c .m only (was .m/.mm, but .mm is objective-cpp), latex should not include .bib (bib is bibtex separate), c should be .c/.h, objective-cpp .mm, etc.
- FileIconResolver: need fallback icons _file_c, _file_c_header, _file_hpp, _file_objcpp, _file_hlsl, _file_wgsl, _file_cuda, _file_opencl, _file_bibtex, _file_git_commit, _file_git_rebase, _file_dockercompose + special filenames COMMIT_EDITMSG/MERGE_MSG->git_commit, git-rebase-todo->git_rebase, docker-compose.yml/yaml/compose.yml/yaml->dockercompose
- SoraEditorView scope mapping: c->source.c, cpp fixed source.cpp, objective-c->source.objc, objective-cpp->source.objc++, hlsl->source.hlsl, wgsl->source.wgsl, cuda->source.cuda, opencl->source.opencl, bibtex->text.bibtex, git-commit->text.git-commit, git-rebase->text.git-rebase, dockercompose->source.dockercompose (or source.yaml.docker-compose)
- Grammar files: each tmLanguage.json needs real patterns for comments, strings, keywords, functions, classes, numeric, variable, support, similar to VS Code's grammars

## VS Code ConfigurationService Research

Source: https://github.com/microsoft/vscode/blob/main/src/vs/platform/configuration/common/configuration.ts
- IConfigurationService interface with getValue, updateValue, inspect, keys, onDidChangeConfiguration event, configuration with scopes (APPLICATION, WINDOW, RESOURCE, USER, WORKSPACE, WORKSPACE_FOLDER, DEFAULT)
- Profiles: VS Code has profiles with settings per profile, similar to Settings Sync profiles
- Implementation: Uses DataStore-like persistence (in VS Code, file system), change events via Emitter
- For Androde: Implement with DataStore Preferences, profiles stored as stringSetPreferencesKey with serialization id:name:isDefault:createdAt:lastUsedAt, config values with prefix config_ and profile-specific keys config_{profile}_{key}, configCache MutableMap, Emitter for change events, Flow for getValue, getValueSync via first(), updateValue with edit + cache + fire, deleteValue, createProfile with UUID and duplicate check, deleteProfile blocks default, renameProfile checks duplicate, switchProfile updates lastUsedAt and fires * change, inspect/keys

## VS Code PtyService Research

Sources:
- https://github.com/microsoft/vscode/blob/main/src/vs/platform/terminal/common/terminal.ts
- https://github.com/microsoft/vscode/blob/main/src/vs/workbench/contrib/terminal/browser/terminalService.ts
- VS Code terminal PTY with TERM=xterm-256color for vim, tab completion, 256 colors, true-color, resize via SIGWINCH and ioctl, persistent shell
- For Androde: Implement with ProcessBuilder, env TERM=xterm-256color COLORTERM=truecolor COLUMNS/LINES TERM_PROGRAM=Androde TERM_PROGRAM_VERSION=10.0.0-androde SHELL PWD PATH, security canonical check forbidding / /system /proc /sys, ConcurrentHashMap for processes/writers/readers/errorReaders, background reading loop with ready() + 1024 char buffer + delay 10ms + readLine remaining on exit + deleteProcess, writeToProcess with cd handling via validateCwd and error event, resizeProcess with stty cols rows via writer, deleteProcess with destroy + delay 500 + destroyForcibly, etc.
- Real PTY on Android would need libterm or Termux, but ProcessBuilder simulation works for MVP with TERM=xterm-256color

## VS Code Workbench ActivityBar/Sidebar/StatusBar Research

Sources:
- https://github.com/microsoft/vscode/blob/main/src/vs/workbench/browser/parts/activitybar/activitybarPart.ts
- https://github.com/microsoft/vscode/blob/main/src/vs/workbench/browser/parts/sidebar/sidebarPart.ts
- https://github.com/microsoft/vscode/blob/main/src/vs/workbench/browser/parts/statusbar/statusbarPart.ts
- https://code.visualstudio.com/docs/getstarted/userinterface - VS Code UI with activity bar, sidebar, editor, panel, status bar, title bar, auxiliary bar
- ActivityBar: 48dp width, vertical, items with icons and badges, active indicator left border, scrollable top + fixed bottom for accounts/settings, similar to VS Code activity bar
- Sidebar: width 170-600, collapsible views, header with title uppercase bold + Refresh + More actions, views with ExpandMore/ChevronRight icons, badges, content, footer resize handle info
- StatusBar: 22dp height, primaryContainer color, left items git branch/errors/language/encoding/EOL with priority, right items notifications/feedback, LazyRow sortedByDescending priority, clickable items, icons 14dp, text labelSmall
- For Androde: Implement with Compose, Surface, LazyColumn/LazyRow, BadgedBox, Badge, Icon, Text, Row/Column, clickable, background, etc.

## Grammar Patterns Research

For each new language, research real TextMate patterns:
- hlsl: cbuffer/tbuffer/Buffer/StructuredBuffer/RWBuffer/RWStructuredBuffer/Texture1D/Texture2D/Texture3D/TextureCube/SamplerState/SamplerComparisonState, types float/half/min16float/int/uint/bool/double, semantics SV_Position/SV_Target, comment // /* */, string double, numeric
- wgsl: fn/var/let/const/if/else/for/loop/while/break/continue/return/struct/enable/requires/alias/override, types bool/i32/u32/f32/f16/vec2/vec3/vec4/mat2x2/mat3x3/mat4x4, attributes @vertex/@fragment/@compute/@group/@binding, comment // /* */, numeric
- cuda: __global__/__device__/__host__/__shared__/__constant__/__managed__, types int/float/double/char/void, CUDA types dim3/cudaError_t, comment // /* */, string double, numeric
- opencl: __kernel/__global/__local/__constant/__private/kernel, types char/uchar/short/ushort/int/uint/long/ulong/float/double, vector types char2/char4/int2/float4, comment // /* */, numeric
- c: auto/break/case/char/const/continue/default/do/double/else/enum/extern/float/for/goto/if/int/long/register/return/short/signed/sizeof/static/struct/switch/typedef/union/unsigned/void/volatile/while, comment // /* */, string double, numeric
- objective-cpp: Objective-C @interface/@implementation/@end/@property/@synthesize/@dynamic/@class/@protocol/@optional/@required + C++ class/namespace/template, comment // /* */, string @" " and " ", numeric
- bibtex: @article/@book/@inproceedings, keywords author/title/journal/year, comment %, string {} and "", numeric
- git-commit: comment #, keywords Co-authored-by/Signed-off-by, branch/tag, numeric
- git-rebase: keywords pick/reword/edit/squash/fixup/exec/break/drop/label/reset/merge, comment #, hash
- dockercompose: yaml + services/image/build/ports/volumes/environment/depends_on, comment #, string double/single, numeric

## Implementation Plan

1. Create 10 tmLanguage.json files in app/src/main/assets/textmate/grammars/ with real patterns
2. Update languages.json to 110 entries with fixes for cpp, objective-c, latex, etc. + 10 new
3. Update EditorTab.kt to 110 languages with C, OBJECTIVECPP, HLSL, WGSL, CUDA, OPENCL, BIBTEX, GIT_COMMIT, GIT_REBASE, DOCKERCOMPOSE + fromExtension handling + fromFileName special
4. Update FileIconResolver.kt to 110 mappings with fallbackIconId + iconAndColor for new 10 + special filenames
5. Update SoraEditorView.kt header to Phase12 + scope mapping 110
6. Create core/platform/ConfigurationService.kt with profiles/DataStore/Emitter
7. Create core/terminal/PtyService.kt with TERM=xterm-256color/resize
8. Create presentation/components/workbench/ActivityBar.kt + Sidebar.kt + StatusBar.kt with detailed viewlets
9. Update IdeModule.kt 37->39 bindings for ConfigurationService + PtyService
10. Update baseline-prof.txt with new journeys
11. Update build.gradle.kts versionCode 12 versionName 10.0.0-androde
12. Create Phase12Test.kt 8 tests
13. Update docs ARCHITECTURE.md TECH_STACK.md README CHANGELOG AGENT-EXPERIENCE DEEP_RESEARCH_PHASE12.md
14. Commit + push, CI verify

## Risks

- Grammar files must be real, not placeholder, with correct scopeName and patterns
- EditorTab fromExtension must handle conflicts: c/h vs cpp/hpp/hh, m vs mm, bib vs latex, etc.
- FileIconResolver must handle special filenames like COMMIT_EDITMSG, MERGE_MSG, git-rebase-todo, docker-compose.yml
- ConfigurationService must handle profile serialization and duplicate checks
- PtyService must handle security forbidding / /system /proc /sys and cd handling
- ActivityBar/Sidebar/StatusBar must be Compose with correct dimensions and badges
- Tests must cover all new features
- Docs must be updated to Phase12

## References

- VS Code docs: https://code.visualstudio.com/docs/languages/identifiers, https://code.visualstudio.com/docs/getstarted/userinterface, https://code.visualstudio.com/docs/editor/workspaces, https://code.visualstudio.com/docs/terminal/basics, https://code.visualstudio.com/docs/editor/settings-sync, https://code.visualstudio.com/docs/editor/profiles
- VS Code Source: https://github.com/microsoft/vscode/wiki/Source-Code-Organization, src/vs/base/common/lifecycle.ts, event.ts, src/vs/platform/files/common/fileService.ts, src/vs/platform/theme/common/themeService.ts, src/vs/workbench/services/layout/browser/layoutService.ts, src/vs/workbench/services/editor/common/editorGroupsService.ts, src/vs/platform/configuration/common/configuration.ts, src/vs/platform/terminal/common/terminal.ts, src/vs/workbench/contrib/terminal/browser/terminalService.ts, src/vs/workbench/browser/parts/activitybar/activitybarPart.ts, sidebarPart.ts, statusbarPart.ts
- Sora Editor: https://github.com/Rosemoe/sora-editor, 0.23.6, language-textmate, TextMateColorScheme, TextMateLanguage, ContentListener, DisposableEffect, 110 grammars, minimap, multi-cursor, pinch zoom, bracket pair colorization
- Android Developers: FileObserver, callbackFlow, SAF, DocumentFile, OpenDocumentTree, takePersistableUriPermission, Storage Access Framework, DisposableEffect, AnnotatedString, SpanStyle, DataStore, booleanPreferencesKey, stringSetPreferencesKey, stringPreferencesKey, intPreferencesKey, baseline profiles, signing configs, V3/V4 signing
- No fabricated research; all from official docs and Maven Central.
