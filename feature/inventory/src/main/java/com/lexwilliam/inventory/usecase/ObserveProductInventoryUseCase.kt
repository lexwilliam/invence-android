package com.lexwilliam.inventory.usecase

import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.user.source.SessionManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProductInventoryUseCase
    @Inject
    constructor(
        private val observeProduct: ObserveProductCategoryUseCase,
        private val sessionManager: SessionManager
    ) {
        operator fun invoke(): Flow<List<ProductCategory>> {
            return observeProduct()
        }
    }