package com.lexwilliam.product.model

import android.net.Uri
import com.lexwilliam.core.extensions.toCurrency
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Product(
    val sku: String = "",
    val upc: String = "",
    val name: String = "",
    val description: String = "",
    val categoryName: String = "",
    val sellPrice: Double = 0.0,
    val items: List<ProductItem> = emptyList(),
    val imagePath: Uri? = null,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
) {
    val quantity = items.sumOf { it.quantity }

    private val minBuyPrice = items.minOfOrNull { it.buyPrice } ?: 0.0
    private val maxBuyPrice = items.maxOfOrNull { it.buyPrice } ?: 0.0
    val buyPriceRange =
        when {
            (items.size == 1) -> items[0].buyPrice.toCurrency()
            else -> "${minBuyPrice.toCurrency()} - ${maxBuyPrice.toCurrency()}"
        }

    fun getProfit(
        item: ProductItem,
        quantity: Int = 1
    ): Double {
        return (sellPrice - item.buyPrice) * quantity
    }
}