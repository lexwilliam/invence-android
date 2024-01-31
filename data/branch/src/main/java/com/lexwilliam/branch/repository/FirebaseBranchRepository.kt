package com.lexwilliam.branch.repository

import arrow.core.Either
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.util.DeleteBranchFailure
import com.lexwilliam.branch.util.UpsertBranchFailure
import kotlinx.coroutines.flow.Flow
import java.util.UUID

fun firebaseBranchRepository() =
    object : BranchRepository {
        override fun observeBranch(branchUUID: UUID): Flow<Branch> {
            TODO("Not yet implemented")
        }

        override suspend fun upsertBranch(branch: Branch): Either<UpsertBranchFailure, Branch> {
            TODO("Not yet implemented")
        }

        override suspend fun deleteBranch(branch: Branch): Either<DeleteBranchFailure, Branch> {
            TODO("Not yet implemented")
        }
    }