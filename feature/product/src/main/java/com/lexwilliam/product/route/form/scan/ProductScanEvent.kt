package com.lexwilliam.inventory.scan

import android.graphics.RectF

sealed interface ProductScanEvent {
    data object Dismiss : ProductScanEvent

    data class ScanningAreaReady(val scanningArea: RectF) : ProductScanEvent

    data class CameraBoundaryReady(val cameraBoundary: RectF) : ProductScanEvent

    data class BottomSheetDialogStateChanged(val expanded: Boolean) : ProductScanEvent

    data object BottomSheetDismiss : ProductScanEvent

    data object ConfirmClicked : ProductScanEvent
}