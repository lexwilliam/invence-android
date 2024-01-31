package com.lexwilliam.product.usecase

import arrow.core.Either
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.util.DeleteProductFailure
import javax.inject.Inject

class DeleteProductCategoryUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        suspend operator fun invoke(
            category: ProductCategory
        ): Either<DeleteProductFailure, ProductCategory> =
            repository.deleteProductCategory(
                category
            )
    }