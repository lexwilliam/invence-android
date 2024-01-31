package com.lexwilliam.company.repository

import arrow.core.Either
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.model.CompanyDto
import com.lexwilliam.company.util.DeleteCompanyFailure
import com.lexwilliam.company.util.UnknownFailure
import com.lexwilliam.company.util.UpsertCompanyFailure
import com.lexwilliam.firebase.FirestoreConfig
import kotlinx.coroutines.flow.Flow

fun firebaseCompanyRepository(
    analytics: FirebaseCrashlytics,
    store: FirebaseFirestore
) =
    object : CompanyRepository {
        override fun observeCompany(): Flow<Company> {
            TODO("Not yet implemented")
        }

        override suspend fun upsertCompany(
            company: Company
        ): Either<UpsertCompanyFailure, Company> {
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

        override suspend fun deleteCompany(
            company: Company
        ): Either<DeleteCompanyFailure, Company> {
            TODO("Not yet implemented")
        }
    }