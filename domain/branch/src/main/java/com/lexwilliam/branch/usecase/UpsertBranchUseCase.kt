package com.lexwilliam.branch.usecase

import arrow.core.Either
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.repository.BranchRepository
import com.lexwilliam.branch.util.UpsertBranchFailure
import javax.inject.Inject

class UpsertBranchUseCase
    @Inject
    constructor(
        private val branchRepository: BranchRepository
    ) {
        suspend operator fun invoke(branch: Branch): Either<UpsertBranchFailure, Branch> {
            return branchRepository.upsertBranch(branch)
        }
    }