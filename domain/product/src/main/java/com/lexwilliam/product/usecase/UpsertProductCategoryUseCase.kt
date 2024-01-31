package com.lexwilliam.product.usecase

import arrow.core.Either
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.util.UpsertProductFailure
import javax.inject.Inject

class UpsertProductCategoryUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        suspend operator fun invoke(
            category: ProductCategory
        ): Either<UpsertProductFailure, ProductCategory> =
            repository.upsertProductCategory(
                category
            )
    }