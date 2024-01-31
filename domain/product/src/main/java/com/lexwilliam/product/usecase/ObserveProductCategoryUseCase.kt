package com.lexwilliam.product.usecase

import com.lexwilliam.core.util.TestUUID
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveProductCategoryUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        operator fun invoke(branchUUID: UUID = TestUUID.BRANCH_UUID): Flow<List<ProductCategory>> =
            repository.observeProductCategory(branchUUID)
    }