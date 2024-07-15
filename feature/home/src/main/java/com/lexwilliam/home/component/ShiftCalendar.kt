package com.lexwilliam.home.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.ContentHeightMode
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.lexwilliam.core_ui.theme.InvenceTheme
import com.lexwilliam.home.route.HomeUiState
import com.lexwilliam.user.model.EmployeeShift
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ShiftCalendar(
    modifier: Modifier = Modifier,
    state: HomeUiState,
    shift: EmployeeShift,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCheckIn: () -> Unit
) {
    val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)

    val calendarState =
        rememberCalendarState(
            startMonth = state.date.minusMonths(100),
            endMonth = state.date.plusMonths(100),
            firstVisibleMonth = state.date,
            firstDayOfWeek = daysOfWeek.first()
        )

    LaunchedEffect(state.date) {
        calendarState.animateScrollToMonth(state.date)
    }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(InvenceTheme.colors.primary, RoundedCornerShape(16.dp))
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Shift Calendar",
                style = InvenceTheme.typography.titleMedium,
                color = InvenceTheme.colors.neutral10
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onPrevious()
                }) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "previous date",
                        tint = InvenceTheme.colors.neutral10
                    )
                }
                Text(
                    text = state.date.toString(),
                    style = InvenceTheme.typography.titleMedium,
                    color = InvenceTheme.colors.neutral10
                )
                IconButton(onClick = onNext) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "next date",
                        tint = InvenceTheme.colors.neutral10
                    )
                }
            }
        }

        Row {
            HorizontalCalendar(
                modifier =
                    Modifier
                        .weight(1f),
                contentHeightMode = ContentHeightMode.Fill,
                state = calendarState,
                dayContent = { day ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            color =
                                if (day.position == DayPosition.MonthDate) {
                                    InvenceTheme.colors.neutral10
                                } else {
                                    InvenceTheme.colors.neutral70
                                },
                            style = InvenceTheme.typography.labelSmall
                        )
                        if (shift.shift.contains(day.toFormatString())) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(4.dp)
                                        .background(InvenceTheme.colors.secondary, CircleShape)
                            )
                        }
                    }
                },
                monthHeader = {
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                    Spacer(modifier = Modifier.size(12.dp))
                }
            )
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    containerColor = InvenceTheme.colors.secondary,
                    contentColor = InvenceTheme.colors.primary,
                    onClick = onCheckIn
                ) {
                    Icon(Icons.Default.Check, contentDescription = "daily check in icon")
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                style = InvenceTheme.typography.labelSmall,
                color = InvenceTheme.colors.neutral10,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
private fun CalendarDay.toFormatString(): String {
    val oldFormatter = SimpleDateFormat("yyyy-M-d")
    val newFormatter = SimpleDateFormat("yyyy-MM-dd")
    val date = oldFormatter.parse("${date.year}-${date.monthValue}-${date.dayOfMonth}")
    return date?.let { newFormatter.format(it) } ?: ""
}