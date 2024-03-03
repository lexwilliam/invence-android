package com.lexwilliam.branch.usecase

import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.repository.BranchRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveBranchUseCase
    @Inject
    constructor(
        private val branchRepository: BranchRepository
    ) {
        operator fun invoke(branchUUID: UUID): Flow<Branch?> {
            return branchRepository.observeBranch(branchUUID)
        }
    }