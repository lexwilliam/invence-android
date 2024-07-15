package com.lexwilliam.home.route

import com.lexwilliam.core.extensions.toFormatString
import kotlinx.datetime.Clock
import java.time.YearMonth

data class HomeUiState(
    val date: YearMonth = YearMonth.now()
) {
    val today = Clock.System.now().toFormatString("yyyy-MM-dd")
}