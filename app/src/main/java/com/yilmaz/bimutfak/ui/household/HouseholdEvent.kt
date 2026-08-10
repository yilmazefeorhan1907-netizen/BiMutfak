package com.yilmaz.bimutfak.ui.household

// Kullanıcının Hanem ekranında gerçekleştirebileceği işlemleri tanımlar.
sealed interface HouseholdEvent {

    // Yeni hane adı alanındaki değeri ViewModel'e gönderir.
    data class HouseholdNameChanged(
        val householdName: String
    ) : HouseholdEvent

    // Davet kodu alanındaki değeri ViewModel'e gönderir.
    data class InviteCodeChanged(
        val inviteCode: String
    ) : HouseholdEvent

    // Hane oluşturma penceresinin açılmasını ister.
    data object CreateHouseholdRequested : HouseholdEvent

    // Hane oluşturma penceresini kapatır.
    data object CreateHouseholdDismissed : HouseholdEvent

    // Girilen bilgilerle yeni hane oluşturulmasını ister.
    data object CreateHouseholdClicked : HouseholdEvent

    // Haneye katılma penceresinin açılmasını ister.
    data object JoinHouseholdRequested : HouseholdEvent

    // Haneye katılma penceresini kapatır.
    data object JoinHouseholdDismissed : HouseholdEvent

    // Girilen davet koduyla haneye katılmayı ister.
    data object JoinHouseholdClicked : HouseholdEvent
    // Normal üye için haneden ayrılma onay penceresini açar.
    data object LeaveHouseholdRequested : HouseholdEvent

    // Haneden ayrılma onay penceresini kapatır.
    data object LeaveHouseholdDismissed : HouseholdEvent

    // Kullanıcının haneden ayrılma işlemini onayladığını bildirir.
    data object LeaveHouseholdConfirmed : HouseholdEvent

    // Yöneticinin seçilen üyeyi çıkarma onay penceresini açar.
    data class RemoveMemberRequested(
        val memberId: String
    ) : HouseholdEvent

    // Üye çıkarma onay penceresini kapatır.
    data object RemoveMemberDismissed : HouseholdEvent

    // Seçilen üyeyi haneden çıkarma işlemini onaylar.
    data object RemoveMemberConfirmed : HouseholdEvent

    // Hane bilgilerinin tekrar yüklenmesini ister.
    data object RetryClicked : HouseholdEvent

    // Gösterilmiş hata mesajını temizler.
    data object ClearError : HouseholdEvent
}