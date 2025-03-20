package com.lexwilliam.product.usecase

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.core.util.UploadImageFailure
import com.lexwilliam.product.repository.ProductRepository
import java.util.UUID
import javax.inject.Inject

class UploadCategoryImageUseCase
    @Inject
    constructor(
        private val productRepository: ProductRepository
    ) {
        suspend operator fun invoke(
            branchUUID: UUID,
            categoryUUID: UUID,
            image: Any
        ): Either<UploadImageFailure, Uri> {
            return productRepository.uploadProductCategoryImage(
                branchUUID,
                categoryUUID,
                image
            )
        }
    }