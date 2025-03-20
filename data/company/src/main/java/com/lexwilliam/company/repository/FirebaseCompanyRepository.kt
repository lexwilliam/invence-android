package com.lexwilliam.company.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.model.CompanyDto
import com.lexwilliam.company.util.DeleteCompanyFailure
import com.lexwilliam.company.util.FetchCompanyFailure
import com.lexwilliam.company.util.UnknownFailure
import com.lexwilliam.company.util.UpsertCompanyFailure
import com.lexwilliam.firebase.utils.FirestoreConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

fun firebaseCompanyRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) = object : CompanyRepository {
    override fun observeCompany(companyUUID: String): Flow<Company> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_COMPANY)
                    .document(companyUUID)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(CompanyDto().toDomain())
                    }

                    trySend(
                        value
                            ?.toObject(CompanyDto::class.java)
                            ?.toDomain()
                            ?: CompanyDto().toDomain()
                    )
                }

            awaitClose { registration.remove() }
        }

    override suspend fun fetchCompany(companyUUID: String): Either<FetchCompanyFailure, Company> {
        return Either.catch {
            val documentSnapshot =
                store
                    .collection(FirestoreConfig.COLLECTION_COMPANY)
                    .document(companyUUID)
                    .get()
                    .await()

            if (documentSnapshot.exists()) {
                val company =
                    documentSnapshot.toObject(CompanyDto::class.java)
                        ?.toDomain()
                return company?.right() ?: FetchCompanyFailure.CompanyIsNull.left()
            } else {
                return FetchCompanyFailure.DocumentNotFound.left()
            }
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun upsertCompany(company: Company): Either<UpsertCompanyFailure, Company> {
        return Either.catch {
            store
                .collection(FirestoreConfig.COLLECTION_COMPANY)
                .document(company.uuid)
                .set(CompanyDto.fromDomain(company))
            company
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }

    override suspend fun deleteCompany(company: Company): Either<DeleteCompanyFailure, Company> {
        TODO("Not yet implemented")
    }
}