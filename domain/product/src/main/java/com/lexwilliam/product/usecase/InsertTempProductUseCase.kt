package com.lexwilliam.product.usecase

import com.lexwilliam.product.model.ProductWithImageFormat
import com.lexwilliam.product.repository.TempProductRepository
import javax.inject.Inject

class InsertTempProductUseCase
    @Inject
    constructor(
        private val tempProductRepository: TempProductRepository
    ) {
        suspend operator fun invoke(product: ProductWithImageFormat) {
            tempProductRepository.insertTempProduct(product)
        }
    }