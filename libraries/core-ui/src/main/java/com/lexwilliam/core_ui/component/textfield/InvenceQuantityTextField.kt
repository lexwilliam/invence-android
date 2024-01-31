package com.lexwilliam.core_ui.component.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.lexwilliam.core_ui.theme.InvenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvenceQuantityTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = InvenceTheme.typography.bodyLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.invenceTextFieldColors()
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier,
        readOnly = readOnly,
        interactionSource = interactionSource,
        enabled = enabled,
        singleLine = singleLine,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        maxLines = maxLines,
        minLines = minLines
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            innerTextField = {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InvenceTextFieldQuantityIndicator(
                        containerColor = InvenceTheme.colors.primary,
                        contentColor = InvenceTheme.colors.neutral10,
                        title = "-5",
                        onClick = { onValueChange((value.toInt() - 5).toString()) }
                    )
                    InvenceTextFieldQuantityIndicator(
                        containerColor = InvenceTheme.colors.secondary,
                        contentColor = InvenceTheme.colors.primary,
                        title = "-1",
                        onClick = { onValueChange((value.toInt() - 1).toString()) }
                    )
                    Row(
                        modifier = Modifier.width(IntrinsicSize.Min)
                    ) {
                        innerTextField()
                    }
                    InvenceTextFieldQuantityIndicator(
                        containerColor = InvenceTheme.colors.secondary,
                        contentColor = InvenceTheme.colors.primary,
                        title = "+1",
                        onClick = { onValueChange((value.toInt() + 1).toString()) }
                    )
                    InvenceTextFieldQuantityIndicator(
                        containerColor = InvenceTheme.colors.primary,
                        contentColor = InvenceTheme.colors.neutral10,
                        title = "+5",
                        onClick = { onValueChange((value.toInt() + 5).toString()) }
                    )
                }
            },
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            isError = isError,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = supportingText,
            colors = colors,
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled,
                    isError,
                    interactionSource,
                    colors,
                    shape
                )
            }
        )
    }
}

@Preview
@Composable
fun InvenceQuantityTextFieldPreview() {
    InvenceQuantityTextField(value = "10", onValueChange = {})
}