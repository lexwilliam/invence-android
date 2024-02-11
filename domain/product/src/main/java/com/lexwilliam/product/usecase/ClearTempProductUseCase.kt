package com.lexwilliam.product.usecase

import com.lexwilliam.product.repository.TempProductRepository
import javax.inject.Inject

class ClearTempProductUseCase
    @Inject
    constructor(
        private val tempProductRepository: TempProductRepository
    ) {
        suspend operator fun invoke() {
            tempProductRepository.clearTempProduct()
        }
    }