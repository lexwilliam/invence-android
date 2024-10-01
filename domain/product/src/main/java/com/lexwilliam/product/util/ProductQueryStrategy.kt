package com.lexwilliam.product.util

import com.lexwilliam.product.model.ProductCategory
import java.util.Locale
import java.util.UUID

data class ProductQueryStrategy(
    val query: String = "",
    val categoryUUID: UUID? = null,
    val sortBy: SortStrategy? = null,
    val orderBy: OrderStrategy = OrderStrategy.ASCENDING
)

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

fun queryProductCategory(
    categories: List<ProductCategory>,
    queryStrategy: ProductQueryStrategy?
): List<ProductCategory> {
    var result = categories
    if (queryStrategy != null) {
        when {
            queryStrategy.query != "" -> {
                result =
                    categories
                        .map { category ->
                            category.copy(
                                products =
                                    category.products
                                        .filter { product ->
                                            product.name
                                                .lowercase(Locale.ROOT)
                                                .contains(
                                                    queryStrategy.query
                                                        .lowercase(Locale.ROOT)
                                                )
                                        }
                            )
                        }
            }
            queryStrategy.categoryUUID != null -> {
                result =
                    categories
                        .filter { category -> category.uuid == queryStrategy.categoryUUID }
            }
            else -> {}
        }
    }
    return result
}