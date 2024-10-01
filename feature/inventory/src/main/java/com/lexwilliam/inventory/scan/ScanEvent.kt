package com.lexwilliam.inventory.scan

import android.graphics.RectF

sealed interface ScanEvent {
    data object Dismiss : ScanEvent

    data class ScanningAreaReady(val scanningArea: RectF) : ScanEvent

    data class CameraBoundaryReady(val cameraBoundary: RectF) : ScanEvent

    data class BottomSheetDialogStateChanged(val expanded: Boolean) : ScanEvent

    data object BottomSheetDismiss : ScanEvent

    data object AddProductClicked : ScanEvent

    data class ProductDetailClicked(val productUUID: String) : ScanEvent
}