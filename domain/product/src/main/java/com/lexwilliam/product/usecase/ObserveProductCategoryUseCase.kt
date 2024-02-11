package com.lexwilliam.product.usecase

import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.util.ProductQueryStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class ObserveProductCategoryUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        operator fun invoke(
            branchUUID: UUID,
            queryStrategy: ProductQueryStrategy? = null
        ): Flow<List<ProductCategory>> {
            return repository
                .observeProductCategory(branchUUID)
                .map { category ->
                    queryProductCategory(category, queryStrategy)
                }
        }
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

            else -> {}
        }
    }
    return result
}