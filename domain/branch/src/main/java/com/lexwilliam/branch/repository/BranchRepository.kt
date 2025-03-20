package com.lexwilliam.branch.repository

import android.net.Uri
import arrow.core.Either
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.util.DeleteBranchFailure
import com.lexwilliam.branch.util.UpsertBranchFailure
import com.lexwilliam.core.util.UploadImageFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BranchRepository {
    fun observeBranch(branchUUID: UUID): Flow<Branch?>

    suspend fun upsertBranch(branch: Branch): Either<UpsertBranchFailure, Branch>

    suspend fun deleteBranch(branch: Branch): Either<DeleteBranchFailure, Branch>

    suspend fun uploadBranchImage(
        branchUUID: UUID,
        image: Any
    ): Either<UploadImageFailure, Uri>
}