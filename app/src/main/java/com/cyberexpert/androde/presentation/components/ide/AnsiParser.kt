package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration

/**
 * Real working ANSI color parser for terminal - Phase 6 with 256 colors and true colors.
 * Similar to VS Code terminal ANSI handling, parses ANSI escape codes like \u001B[31m, \u001B[38;5;196m, \u001B[38;2;255;100;0m.
 * Production-ready with 16 colors, 256 colors, true colors, bold, italic, underline, etc.
 * Supports: \u001B[0m reset, \u001B[1m bold, \u001B[3m italic, \u001B[4m underline, \u001B[30-37m fg, \u001B[90-97m bright fg, \u001B[40-47m bg, \u001B[38;5;Nm 256 fg, \u001B[48;5;Nm 256 bg, \u001B[38;2;R;G;Bm true fg, \u001B[48;2;R;G;Bm true bg.
 */
object AnsiParser {

    private val ansiRegex = Regex("\u001B\\[([0-9;]+)m")

    private val ansiColorMap = mapOf(
        30 to Color(0xFF000000),
        31 to Color(0xFFCD3131),
        32 to Color(0xFF0DBC79),
        33 to Color(0xFFE5E510),
        34 to Color(0xFF2472C8),
        35 to Color(0xFFBC3FBC),
        36 to Color(0xFF11A8CD),
        37 to Color(0xFFE5E5E5),
        90 to Color(0xFF666666),
        91 to Color(0xFFF14C4C),
        92 to Color(0xFF23D18B),
        93 to Color(0xFFF5F543),
        94 to Color(0xFF3B8EEA),
        95 to Color(0xFFD670D6),
        96 to Color(0xFF29B8DB),
        97 to Color(0xFFE5E5E5)
    )

    private val ansiBgColorMap = mapOf(
        40 to Color(0xFF000000),
        41 to Color(0xFFCD3131),
        42 to Color(0xFF0DBC79),
        43 to Color(0xFFE5E510),
        44 to Color(0xFF2472C8),
        45 to Color(0xFFBC3FBC),
        46 to Color(0xFF11A8CD),
        47 to Color(0xFFE5E5E5)
    )

    // 256 color palette - xterm 256 colors
    private val xterm256Colors: List<Color> by lazy {
        val colors = mutableListOf<Color>()
        // 0-15: system colors (same as ansiColorMap but with more)
        colors.addAll(
            listOf(
                Color(0xFF000000), Color(0xFF800000), Color(0xFF008000), Color(0xFF808000),
                Color(0xFF000080), Color(0xFF800080), Color(0xFF008080), Color(0xFFC0C0C0),
                Color(0xFF808080), Color(0xFFFF0000), Color(0xFF00FF00), Color(0xFFFFFF00.toLong()),
                Color(0xFF0000FF), Color(0xFFFF00FF), Color(0xFF00FFFF), Color(0xFFFFFFFF)
            )
        )
        // 16-231: 6x6x6 color cube
        for (r in 0..5) {
            for (g in 0..5) {
                for (b in 0..5) {
                    val rr = if (r == 0) 0 else 55 + r * 40
                    val gg = if (g == 0) 0 else 55 + g * 40
                    val bb = if (b == 0) 0 else 55 + b * 40
                    colors.add(Color((0xFF shl 24) or (rr shl 16) or (gg shl 8) or bb))
                }
            }
        }
        // 232-255: grayscale
        for (i in 0..23) {
            val v = 8 + i * 10
            colors.add(Color((0xFF shl 24) or (v shl 16) or (v shl 8) or v))
        }
        colors
    }

    data class AnsiState(
        val foreground: Color? = null,
        val background: Color? = null,
        val bold: Boolean = false,
        val italic: Boolean = false,
        val underline: Boolean = false
    )

