package com.lexwilliam.product.route.form

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lexwilliam.product.category.CategoryUiEvent
import com.lexwilliam.product.category.CategoryUiState
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.UiCategory
import com.lexwilliam.product.navigation.ProductFormNavigationTarget
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFormViewModel
    @Inject
    constructor(
        observeProductCategoryUseCase: ObserveProductCategoryUseCase,
        private val upsertProductCategoryUseCase: UpsertProductCategoryUseCase
    ) : ViewModel() {
        private val _state = MutableStateFlow(ProductFormUiState())
        val uiState = _state.asStateFlow()

        private val _categoryState = MutableStateFlow<CategoryUiState?>(null)
        val categoryState = _categoryState.asStateFlow()

        private val categories = observeProductCategoryUseCase()

        private val _navigation = Channel<ProductFormNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: ProductFormUiEvent) {
            when (event) {
                is ProductFormUiEvent.BackStackClicked -> handleBackStackClicked()
                is ProductFormUiEvent.InputImageChanged -> handleInputImageChanged(event.uri)
                is ProductFormUiEvent.ScanBarcodeClicked -> handleScanBarcodeClicked()
                is ProductFormUiEvent.TitleValueChanged -> handleTitleValueChanged(event.value)
                is ProductFormUiEvent.BuyPriceValueChanged ->
                    handleBuyPriceValueChanged(
                        event.value
                    )
                is ProductFormUiEvent.SellPriceValueChanged -> handleSellPriceChanged(event.value)
                is ProductFormUiEvent.QuantityValueChanged ->
                    handleQuantityValueChanged(
                        event.value
                    )
                is ProductFormUiEvent.SelectCategoryClicked ->
                    handleSelectCategoryClicked()
                is ProductFormUiEvent.DescriptionValueChanged ->
                    handleDescriptionValueChanged(
                        event.value
                    )
                is ProductFormUiEvent.SaveClicked -> handleSaveClicked()
            }
        }

        private fun handleInputImageChanged(uri: Uri?) {
            _state.update { old -> old.copy(imagePath = uri) }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(ProductFormNavigationTarget.BackStackClicked)
            }
        }

        private fun handleScanBarcodeClicked() {
        }

        private fun handleTitleValueChanged(value: String) {
            _state.update { old -> old.copy(title = value) }
        }

        private fun handleBuyPriceValueChanged(value: String) {
            _state.update { old -> old.copy(buyPrice = value) }
        }

        private fun handleSellPriceChanged(value: String) {
            _state.update { old -> old.copy(sellPrice = value) }
        }

        private fun handleQuantityValueChanged(value: String) {
            if (value.toIntOrNull() != null) {
                _state.update { old -> old.copy(quantity = value.toInt()) }
            }
        }

        private fun handleSelectCategoryClicked() {
            viewModelScope.launch {
                _categoryState.update {
                    CategoryUiState(
                        categories = categories.firstOrNull() ?: emptyList()
                    )
                }
            }
        }

        private fun handleDescriptionValueChanged(value: String) {
            _state.update { old -> old.copy(description = value) }
        }

        private fun handleSaveClicked() {
        }

        fun onCategoryDialogEvent(event: CategoryUiEvent) {
            when (event) {
                is CategoryUiEvent.QueryChanged -> handleCategoryQueryChanged(event.value)
                is CategoryUiEvent.CategoryClicked -> handleCategoryClicked(event.item)
                CategoryUiEvent.Dismiss -> handleCategoryDismiss()
                is CategoryUiEvent.InputImageChanged -> handleCategoryInputImageChanged(event.uri)
                is CategoryUiEvent.AddCategoryTitleChanged ->
                    handleAddCategoryNameChanged(
                        event.value
                    )
                CategoryUiEvent.AddCategoryConfirm -> handleAddCategoryConfirm()
            }
        }

        private fun handleCategoryDismiss() {
            _categoryState.update { null }
        }

        private fun handleCategoryClicked(category: ProductCategory) {
            _state.update {
                    old ->
                old.copy(selectedCategory = UiCategory(category.uuid, category.name))
            }
        }

        private fun handleAddCategoryConfirm() {
            TODO("Not yet implemented")
        }

        private fun handleAddCategoryNameChanged(value: String) {
            _categoryState.update { old -> old?.copy(formTitle = value) }
        }

        private fun handleCategoryInputImageChanged(uri: Uri?) {
            _categoryState.update { old ->
                Log.d("TAG1", uri.toString())
                old?.copy(formImagePath = uri)
            }
        }

        private fun handleCategoryQueryChanged(value: String) {
            _categoryState.update { old -> old?.copy(query = value) }
        }
    }