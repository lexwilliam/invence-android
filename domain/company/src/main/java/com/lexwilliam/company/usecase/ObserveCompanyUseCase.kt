package com.lexwilliam.company.usecase

import com.lexwilliam.company.model.Company
import com.lexwilliam.company.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCompanyUseCase
    @Inject
    constructor(
        private val companyRepository: CompanyRepository
    ) {
        operator fun invoke(companyUUID: String): Flow<Company?> {
            return companyRepository.observeCompany(companyUUID)
        }
    }