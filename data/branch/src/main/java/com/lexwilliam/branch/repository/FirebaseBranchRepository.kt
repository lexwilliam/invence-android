package com.lexwilliam.branch.repository

import arrow.core.Either
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.branch.model.Branch
import com.lexwilliam.branch.model.BranchDto
import com.lexwilliam.branch.util.DeleteBranchFailure
import com.lexwilliam.branch.util.UnknownFailure
import com.lexwilliam.branch.util.UpsertBranchFailure
import com.lexwilliam.firebase.FirestoreConfig
import kotlinx.coroutines.flow.Flow
import java.util.UUID

fun firebaseBranchRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) = object : BranchRepository {
    override fun observeBranch(branchUUID: UUID): Flow<Branch> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertBranch(branch: Branch): Either<UpsertBranchFailure, Branch> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_BRANCH)
                .document(branch.uuid.toString())
                .set(BranchDto.fromDomain(branch))
            branch
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun deleteBranch(branch: Branch): Either<DeleteBranchFailure, Branch> {
        TODO("Not yet implemented")
    }
}