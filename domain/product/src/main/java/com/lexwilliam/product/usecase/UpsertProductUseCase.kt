package com.lexwilliam.product.usecase

import arrow.core.Either
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.util.UpsertProductFailure
import javax.inject.Inject

class UpsertProductUseCase
    @Inject
    constructor(
        private val repository: ProductRepository
    ) {
        suspend operator fun invoke(
            category: ProductCategory,
            product: Product
        ): Either<UpsertProductFailure, Product> =
            repository.upsertProduct(
                category,
                product
            )
    }