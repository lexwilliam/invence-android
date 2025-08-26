package com.lexwilliam.product.usecase

import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductCategoryUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        operator fun invoke(): Flow<List<ProductCategory>> {
            return repository
                .observeProductCategory()
        }
    }