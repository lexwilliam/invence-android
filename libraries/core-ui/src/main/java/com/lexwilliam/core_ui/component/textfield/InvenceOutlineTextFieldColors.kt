package com.lexwilliam.core_ui.component.textfield

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lexwilliam.core_ui.theme.InvenceTheme

@Composable
fun OutlinedTextFieldDefaults.invenceTextFieldColors(
    textColor: Color = InvenceTheme.colors.neutral100,
    unfocusedTextColor: Color = InvenceTheme.colors.neutral70,
    disabledTextColor: Color = InvenceTheme.colors.neutral60,
    cursorColor: Color = InvenceTheme.colors.primary,
    errorCursorColor: Color = InvenceTheme.colors.error,
    focusedLeadingIconColor: Color = InvenceTheme.colors.primary,
    disabledLeadingIconColor: Color = InvenceTheme.colors.neutral60,
    unfocusedLeadingIconColor: Color = InvenceTheme.colors.neutral70,
    errorLeadingIconColor: Color = InvenceTheme.colors.neutral70,
    focusedTrailingIconColor: Color = InvenceTheme.colors.neutral70,
    unfocusedTrailingIconColor: Color = InvenceTheme.colors.neutral70,
    disabledTrailingIconColor: Color = InvenceTheme.colors.neutral60,
    errorTrailingIconColor: Color = InvenceTheme.colors.neutral70,
    containerColor: Color = InvenceTheme.colors.neutral10,
    focusedLabelColor: Color = InvenceTheme.colors.neutral100,
    unfocusedLabelColor: Color = InvenceTheme.colors.neutral60,
    disabledLabelColor: Color = InvenceTheme.colors.neutral60,
    errorLabelColor: Color = InvenceTheme.colors.neutral100,
    placeholderColor: Color = InvenceTheme.colors.neutral70,
    disabledPlaceholderColor: Color = InvenceTheme.colors.neutral60,
    focusedSupportingTextColor: Color = InvenceTheme.colors.neutral60,
    unfocusedSupportingTextColor: Color = InvenceTheme.colors.neutral60,
    disabledSupportingTextColor: Color = InvenceTheme.colors.neutral60,
    errorSupportingTextColor: Color = InvenceTheme.colors.neutral60,
    focusedBorderColor: Color = InvenceTheme.colors.primary,
    unfocusedBorderColor: Color = InvenceTheme.colors.neutral70,
    disabledBorderColor: Color = InvenceTheme.colors.neutral30,
    errorBorderColor: Color = InvenceTheme.colors.error,
    selectionColors: TextSelectionColors =
        TextSelectionColors(
            handleColor = InvenceTheme.colors.primary,
            backgroundColor = InvenceTheme.colors.primary
        )
): TextFieldColors {
    return colors(
        disabledTextColor = disabledTextColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        focusedLeadingIconColor = focusedLeadingIconColor,
        unfocusedLeadingIconColor = unfocusedLeadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = focusedTrailingIconColor,
        unfocusedTrailingIconColor = unfocusedTrailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        focusedSupportingTextColor = focusedSupportingTextColor,
        unfocusedSupportingTextColor = unfocusedSupportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor,
        selectionColors = selectionColors,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        disabledBorderColor = disabledBorderColor,
        errorBorderColor = errorBorderColor,
        focusedTextColor = textColor,
        unfocusedTextColor = unfocusedTextColor,
        unfocusedPlaceholderColor = placeholderColor,
        errorPlaceholderColor = placeholderColor,
        focusedPlaceholderColor = placeholderColor,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        errorContainerColor = containerColor
    )
}