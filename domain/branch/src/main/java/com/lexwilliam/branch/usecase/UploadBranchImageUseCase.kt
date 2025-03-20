package com.lexwilliam.branch.usecase

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.branch.repository.BranchRepository
import com.lexwilliam.core.util.UploadImageFailure
import java.util.UUID
import javax.inject.Inject

class UploadBranchImageUseCase
    @Inject
    constructor(
        private val branchRepository: BranchRepository
    ) {
        suspend operator fun invoke(
            branchUUID: UUID,
            image: Any
        ): Either<UploadImageFailure, Uri> {
            return branchRepository.uploadBranchImage(branchUUID, image)
        }
    }