    fun parseToAnnotatedString(input: String, defaultColor: Color = Color(0xFFD4D4D4)): AnnotatedString {
        return buildAnnotatedString {
            var currentState = AnsiState(foreground = defaultColor)
            var lastIndex = 0

            val matches = ansiRegex.findAll(input)
            var hasAnsi = false
            for (match in matches) {
                hasAnsi = true
                val textBefore = input.substring(lastIndex, match.range.first)
                if (textBefore.isNotEmpty()) {
                    withStyle(currentState.toSpanStyle()) {
                        append(textBefore)
                    }
                }

                val codes = match.groupValues[1].split(";").mapNotNull { it.toIntOrNull() }
                currentState = updateState(currentState, codes, defaultColor)

                lastIndex = match.range.last + 1
            }

            val remaining = input.substring(lastIndex)
            if (remaining.isNotEmpty()) {
                withStyle(currentState.toSpanStyle()) {
                    append(remaining)
                }
            }

            if (!hasAnsi) {
                withStyle(SpanStyle(color = defaultColor)) {
                    append(input)
                }
            }
        }
    }

    fun stripAnsi(input: String): String {
        return ansiRegex.replace(input, "")
    }

    private fun updateState(current: AnsiState, codes: List<Int>, defaultColor: Color): AnsiState {
        var newState = current
        var i = 0
        while (i < codes.size) {
            val code = codes[i]
            when (code) {
                0 -> newState = AnsiState(foreground = defaultColor)
                1 -> newState = newState.copy(bold = true)
                3 -> newState = newState.copy(italic = true)
                4 -> newState = newState.copy(underline = true)
                22 -> newState = newState.copy(bold = false)
                23 -> newState = newState.copy(italic = false)
                24 -> newState = newState.copy(underline = false)
                in 30..37, in 90..97 -> {
                    ansiColorMap[code]?.let { color ->
                        newState = newState.copy(foreground = color)
                    }
                }
                39 -> newState = newState.copy(foreground = defaultColor)
                in 40..47 -> {
                    ansiBgColorMap[code]?.let { color ->
                        newState = newState.copy(background = color)
                    }
                }
                49 -> newState = newState.copy(background = null)
                38, 48 -> {
                    // Extended colors: 38;5;N (256) or 38;2;R;G;B (true)
                    if (i + 2 < codes.size) {
                        val type = codes[i + 1]
                        when (type) {
                            5 -> {
                                // 256 colors
                                val n = codes[i + 2]
                                if (n in xterm256Colors.indices) {
                                    val color = xterm256Colors[n]
                                    if (code == 38) {
                                        newState = newState.copy(foreground = color)
                                    } else {
                                        newState = newState.copy(background = color)
                                    }
                                }
                                i += 2
                            }
                            2 -> {
                                // True colors RGB
                                if (i + 4 < codes.size) {
                                    val r = codes[i + 2].coerceIn(0, 255)
                                    val g = codes[i + 3].coerceIn(0, 255)
                                    val b = codes[i + 4].coerceIn(0, 255)
                                    val color = Color((0xFF shl 24) or (r shl 16) or (g shl 8) or b)
                                    if (code == 38) {
                                        newState = newState.copy(foreground = color)
                                    } else {
                                        newState = newState.copy(background = color)
                                    }
                                    i += 4
                                }
                            }
                        }
                    }
                }
            }
            i++
        }
        return newState
    }

    private fun AnsiState.toSpanStyle(): SpanStyle {
        return SpanStyle(
            color = foreground ?: Color(0xFFD4D4D4),
            background = background ?: Color.Transparent,
            fontWeight = if (bold) FontWeight.Bold else null,
            fontStyle = if (italic) FontStyle.Italic else null,
            textDecoration = if (underline) TextDecoration.Underline else null
        )
    }

    fun containsAnsi(input: String): Boolean {
        return ansiRegex.containsMatchIn(input)
    }

    /**
     * Get 256 color by index
     */
    fun getXterm256Color(index: Int): Color {
        return if (index in xterm256Colors.indices) xterm256Colors[index] else Color(0xFFD4D4D4)
    }
}
