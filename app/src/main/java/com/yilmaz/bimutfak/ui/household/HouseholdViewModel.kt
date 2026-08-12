package com.yilmaz.bimutfak.ui.household

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.yilmaz.bimutfak.domain.error.HouseholdException

// Hanem ekranındaki olayları işler ve ekran durumunu yönetir.
@HiltViewModel
class HouseholdViewModel @Inject constructor(
    private val householdRepository: HouseholdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HouseholdUiState(
            currentUserId =
                householdRepository.currentUserId.orEmpty()
        )
    )
    val uiState: StateFlow<HouseholdUiState> =
        _uiState.asStateFlow()

    init {
        loadHousehold()
    }

    // Ekrandan gelen kullanıcı olaylarını uygun işleme yönlendirir.
    fun onEvent(event: HouseholdEvent) {
        when (event) {

            is HouseholdEvent.HouseholdNameChanged -> {
                _uiState.update {
                    it.copy(
                        householdName = event.householdName,
                        errorMessageResId = null
                    )
                }
            }

            is HouseholdEvent.InviteCodeChanged -> {
                _uiState.update {
                    it.copy(
                        inviteCode = event.inviteCode,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.CreateHouseholdRequested -> {
                _uiState.update {
                    it.copy(
                        householdName = "",
                        isCreateDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.CreateHouseholdDismissed -> {
                _uiState.update {
                    it.copy(
                        householdName = "",
                        isCreateDialogVisible = false,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.CreateHouseholdClicked -> {
                createHousehold()
            }

            HouseholdEvent.JoinHouseholdRequested -> {
                _uiState.update {
                    it.copy(
                        inviteCode = "",
                        isJoinDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.JoinHouseholdDismissed -> {
                _uiState.update {
                    it.copy(
                        inviteCode = "",
                        isJoinDialogVisible = false,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.JoinHouseholdClicked -> {
                joinHousehold()
            }

            HouseholdEvent.LeaveHouseholdRequested -> {
                _uiState.update {
                    it.copy(
                        isLeaveDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.LeaveHouseholdDismissed -> {
                if (!_uiState.value.isSubmitting) {
                    _uiState.update {
                        it.copy(
                            isLeaveDialogVisible = false,
                            errorMessageResId = null
                        )
                    }
                }
            }

            HouseholdEvent.LeaveHouseholdConfirmed -> {
                leaveHousehold()
            }

            is HouseholdEvent.RemoveMemberRequested -> {
                _uiState.update {
                    it.copy(
                        selectedMemberId = event.memberId,
                        isRemoveMemberDialogVisible = true,
                        errorMessageResId = null
                    )
                }
            }

            HouseholdEvent.RemoveMemberDismissed -> {
                if (!_uiState.value.isSubmitting) {
                    _uiState.update {
                        it.copy(
                            selectedMemberId = null,
                            isRemoveMemberDialogVisible = false,
                            errorMessageResId = null
                        )
                    }
                }
            }

            HouseholdEvent.RemoveMemberConfirmed -> {
                removeMember()
            }
            HouseholdEvent.RetryClicked -> {
                loadHousehold()
            }

            HouseholdEvent.ClearError -> {
                _uiState.update {
                    it.copy(
                        errorMessageResId = null
                    )
                }
            }
        }
    }

    // Kullanıcının bağlı olduğu haneyi ve üyelerini yükler.
    private fun loadHousehold() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessageResId = null
                )
            }

            try {
                val household =
                    householdRepository.getCurrentHousehold()

                val members = if (household != null) {
                    householdRepository.getMembers(
                        household = household
                    )
                } else {
                    emptyList()
                }

                _uiState.update {
                    it.copy(
                        household = household,
                        members = members,
                        isLoading = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId =
                            R.string.household_error_load
                    )
                }
            }
        }
    }

    // Girilen hane adıyla yeni bir hane oluşturur.
    private fun createHousehold() {
        val householdName =
            _uiState.value.householdName.trim()

        if (householdName.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessageResId =
                        R.string.household_error_name_empty
                )
            }
            return
        }

        if (_uiState.value.isSubmitting) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessageResId = null
                )
            }

            try {
                val household =
                    householdRepository.createHousehold(
                        householdName = householdName
                    )

                val members =
                    householdRepository.getMembers(
                        household = household
                    )

                _uiState.update {
                    it.copy(
                        household = household,
                        members = members,
                        householdName = "",
                        isSubmitting = false,
                        isCreateDialogVisible = false
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessageResId =
                            R.string.household_error_create
                    )
                }
            }
        }
    }

    // Girilen davet koduyla mevcut bir haneye katılır.
    private fun joinHousehold() {
        val inviteCode =
            _uiState.value.inviteCode.trim()

        if (inviteCode.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessageResId =
                        R.string.household_error_invite_empty
                )
            }
            return
        }

        if (_uiState.value.isSubmitting) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessageResId = null
                )
            }

            try {
                val household =
                    householdRepository.joinHousehold(
                        inviteCode = inviteCode
                    )

                val members =
                    householdRepository.getMembers(
                        household = household
                    )

                _uiState.update {
                    it.copy(
                        household = household,
                        members = members,
                        inviteCode = "",
                        isSubmitting = false,
                        isJoinDialogVisible = false
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessageResId =
                            resolveJoinError(exception)
                    )
                }
            }
        }
    }
    // Normal kullanıcının bağlı olduğu haneden ayrılma işlemini yürütür.
    private fun leaveHousehold() {
        if (_uiState.value.isSubmitting) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessageResId = null
                )
            }

            try {
                householdRepository.leaveHousehold()

                _uiState.update {
                    it.copy(
                        household = null,
                        members = emptyList(),
                        isSubmitting = false,
                        isLeaveDialogVisible = false,
                        errorMessageResId = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessageResId =
                            R.string.household_error_leave
                    )
                }
            }
        }
    }

    // Yöneticinin seçtiği üyeyi haneden çıkarma işlemini yürütür.
    private fun removeMember() {
        val memberId = _uiState.value.selectedMemberId
            ?: return

        if (_uiState.value.isSubmitting) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessageResId = null
                )
            }

            try {
                val household =
                    householdRepository.removeMember(
                        memberId = memberId
                    )

                val members =
                    householdRepository.getMembers(
                        household = household
                    )

                _uiState.update {
                    it.copy(
                        household = household,
                        members = members,
                        selectedMemberId = null,
                        isSubmitting = false,
                        isRemoveMemberDialogVisible = false,
                        errorMessageResId = null
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessageResId =
                            R.string.household_error_remove_member
                    )
                }
            }
        }
    }
    // Firestore'dan gelen katılma hatasını kullanıcıya uygun metne dönüştürür.
    private fun resolveJoinError(
        exception: Exception
    ): Int {
        return when (exception) {
            is HouseholdException.HouseholdFull ->
                R.string.household_error_full

            is HouseholdException.InviteCodeExpired ->
                R.string.household_error_invite_expired

            is HouseholdException.InviteCodeNotFound ->
                R.string.household_error_invite_invalid

            is HouseholdException.InviteCodeEmpty ->
                R.string.household_error_invite_empty

            else ->
                R.string.household_error_join
        }
    }
}