package com.lexwilliam.core_ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val Primary = Color(0xFF002F34)
internal val Secondary = Color(0xFFD0EFEF)
internal val Tertiary = Color(0xFFFFF4EA)

internal val Success = Color(0xFF3C9F3E)
internal val Error = Color(0xFFED4330)
internal val Danger = Color(0xFFED8B30)

internal val Neutral10 = Color(0xFFFFFFFF)
internal val Neutral20 = Color(0xFFF5F5F5)
internal val Neutral30 = Color(0xFFEDEDED)
internal val Neutral40 = Color(0xFFE0E0E0)
internal val Neutral50 = Color(0xFFC2C2C2)
internal val Neutral60 = Color(0xFF9E9E9E)
internal val Neutral70 = Color(0xFF757575)
internal val Neutral80 = Color(0xFF616161)
internal val Neutral90 = Color(0xFF404040)
internal val Neutral100 = Color(0xFF0A0A0A)

internal val InvenceLightColors = InvenceColors(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,

    success = Success,
    error = Error,
    danger = Danger,

    neutral10 = Neutral10,
    neutral20 = Neutral20,
    neutral30 = Neutral30,
    neutral40 = Neutral40,
    neutral50 = Neutral50,
    neutral60 = Neutral60,
    neutral70 = Neutral70,
    neutral80 = Neutral80,
    neutral90 = Neutral90,
    neutral100 = Neutral100,
)

@Immutable
data class InvenceColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,

    val success: Color,
    val error: Color,
    val danger: Color,

    val neutral10: Color,
    val neutral20: Color,
    val neutral30: Color,
    val neutral40: Color,
    val neutral50: Color,
    val neutral60: Color,
    val neutral70: Color,
    val neutral80: Color,
    val neutral90: Color,
    val neutral100: Color,
)


internal val LocalInvenceColors = staticCompositionLocalOf {
    InvenceColors(
        primary = Color.Unspecified,
        secondary = Color.Unspecified,
        tertiary = Color.Unspecified,

        success = Color.Unspecified,
        error = Color.Unspecified,
        danger = Color.Unspecified,

        neutral10 = Color.Unspecified,
        neutral20 = Color.Unspecified,
        neutral30 = Color.Unspecified,
        neutral40 = Color.Unspecified,
        neutral50 = Color.Unspecified,
        neutral60 = Color.Unspecified,
        neutral70 = Color.Unspecified,
        neutral80 = Color.Unspecified,
        neutral90 = Color.Unspecified,
        neutral100 = Color.Unspecified,
    )
}