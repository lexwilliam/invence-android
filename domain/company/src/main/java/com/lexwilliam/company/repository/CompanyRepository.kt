package com.lexwilliam.company.repository

import arrow.core.Either
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.util.DeleteCompanyFailure
import com.lexwilliam.company.util.UpsertCompanyFailure
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun observeCompany(): Flow<Company>

    suspend fun upsertCompany(company: Company): Either<UpsertCompanyFailure, Company>

    suspend fun deleteCompany(company: Company): Either<DeleteCompanyFailure, Company>
}