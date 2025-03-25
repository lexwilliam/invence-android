import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.lexwilliam.core_ui.theme.InvenceTheme
import kotlin.math.max
import kotlin.math.min

@Composable
fun InvenceSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = InvenceSnackbarDefaults.shape,
    containerColor: Color =
        when (snackbarData.visuals.message.split("_")[0]) {
            "SUCCESS" -> InvenceSnackbarDefaults.successColor
            else -> InvenceSnackbarDefaults.errorColor
        },
    contentColor: Color = InvenceSnackbarDefaults.contentColor,
    borderColor: Color =
        when (snackbarData.visuals.message.split("_")[0]) {
            "SUCCESS" -> InvenceSnackbarDefaults.successContentColor
            else -> InvenceSnackbarDefaults.errorContentColor
        },
    actionColor: Color =
        when (snackbarData.visuals.message.split("_")[0]) {
            "SUCCESS" -> InvenceSnackbarDefaults.successContentColor
            else -> InvenceSnackbarDefaults.errorContentColor
        },
    actionContentColor: Color =
        when (snackbarData.visuals.message.split("_")[0]) {
            "SUCCESS" -> InvenceSnackbarDefaults.successContentColor
            else -> InvenceSnackbarDefaults.errorContentColor
        },
    dismissActionContentColor: Color =
        when (snackbarData.visuals.message.split("_")[0]) {
            "SUCCESS" -> InvenceSnackbarDefaults.successContentColor
            else -> InvenceSnackbarDefaults.errorContentColor
        }
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? =
        if (actionLabel != null) {
            @Composable {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = actionColor),
                    onClick = { snackbarData.performAction() },
                    content = { Text(actionLabel) }
                )
            }
        } else {
            null
        }
    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            @Composable {
                IconButton(
                    onClick = { snackbarData.dismiss() },
                    content = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Dismiss"
                        )
                    }
                )
            }
        } else {
            null
        }
    Snackbar(
        modifier =
            modifier
                .padding(12.dp)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        action = actionComposable,
        dismissAction = dismissActionComposable,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        content = {
            val message = snackbarData.visuals.message.split("_")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (message[0]) {
                    "SUCCESS" ->
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = InvenceSnackbarDefaults.successContentColor
                        )

                    "ERROR" ->
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = "Error",
                            tint = InvenceSnackbarDefaults.errorContentColor
                        )
                    else -> {}
                }
                Text(message[1], style = InvenceTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun NewLineButtonSnackbar(
    text: @Composable () -> Unit,
    action: @Composable () -> Unit,
    dismissAction: @Composable (() -> Unit)?,
    actionTextStyle: TextStyle,
    actionContentColor: Color,
    dismissActionContentColor: Color
) {
    Column(
        modifier =
            Modifier
                // Fill max width, up to ContainerMaxWidth.
                .widthIn(max = ContainerMaxWidth)
                .fillMaxWidth()
                .padding(
                    start = HorizontalSpacing,
                    bottom = SeparateButtonExtraY
                )
    ) {
        Box(
            Modifier
                .paddingFromBaseline(HeightToFirstLine, LongButtonVerticalOffset)
                .padding(end = HorizontalSpacingButtonSide)
        ) { text() }

        Box(
            Modifier
                .align(Alignment.End)
                .padding(end = if (dismissAction == null) HorizontalSpacingButtonSide else 0.dp)
        ) {
            Row {
                CompositionLocalProvider(
                    LocalContentColor provides actionContentColor,
                    LocalTextStyle provides actionTextStyle,
                    content = action
                )
                if (dismissAction != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides dismissActionContentColor,
                        content = dismissAction
                    )
                }
            }
        }
    }
}

