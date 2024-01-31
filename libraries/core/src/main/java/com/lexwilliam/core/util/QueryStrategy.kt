package com.lexwilliam.core.util

import java.util.UUID

data class QueryStrategy(
    val query: String = "",
    val filter: List<FilterStrategy> = emptyList(),
    val sortBy: SortStrategy? = null,
    val orderBy: OrderStrategy = OrderStrategy.ASCENDING
)

sealed interface FilterStrategy {
    data class CategoryFilter(val categoryUUID: UUID) : FilterStrategy

    data class PriceRange(val min: Double, val max: Double) : FilterStrategy
}

enum class SortStrategy {
    NAME,
    PRICE,
    MODIFIED_DATE,
    CREATED_DATE
}

enum class OrderStrategy {
    ASCENDING,
    DESCENDING
}