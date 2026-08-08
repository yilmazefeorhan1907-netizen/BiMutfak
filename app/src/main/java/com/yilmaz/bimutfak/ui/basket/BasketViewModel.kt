package com.yilmaz.bimutfak.ui.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.data.repository.BasketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Bi’Sepet ekranının durumunu ve kullanıcı işlemlerini yönetir.
@HiltViewModel
class BasketViewModel @Inject constructor(
    private val basketRepository: BasketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BasketUiState()
    )

    val uiState: StateFlow<BasketUiState> =
        _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun onEvent(event: BasketEvent) {
        when (event) {
            is BasketEvent.NameChanged -> {
                _uiState.update {
                    it.copy(
                        name = event.name,
                        errorMessageResId = null
                    )
                }
            }

            is BasketEvent.QuantityChanged -> {
                _uiState.update {
                    it.copy(
                        quantity = event.quantity,
                        errorMessageResId = null
                    )
                }
            }

            is BasketEvent.UnitChanged -> {
                _uiState.update {
                    it.copy(
                        unit = event.unit,
                        errorMessageResId = null
                    )
                }
            }

            is BasketEvent.ItemCheckedChanged -> {
                setItemChecked(
                    itemId = event.itemId,
                    checked = event.checked
                )
            }

            is BasketEvent.DeleteItemClicked -> {
                deleteItem(event.itemId)
            }

            BasketEvent.AddItemRequested -> {
                _uiState.update {
                    it.copy(
                        isAddItemDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            BasketEvent.AddItemDismissed -> {
                dismissAddItemDialog()
            }

            BasketEvent.SaveItemClicked -> {
                saveItem()
            }

            BasketEvent.RetryClicked -> {
                loadItems()
            }

            BasketEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        errorMessageResId = null
                    )
                }
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                val items = basketRepository.getItems()

                _uiState.update {
                    it.copy(
                        items = items,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId =
                            R.string.basket_error_load
                    )
                }
            }
        }
    }

    private fun saveItem() {
        val state = _uiState.value
        val validationError = validateForm(state)

        if (validationError != null) {
            _uiState.update {
                it.copy(
                    errorMessageResId = validationError
                )
            }
            return
        }

        if (state.isSaving) return

        val quantity = state.quantity
            .replace(',', '.')
            .toDoubleOrNull()
            ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessageResId = null
                )
            }

            try {
                val savedItem = basketRepository.addItem(
                    name = state.name,
                    quantity = quantity,
                    unit = state.unit
                )

                _uiState.update {
                    it.copy(
                        items = listOf(savedItem) + it.items,
                        name = "",
                        quantity = "1",
                        unit = "adet",
                        isSaving = false,
                        isAddItemDialogVisible = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessageResId =
                            R.string.basket_error_save
                    )
                }
            }
        }
    }

    private fun setItemChecked(
        itemId: String,
        checked: Boolean
    ) {
        val state = _uiState.value

        if (state.processingItemId != null) return

        val item = state.items.firstOrNull {
            it.id == itemId
        } ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingItemId = itemId,
                    errorMessageResId = null
                )
            }

            try {
                val updatedItem =
                    basketRepository.setItemChecked(
                        item = item,
                        checked = checked
                    )

                _uiState.update {
                    it.copy(
                        items = it.items.map { currentItem ->
                            if (currentItem.id == updatedItem.id) {
                                updatedItem
                            } else {
                                currentItem
                            }
                        },
                        processingItemId = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        processingItemId = null,
                        errorMessageResId =
                            R.string.basket_error_update
                    )
                }
            }
        }
    }

    private fun deleteItem(
        itemId: String
    ) {
        if (_uiState.value.processingItemId != null) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    processingItemId = itemId,
                    errorMessageResId = null
                )
            }

            try {
                basketRepository.deleteItem(itemId)

                _uiState.update {
                    it.copy(
                        items = it.items.filterNot { item ->
                            item.id == itemId
                        },
                        processingItemId = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        processingItemId = null,
                        errorMessageResId =
                            R.string.basket_error_delete
                    )
                }
            }
        }
    }

    private fun dismissAddItemDialog() {
        if (_uiState.value.isSaving) return

        _uiState.update {
            it.copy(
                name = "",
                quantity = "1",
                unit = "adet",
                isAddItemDialogVisible = false,
                errorMessageResId = null
            )
        }
    }

    private fun validateForm(
        state: BasketUiState
    ): Int? {
        val quantity = state.quantity
            .replace(',', '.')
            .toDoubleOrNull()

        return when {
            state.name.isBlank() ->
                R.string.basket_error_name_empty

            quantity == null || quantity <= 0.0 ->
                R.string.basket_error_quantity_invalid

            state.unit.isBlank() ->
                R.string.basket_error_unit_empty

            else -> null
        }
    }
}