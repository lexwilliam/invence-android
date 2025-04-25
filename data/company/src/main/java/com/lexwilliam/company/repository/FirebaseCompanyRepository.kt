package com.lexwilliam.company.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.model.CompanyDto
import com.lexwilliam.company.model.CompanyInviteRequest
import com.lexwilliam.company.util.DeleteCompanyFailure
import com.lexwilliam.company.util.FetchCompanyFailure
import com.lexwilliam.company.util.InviteCompanyFailure
import com.lexwilliam.company.util.UnknownFailure
import com.lexwilliam.company.util.UpsertCompanyFailure
import com.lexwilliam.firebase.utils.FirestoreConfig
import com.lexwilliam.user.model.User
import com.lexwilliam.user.repository.UserRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock

fun firebaseCompanyRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore,
    userRepository: UserRepository
) = object : CompanyRepository {
    override fun observeCompany(companyUUID: String): Flow<Company?> =
        callbackFlow {
            val reference =
                store
                    .collection(FirestoreConfig.COLLECTION_COMPANY)
                    .document(companyUUID)

            val registration =
                reference.addSnapshotListener { value, error ->
                    error?.let { exception ->
                        analytics.recordException(exception)
                        trySend(null)
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

    override suspend fun fetchCurrentCompany(): Either<FetchCompanyFailure, Company> {
        return Either.catch {
            val companyUUID: String? =
                userRepository.fetchCurrentUser().getOrNull()?.companyUUID?.toString()
            if (companyUUID == null) return FetchCompanyFailure.CompanyIsNull.left()
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

    override suspend fun sendInvite(
        company: Company,
        user: User
    ): Either<InviteCompanyFailure, Company> {
        return Either.catch {
            val modifiedCompany =
                company.copy(
                    inviteRequest =
                        company.inviteRequest +
                            CompanyInviteRequest(
                                userId = user.uuid,
                                email = user.email,
                                imageUrl = user.imageUrl,
                                createdAt = Clock.System.now()
                            )
                )
            upsertCompany(modifiedCompany)
            modifiedCompany
        }.mapLeft { t ->
            t.printStackTrace()
            analytics.recordException(t)
            UnknownFailure(t.message)
        }
    }
}