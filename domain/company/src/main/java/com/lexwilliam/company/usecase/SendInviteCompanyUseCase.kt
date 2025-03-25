package com.lexwilliam.company.usecase

import arrow.core.Either
import com.lexwilliam.company.model.Company
import com.lexwilliam.company.repository.CompanyRepository
import com.lexwilliam.company.util.InviteCompanyFailure
import com.lexwilliam.user.model.User
import javax.inject.Inject

class SendInviteCompanyUseCase
    @Inject
    constructor(
        private val companyRepository: CompanyRepository
    ) {
        suspend operator fun invoke(
            company: Company,
            user: User
        ): Either<InviteCompanyFailure, Company> {
            return companyRepository.sendInvite(company, user)
        }
    }