@Composable
private fun OneRowSnackbar(
    text: @Composable () -> Unit,
    action: @Composable (() -> Unit)?,
    dismissAction: @Composable (() -> Unit)?,
    actionTextStyle: TextStyle,
    actionTextColor: Color,
    dismissActionColor: Color
) {
    val textTag = "text"
    val actionTag = "action"
    val dismissActionTag = "dismissAction"
    Layout(
        {
            Box(
                Modifier
                    .layoutId(textTag)
                    .padding(vertical = SnackbarVerticalPadding)
            ) { text() }
            if (action != null) {
                Box(Modifier.layoutId(actionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides actionTextColor,
                        LocalTextStyle provides actionTextStyle,
                        content = action
                    )
                }
            }
            if (dismissAction != null) {
                Box(Modifier.layoutId(dismissActionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides dismissActionColor,
                        content = dismissAction
                    )
                }
            }
        },
        modifier =
            Modifier.padding(
                start = HorizontalSpacing,
                end = if (dismissAction == null) HorizontalSpacingButtonSide else 0.dp
            )
    ) { measurables, constraints ->
        val containerWidth = min(constraints.maxWidth, ContainerMaxWidth.roundToPx())
        val actionButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == actionTag }?.measure(constraints)
        val dismissButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == dismissActionTag }?.measure(constraints)
        val actionButtonWidth = actionButtonPlaceable?.width ?: 0
        val actionButtonHeight = actionButtonPlaceable?.height ?: 0
        val dismissButtonWidth = dismissButtonPlaceable?.width ?: 0
        val dismissButtonHeight = dismissButtonPlaceable?.height ?: 0
        val extraSpacingWidth = if (dismissButtonWidth == 0) TextEndExtraSpacing.roundToPx() else 0
        val textMaxWidth =
            (containerWidth - actionButtonWidth - dismissButtonWidth - extraSpacingWidth)
                .coerceAtLeast(constraints.minWidth)
        val textPlaceable =
            measurables.fastFirst { it.layoutId == textTag }.measure(
                constraints.copy(minHeight = 0, maxWidth = textMaxWidth)
            )

        val firstTextBaseline = textPlaceable[FirstBaseline]
        require(firstTextBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        val lastTextBaseline = textPlaceable[LastBaseline]
        require(lastTextBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        val isOneLine = firstTextBaseline == lastTextBaseline
        val dismissButtonPlaceX = containerWidth - dismissButtonWidth
        val actionButtonPlaceX = dismissButtonPlaceX - actionButtonWidth

        val textPlaceY: Int
        val containerHeight: Int
        val actionButtonPlaceY: Int
        if (isOneLine) {
            val minContainerHeight = 52.dp.roundToPx()
            val contentHeight = max(actionButtonHeight, dismissButtonHeight)
            containerHeight = max(minContainerHeight, contentHeight)
            textPlaceY = (containerHeight - textPlaceable.height) / 2
            actionButtonPlaceY =
                if (actionButtonPlaceable != null) {
                    actionButtonPlaceable[FirstBaseline].let {
                        if (it != AlignmentLine.Unspecified) {
                            textPlaceY + firstTextBaseline - it
                        } else {
                            0
                        }
                    }
                } else {
                    0
                }
        } else {
            val baselineOffset = HeightToFirstLine.roundToPx()
            textPlaceY = baselineOffset - firstTextBaseline
            val minContainerHeight = 80.dp.roundToPx()
            val contentHeight = textPlaceY + textPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            actionButtonPlaceY =
                if (actionButtonPlaceable != null) {
                    (containerHeight - actionButtonPlaceable.height) / 2
                } else {
                    0
                }
        }
        val dismissButtonPlaceY =
            if (dismissButtonPlaceable != null) {
                (containerHeight - dismissButtonPlaceable.height) / 2
            } else {
                0
            }

        layout(containerWidth, containerHeight) {
            textPlaceable.placeRelative(0, textPlaceY)
            dismissButtonPlaceable?.placeRelative(dismissButtonPlaceX, dismissButtonPlaceY)
            actionButtonPlaceable?.placeRelative(actionButtonPlaceX, actionButtonPlaceY)
        }
    }
}

/**
 * Contains the default values used for [Snackbar].
 */
object InvenceSnackbarDefaults {
    /** Default shape of a snackbar. */
    val shape: Shape @Composable get() = RoundedCornerShape(12.dp)

    /** Default color of a snackbar. */
    val successColor: Color @Composable get() = InvenceTheme.colors.primary

    val successContentColor: Color @Composable get() = InvenceTheme.colors.neutral10

    val errorColor: Color @Composable get() = InvenceTheme.colors.error

    val errorContentColor: Color @Composable get() = InvenceTheme.colors.neutral10

    /** Default content color of a snackbar. */
    val contentColor: Color @Composable get() = InvenceTheme.colors.neutral100

//    /** Default action color of a snackbar. */
//    val actionColor: Color @Composable get() = SnackbarTokens.ActionLabelTextColor.value
//
//    /** Default action content color of a snackbar. */
//    val actionContentColor: Color @Composable get() = SnackbarTokens.ActionLabelTextColor.value
//
//    /** Default dismiss action content color of a snackbar. */
//    val dismissActionContentColor: Color @Composable get() =
}

private val ContainerMaxWidth = 600.dp
private val HeightToFirstLine = 30.dp
private val HorizontalSpacing = 16.dp
private val HorizontalSpacingButtonSide = 8.dp
private val SeparateButtonExtraY = 2.dp
private val SnackbarVerticalPadding = 6.dp
private val TextEndExtraSpacing = 8.dp
private val LongButtonVerticalOffset = 12.dp