package com.example.barcode.di

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import com.example.barcode.analyzer.BarcodeImageAnalyzer
import com.example.barcode.analyzer.BarcodeResultBoundaryAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanModule {
    @OptIn(ExperimentalGetImage::class)
    @Singleton
    @Provides
    fun providesBarcodeAnalyzer(barcodeScanner: BarcodeScanner): BarcodeImageAnalyzer =
        BarcodeImageAnalyzer(barcodeScanner)

    @Singleton
    @Provides
    fun provideBarcodeScanner(): BarcodeScanner {
        val options =
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_CODE_128,
                    Barcode.FORMAT_CODE_39,
                    Barcode.FORMAT_CODE_93,
                    Barcode.FORMAT_CODABAR,
                    Barcode.FORMAT_DATA_MATRIX,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_ITF,
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_PDF417,
                    Barcode.FORMAT_AZTEC,
                    Barcode.TYPE_ISBN
                )
                .build()
        return BarcodeScanning.getClient(options)
    }

    @Singleton
    @Provides
    fun provideBarcodeResultBoundaryAnalyzer(): BarcodeResultBoundaryAnalyzer {
        return BarcodeResultBoundaryAnalyzer()
    }
}