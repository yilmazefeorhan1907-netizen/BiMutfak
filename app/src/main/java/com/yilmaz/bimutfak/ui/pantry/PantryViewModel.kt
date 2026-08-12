package com.yilmaz.bimutfak.ui.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.model.PantrySection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.usecase.pantry.AddPantryItemUseCase
import com.yilmaz.bimutfak.domain.usecase.pantry.DeletePantryItemUseCase
import com.yilmaz.bimutfak.domain.usecase.pantry.GetPantryItemsUseCase

// Dolabım ekranının durumunu ve kullanıcı işlemlerini yönetir.
@HiltViewModel
class PantryViewModel @Inject constructor(
    private val getPantryItemsUseCase:
    GetPantryItemsUseCase,
    private val addPantryItemUseCase:
    AddPantryItemUseCase,
    private val deletePantryItemUseCase:
    DeletePantryItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PantryUiState())

    val uiState: StateFlow<PantryUiState> =
        _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun onEvent(event: PantryEvent) {
        when (event) {
            is PantryEvent.NameChanged -> {
                _uiState.update {
                    it.copy(
                        name = event.name,
                        errorMessageResId = null
                    )
                }
            }

            is PantryEvent.QuantityChanged -> {
                _uiState.update {
                    it.copy(
                        quantity = event.quantity,
                        errorMessageResId = null
                    )
                }
            }

            is PantryEvent.UnitChanged -> {
                _uiState.update {
                    it.copy(
                        unit = event.unit,
                        errorMessageResId = null
                    )
                }
            }

            is PantryEvent.SectionChanged -> {
                _uiState.update {
                    it.copy(
                        selectedSection = event.section,
                        errorMessageResId = null
                    )
                }
            }

            is PantryEvent.DeleteItemClicked -> {
                deleteItem(event.itemId)
            }

            is PantryEvent.AddItemRequested -> {
                _uiState.update {
                    it.copy(
                        selectedSection = event.section
                            ?: PantrySection.DRY_FOOD,
                        isAddItemDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            PantryEvent.AddItemDismissed -> {
                dismissAddItemDialog()
            }

            PantryEvent.SaveItemClicked -> {
                saveItem()
            }

            PantryEvent.RetryClicked -> {
                loadItems()
            }

            PantryEvent.ClearError -> {
                _uiState.update {
                    it.copy(errorMessageResId = null)
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
                val items = getPantryItemsUseCase()

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
                            R.string.pantry_error_load
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
                it.copy(errorMessageResId = validationError)
            }
            return
        }

        if (state.isSaving) return

        val quantity = state.quantity
            .replace(',', '.')
            .toDouble()

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessageResId = null
                )
            }

            try {
                val savedItem = addPantryItemUseCase(
                    name = state.name,
                    quantity = quantity,
                    unit = state.unit,
                    section = state.selectedSection
                )

                _uiState.update {
                    it.copy(
                        items = listOf(savedItem) + it.items,
                        name = "",
                        quantity = "",
                        unit = "",
                        selectedSection = PantrySection.DRY_FOOD,
                        isSaving = false,
                        isAddItemDialogVisible = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessageResId =
                            R.string.pantry_error_save
                    )
                }
            }
        }
    }

    private fun deleteItem(itemId: String) {
        if (_uiState.value.deletingItemId != null) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    deletingItemId = itemId,
                    errorMessageResId = null
                )
            }

            try {
                deletePantryItemUseCase(itemId)

                _uiState.update {
                    it.copy(
                        items = it.items.filterNot { item ->
                            item.id == itemId
                        },
                        deletingItemId = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        deletingItemId = null,
                        errorMessageResId =
                            R.string.pantry_error_delete
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
                quantity = "",
                unit = "",
                selectedSection = PantrySection.DRY_FOOD,
                isAddItemDialogVisible = false,
                errorMessageResId = null
            )
        }
    }

    private fun validateForm(
        state: PantryUiState
    ): Int? {
        val quantity = state.quantity
            .replace(',', '.')
            .toDoubleOrNull()

        return when {
            state.name.isBlank() ->
                R.string.pantry_error_name_empty

            quantity == null || quantity <= 0.0 ->
                R.string.pantry_error_quantity_invalid

            state.unit.isBlank() ->
                R.string.pantry_error_unit_empty

            else -> null
        }
    }
}