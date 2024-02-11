package com.lexwilliam.product.route.form

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barcode.model.BarcodeNavigationArg
import com.lexwilliam.core.extensions.addOrUpdateDuplicate
import com.lexwilliam.core.extensions.isFirebaseUri
import com.lexwilliam.core.model.UploadImageFormat
import com.lexwilliam.product.category.CategoryUiEvent
import com.lexwilliam.product.category.CategoryUiState
import com.lexwilliam.product.model.Product
import com.lexwilliam.product.model.ProductCategory
import com.lexwilliam.product.model.ProductItem
import com.lexwilliam.product.model.ProductWithImageFormat
import com.lexwilliam.product.model.UiPriceAndQuantity
import com.lexwilliam.product.navigation.ProductFormNavigationTarget
import com.lexwilliam.product.usecase.ClearTempProductUseCase
import com.lexwilliam.product.usecase.InsertTempProductUseCase
import com.lexwilliam.product.usecase.ObserveProductCategoryUseCase
import com.lexwilliam.product.usecase.ObserveTempProductUseCase
import com.lexwilliam.product.usecase.UploadProductCategoryUseCase
import com.lexwilliam.product.usecase.UploadProductImageUseCase
import com.lexwilliam.product.usecase.UpsertProductCategoryUseCase
import com.lexwilliam.user.usecase.FetchUserUseCase
import com.lexwilliam.user.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductFormViewModel
    @Inject
    constructor(
        observeProductCategory: ObserveProductCategoryUseCase,
        private val upsertProductCategory: UpsertProductCategoryUseCase,
        private val uploadProductImage: UploadProductImageUseCase,
        private val uploadProductCategoryImage: UploadProductCategoryUseCase,
        private val observeTempProduct: ObserveTempProductUseCase,
        private val insertTempProduct: InsertTempProductUseCase,
        private val clearTempProduct: ClearTempProductUseCase,
        observeSession: ObserveSessionUseCase,
        fetchUser: FetchUserUseCase,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {
        private val productUUIDFromArgs =
            savedStateHandle
                .getStateFlow<String?>("productUUID", null)

        private val onlyID =
            savedStateHandle
                .getStateFlow<Boolean?>("onlyID", null)
                .map { onlyID ->
                    val id = onlyID ?: false
                    if (id) {
                        val product = observeTempProduct().firstOrNull() ?: return@map false
                        val category =
                            categories
                                .firstOrNull()
                                ?.firstOrNull { category -> category.name == product.name }
                        _state.update { old -> old.copy(selectedCategory = category) }
                        updateProductState(product)
                    }
                    id
                }

        val productUUID =
            combine(
                productUUIDFromArgs,
                onlyID
            ) { productUUID, onlyID ->
                if (onlyID) {
                    observeTempProduct().firstOrNull()?.uuid
                } else {
                    productUUID
                }
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

        private val branchUUID =
            observeSession().map { session ->
                session.userUUID
                    ?.let { fetchUser(it) }
                    ?.getOrNull()
                    ?.branchUUID
            }

        private val categories =
            branchUUID.flatMapLatest {
                when (it) {
                    null -> flowOf(emptyList())
                    else -> observeProductCategory(it)
                }
            }

        private val category =
            categories.map { categories ->
                categories.firstOrNull { category ->
                    category.products.any { product ->
                        product.uuid == productUUID.firstOrNull()
                    }
                }
            }.onEach { category ->
                _state.update { old -> old.copy(selectedCategory = category) }
            }

        val product =
            combine(
                productUUID,
                category
            ) { productUUID, category ->
                productUUID?.let {
                    val product =
                        category?.products?.firstOrNull { product ->
                            product.uuid == productUUID
                        }
                    product?.let { updateProductState(it) } ?: Product()
                } ?: Product()
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                Product()
            )

        private fun updateProductState(product: Product) {
            _state.update {
                    old ->
                old.copy(
                    title = product.name,
                    image = product.imagePath?.let { UploadImageFormat.WithUri(it) },
                    sellPrice = product.sellPrice.toString(),
                    buyPriceList =
                        product.items.associate {
                                item ->
                            item.itemId to
                                UiPriceAndQuantity(
                                    item.buyPrice.toString(),
                                    item.quantity.toString()
                                )
                        },
                    description = product.description
                )
            }
        }

        private fun updateProductState(product: ProductWithImageFormat) {
            _state.update {
                    old ->
                old.copy(
                    title = product.name,
                    image = product.imagePath,
                    sellPrice = product.sellPrice.toString(),
                    buyPriceList =
                        product.items.associate {
                                item ->
                            item.itemId to
                                UiPriceAndQuantity(
                                    item.buyPrice.toString(),
                                    item.quantity.toString()
                                )
                        },
                    description = product.description
                )
            }
        }

        private val _state = MutableStateFlow(ProductFormUiState())
        val uiState =
            combine(
                onlyID,
                _state
            ) { _, state ->
                state
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ProductFormUiState()
            )

        private val _categoryState = MutableStateFlow<CategoryUiState?>(null)
        val categoryState = _categoryState.asStateFlow()

        private val _navigation = Channel<ProductFormNavigationTarget>()
        val navigation = _navigation.receiveAsFlow()

        fun onEvent(event: ProductFormUiEvent) {
            when (event) {
                is ProductFormUiEvent.BackStackClicked -> handleBackStackClicked()
                is ProductFormUiEvent.InputImageChanged -> handleInputImageChanged(event.uri)
                is ProductFormUiEvent.CameraClicked -> handleCameraClicked()
                is ProductFormUiEvent.ProductPhotoTaken -> handleProductPhotoTaken(event.bitmap)
                is ProductFormUiEvent.ScanBarcodeClicked -> handleScanBarcodeClicked()
                is ProductFormUiEvent.TitleValueChanged -> handleTitleValueChanged(event.value)
                ProductFormUiEvent.AddProductItem -> handleAddProductItem()
                is ProductFormUiEvent.BuyPriceValueChanged ->
                    handleBuyPriceChanged(
                        event.itemId,
                        event.value
                    )
                is ProductFormUiEvent.SellPriceValueChanged -> handleSellPriceChanged(event.value)
                is ProductFormUiEvent.QuantityValueChanged ->
                    handleQuantityChanged(
                        event.itemId,
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

        private fun handleCameraClicked() {
            _state.update { old -> old.copy(takePhoto = true) }
        }

        private fun handleProductPhotoTaken(bitmap: Bitmap) {
            _state.update { old ->
                old.copy(
                    image = UploadImageFormat.WithBitmap(bitmap),
                    takePhoto = false
                )
            }
        }

        private fun handleAddProductItem() {
            _state.update { old ->
                val emptyProductItem =
                    (old.buyPriceList.size + 1) to
                        UiPriceAndQuantity(price = "", quantity = "")
                old.copy(buyPriceList = old.buyPriceList + emptyProductItem)
            }
        }

        private fun handleInputImageChanged(uri: Uri?) {
            _state.update { old ->
                old.copy(image = uri?.let { UploadImageFormat.WithUri(it) })
            }
        }

        private fun handleBackStackClicked() {
            viewModelScope.launch {
                _navigation.send(ProductFormNavigationTarget.BackStack)
            }
        }

        private fun handleScanBarcodeClicked() {
            viewModelScope.launch {
                val product =
                    ProductWithImageFormat(
                        uuid = productUUID.value ?: "",
                        name = _state.value.title,
                        description = _state.value.description,
                        categoryName = _state.value.selectedCategory?.name ?: "",
                        sellPrice =
                            _state.value.sellPrice
                                .let { if (it == "") 0.0 else it.toDouble() },
                        items =
                            _state.value.buyPriceList.map { (id, item) ->
                                ProductItem(
                                    itemId = id,
                                    buyPrice =
                                        item.price
                                            .let { if (it == "") 0.0 else it.toDouble() },
                                    quantity =
                                        item.quantity
                                            .let { if (it == "") 0 else it.toInt() },
                                    createdAt = Clock.System.now()
                                )
                            },
                        imagePath = _state.value.image,
                        createdAt = Clock.System.now()
                    )
                Log.d("TAG", product.toString())
                insertTempProduct(product)
                _navigation.send(
                    ProductFormNavigationTarget.Barcode(BarcodeNavigationArg.PRODUCT_FORM)
                )
            }
        }

        private fun handleTitleValueChanged(value: String) {
            _state.update { old -> old.copy(title = value) }
        }

        private fun handleBuyPriceChanged(
            itemId: Int,
            value: String
        ) {
            _state.update { old ->
                old.copy(
                    buyPriceList =
                        old
                            .buyPriceList
                            .toMutableMap()
                            .apply {
                                this[itemId] =
                                    UiPriceAndQuantity(
                                        price = value,
                                        quantity = this[itemId]?.quantity ?: "0"
                                    )
                            }
                )
            }
        }

        private fun handleQuantityChanged(
            itemId: Int,
            value: String
        ) {
            _state.update { old ->
                old.copy(
                    buyPriceList =
                        old
                            .buyPriceList
                            .toMutableMap()
                            .apply {
                                this[itemId] =
                                    UiPriceAndQuantity(
                                        price = this[itemId]?.price ?: "0",
                                        quantity = value
                                    )
                            }
                )
            }
        }

        private fun handleSellPriceChanged(value: String) {
            _state.update { old -> old.copy(sellPrice = value) }
        }

        private fun handleSelectCategoryClicked() {
            viewModelScope.launch {
                _categoryState.update {
                    Log.d("TAG", categories.firstOrNull().toString())
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
            viewModelScope.launch {
                val category = _state.value.selectedCategory ?: return@launch
                val productUUID = productUUID.value ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                var product =
                    Product(
                        uuid = productUUID,
                        name = _state.value.title,
                        description = _state.value.description,
                        categoryName = category.name,
                        sellPrice = _state.value.sellPrice.toDouble(),
                        items =
                            _state.value.buyPriceList.map { (id, item) ->
                                ProductItem(
                                    itemId = id,
                                    buyPrice = item.price.toDouble(),
                                    quantity = item.quantity.toInt(),
                                    createdAt = Clock.System.now()
                                )
                            },
                        createdAt = Clock.System.now()
                    )
                if (_state.value.image != null && !_state.value.image.isFirebaseUri()) {
                    uploadProductImage(
                        branchUUID = branchUUID,
                        productUUID = product.uuid,
                        format = _state.value.image!!
                    ).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                        }
                    ) { uri ->
                        product = product.copy(imagePath = uri)
                    }
                }
                val format = _state.value.image
                if (format.isFirebaseUri()) {
                    when (format) {
                        is UploadImageFormat.WithUri ->
                            product = product.copy(imagePath = format.uri)
                        else -> {}
                    }
                }
                val modifiedCategory =
                    category
                        .copy(
                            products =
                                onlyID.firstOrNull()
                                    ?.let {
                                        category.products
                                            .addOrUpdateDuplicate(product) { e, n ->
                                                Log.d("TAG", "${e.name} ${n.name}")
                                                e.name == n.name
                                            }
                                    } ?: category.products
                                    .addOrUpdateDuplicate(product) { e, n ->
                                        e.uuid == n.uuid
                                    }
                        )
                Log.d("TAG", modifiedCategory.toString())
                upsertProductCategory(modifiedCategory).fold(
                    ifLeft = { failure ->
                        Log.d("TAG", failure.toString())
                    },
                    ifRight = {
                        clearTempProduct()
                        _navigation.send(ProductFormNavigationTarget.Inventory)
                    }
                )
            }
        }

        fun onCategoryDialogEvent(event: CategoryUiEvent) {
            when (event) {
                is CategoryUiEvent.QueryChanged -> handleCategoryQueryChanged(event.value)
                is CategoryUiEvent.CategoryClicked -> handleCategoryClicked(event.item)
                CategoryUiEvent.Dismiss -> handleCategoryDismiss()
                is CategoryUiEvent.InputImageChanged -> handleCategoryInputImageChanged(event.uri)
                is CategoryUiEvent.ShowForm -> handleCategoryShowForm(event.show)
                is CategoryUiEvent.AddCategoryCameraClicked -> handleAddCategoryCameraClicked()
                is CategoryUiEvent.AddCategoryPhotoTaken ->
                    handleAddCategoryPhotoTaken(
                        event.bitmap
                    )
                is CategoryUiEvent.AddCategoryTitleChanged ->
                    handleAddCategoryNameChanged(
                        event.value
                    )
                CategoryUiEvent.AddCategoryConfirm -> handleAddCategoryConfirm()
            }
        }

        private fun handleAddCategoryPhotoTaken(bitmap: Bitmap) {
            _categoryState.update { old ->
                old?.copy(
                    formImagePath = UploadImageFormat.WithBitmap(bitmap),
                    takePhoto = false
                )
            }
        }

        private fun handleAddCategoryCameraClicked() {
            _categoryState.update { old -> old?.copy(takePhoto = true) }
        }

        private fun handleCategoryShowForm(show: Boolean) {
            _categoryState.update { old -> old?.copy(isFormShown = show) }
        }

        private fun handleCategoryDismiss() {
            _categoryState.update { null }
        }

        private fun handleCategoryClicked(category: ProductCategory) {
            _state.update {
                    old ->
                old.copy(selectedCategory = category)
            }
            _categoryState.update { null }
        }

        private fun handleAddCategoryConfirm() {
            viewModelScope.launch {
                val categoryState = _categoryState.value ?: return@launch
                val branchUUID = branchUUID.firstOrNull() ?: return@launch
                val categoryUUID = UUID.randomUUID()
                val category =
                    ProductCategory(
                        uuid = categoryUUID,
                        branchUUID = branchUUID,
                        name = categoryState.formTitle,
                        createdAt = Clock.System.now()
                    )
                if (categoryState.formImagePath != null) {
                    uploadProductCategoryImage(
                        branchUUID = branchUUID,
                        categoryUUID = categoryUUID,
                        format = categoryState.formImagePath
                    ).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                        },
                        ifRight = { uri ->
                            upsertProductCategory(category.copy(imageUrl = uri)).fold(
                                ifLeft = { failure ->
                                    Log.d("TAG", failure.toString())
                                },
                                ifRight = {
                                    _categoryState.update { null }
                                }
                            )
                        }
                    )
                } else {
                    upsertProductCategory(category).fold(
                        ifLeft = { failure ->
                            Log.d("TAG", failure.toString())
                        },
                        ifRight = {
                            _categoryState.update { null }
                        }
                    )
                }
            }
        }

        private fun handleAddCategoryNameChanged(value: String) {
            _categoryState.update { old -> old?.copy(formTitle = value) }
        }

        private fun handleCategoryInputImageChanged(uri: Uri?) {
            _categoryState.update { old ->
                old?.copy(formImagePath = uri?.let { UploadImageFormat.WithUri(it) })
            }
        }

        private fun handleCategoryQueryChanged(value: String) {
            _categoryState.update { old -> old?.copy(query = value) }
        }
    }