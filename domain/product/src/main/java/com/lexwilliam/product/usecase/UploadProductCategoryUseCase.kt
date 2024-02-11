package com.lexwilliam.product.usecase

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.product.repository.ProductRepository
import com.lexwilliam.product.util.UploadImageFailure
import java.util.UUID
import javax.inject.Inject

class UploadProductCategoryUseCase
    @Inject
    constructor(
        private val productRepository: ProductRepository
    ) {
        suspend operator fun invoke(
            branchUUID: UUID,
            categoryUUID: UUID,
            format: UploadImageFormat
        ): Either<UploadImageFailure, Uri> {
            return productRepository.uploadProductCategoryImage(
                branchUUID,
                categoryUUID,
                format
            )
        }
    }