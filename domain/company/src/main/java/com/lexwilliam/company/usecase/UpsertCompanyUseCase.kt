package com.lexwilliam.company.usecase

import arrow.core.Either
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.repository.CompanyRepository
import com.lexwilliam.company.util.UpsertCompanyFailure
import javax.inject.Inject

class UpsertCompanyUseCase
    @Inject
    constructor(
        private val companyRepository: CompanyRepository
    ) {
        suspend operator fun invoke(company: Company): Either<UpsertCompanyFailure, Company> {
            return companyRepository.upsertCompany(company)
        }
    }