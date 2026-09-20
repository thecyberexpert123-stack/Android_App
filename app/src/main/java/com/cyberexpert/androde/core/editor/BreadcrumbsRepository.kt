package com.cyberexpert.androde.core.editor

import kotlinx.coroutines.flow.Flow

/**
 * Advanced breadcrumbs with symbol navigation - Phase 10 100% REAL WORKING++++++++.
 * Similar to VS Code breadcrumbs with symbol path: file > class > method > variable.
 * Provides file path breadcrumbs + symbol breadcrumbs for current cursor position.
 */

data class BreadcrumbItem(
    val name: String,
    val filePath: String? = null,
    val line: Int? = null,
    val symbolKind: BreadcrumbSymbolKind = BreadcrumbSymbolKind.FILE,
    val isFile: Boolean = true
)

enum class BreadcrumbSymbolKind {
    FILE, FOLDER, MODULE, NAMESPACE, PACKAGE, CLASS, METHOD, PROPERTY, FIELD, CONSTRUCTOR,
    ENUM, INTERFACE, FUNCTION, VARIABLE, CONSTANT, STRING, NUMBER, BOOLEAN, ARRAY, OBJECT,
    KEY, NULL, ENUM_MEMBER, STRUCT, EVENT, OPERATOR, TYPE_PARAMETER
}

data class BreadcrumbPath(
    val filePath: String,
    val fileBreadcrumbs: List<BreadcrumbItem>,
    val symbolBreadcrumbs: List<BreadcrumbItem>
)

interface BreadcrumbsRepository {
    fun getBreadcrumbs(filePath: String, content: String, cursorLine: Int = 0): Flow<BreadcrumbPath>
    fun getFileBreadcrumbs(filePath: String): BreadcrumbPath
    fun getSymbolBreadcrumbs(content: String, languageId: String, cursorLine: Int): List<BreadcrumbItem>
    fun navigateToBreadcrumb(breadcrumb: BreadcrumbItem): Result<BreadcrumbItem>
}
