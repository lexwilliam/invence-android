package com.lexwilliam.product.usecase

import com.lexwilliam.product.model.ProductWithImageFormat
import com.lexwilliam.product.repository.TempProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTempProductUseCase
    @Inject
    constructor(
        private val tempProductRepository: TempProductRepository
    ) {
        operator fun invoke(): Flow<ProductWithImageFormat?> {
            return tempProductRepository.productFlow
        }
    }