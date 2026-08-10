package com.yilmaz.bimutfak.ui.household

import androidx.annotation.StringRes
import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User

// Hanem ekranında gösterilecek bütün güncel arayüz verilerini tutar.
data class HouseholdUiState(
    val household: Household? = null,
    val members: List<User> = emptyList(),

    // Yeni hane oluşturma alanındaki değeri tutar.
    val householdName: String = "",

    // Mevcut haneye katılmak için girilen davet kodunu tutar.
    val inviteCode: String = "",

    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,

    val isCreateDialogVisible: Boolean = false,
    val isJoinDialogVisible: Boolean = false,

// Normal üyenin haneden ayrılma onay penceresini kontrol eder.
    val isLeaveDialogVisible: Boolean = false,

// Yöneticinin üyeyi çıkarma onay penceresini kontrol eder.
    val isRemoveMemberDialogVisible: Boolean = false,

// Haneden çıkarılmak üzere seçilen kullanıcının kimliğini tutar.
    val selectedMemberId: String? = null,

// Oturumu açık kullanıcının yönetici olup olmadığını belirlemek için kullanılır.
    val currentUserId: String = "",

    @StringRes val errorMessageResId: Int? = null
)