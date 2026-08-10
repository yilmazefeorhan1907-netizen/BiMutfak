package com.yilmaz.bimutfak.ui.household

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.model.Household
import com.yilmaz.bimutfak.domain.model.User
import com.yilmaz.bimutfak.ui.components.BiMutfakTextField

// HouseholdViewModel ile Hanem ekranı arasındaki bağlantıyı kurar.
@Composable
fun HouseholdRoute(
    viewModel: HouseholdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HouseholdScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HouseholdScreen(
    uiState: HouseholdUiState,
    onEvent: (HouseholdEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        Text(
            text = stringResource(
                R.string.household_title
            ),
            style = MaterialTheme.typography.headlineLarge
        )

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.size(16.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            uiState.errorMessageResId != null &&
            !uiState.isCreateDialogVisible &&
            !uiState.isJoinDialogVisible &&
            !uiState.isLeaveDialogVisible &&
            !uiState.isRemoveMemberDialogVisible
        ) {
            Spacer(modifier = Modifier.size(16.dp))

            HouseholdErrorCard(
                message = stringResource(
                    uiState.errorMessageResId
                ),
                onRetry = {
                    onEvent(HouseholdEvent.RetryClicked)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        when {
            uiState.isLoading &&
                    uiState.household == null -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.household == null -> {
                HouseholdEmptyContent(
                    onCreateClick = {
                        onEvent(
                            HouseholdEvent
                                .CreateHouseholdRequested
                        )
                    },
                    onJoinClick = {
                        onEvent(
                            HouseholdEvent
                                .JoinHouseholdRequested
                        )
                    }
                )
            }

            else -> {
                HouseholdContent(
                    household = uiState.household,
                    members = uiState.members,
                    currentUserId = uiState.currentUserId,
                    onLeaveClick = {
                        onEvent(
                            HouseholdEvent
                                .LeaveHouseholdRequested
                        )
                    },
                    onRemoveMemberClick = { memberId ->
                        onEvent(
                            HouseholdEvent.RemoveMemberRequested(
                                memberId = memberId
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
    }

    if (uiState.isCreateDialogVisible) {
        CreateHouseholdDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }

    if (uiState.isJoinDialogVisible) {
        JoinHouseholdDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }

    if (uiState.isLeaveDialogVisible) {
        LeaveHouseholdDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }

    if (uiState.isRemoveMemberDialogVisible) {
        val selectedMember = uiState.members.firstOrNull {
            it.uid == uiState.selectedMemberId
        }

        RemoveMemberDialog(
            uiState = uiState,
            memberName = selectedMember
                ?.let(::memberDisplayName)
                .orEmpty(),
            onEvent = onEvent
        )
    }
}

@Composable
private fun HouseholdEmptyContent(
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Groups,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(
                    R.string.household_empty_title
                ),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(
                    R.string.household_empty_description
                ),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(24.dp))

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(
                        R.string.household_create
                    )
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            OutlinedButton(
                onClick = onJoinClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Key,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(
                        R.string.household_join
                    )
                )
            }
        }
    }
}

@Composable
private fun HouseholdContent(
    household: Household,
    members: List<User>,
    currentUserId: String,
    onLeaveClick: () -> Unit,
    onRemoveMemberClick: (String) -> Unit
) {
    val isCurrentUserOwner =
        currentUserId == household.ownerId

    HouseholdSummaryCard(
        household = household,
        memberCount = members.size
    )

    Spacer(modifier = Modifier.size(16.dp))

    Text(
        text = stringResource(
            R.string.household_members_title
        ),
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.size(10.dp))

    members.forEach { member ->
        HouseholdMemberCard(
            member = member,
            isOwner = member.uid == household.ownerId,
            canRemove = isCurrentUserOwner &&
                    member.uid != household.ownerId,
            onRemoveClick = {
                onRemoveMemberClick(member.uid)
            }
        )

        Spacer(modifier = Modifier.size(10.dp))
    }

    Spacer(modifier = Modifier.size(6.dp))

    if (isCurrentUserOwner) {
        HouseholdInviteCard(
            inviteCode = household.inviteCode
        )
    } else {
        OutlinedButton(
            onClick = onLeaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(
                    R.string.household_leave
                )
            )
        }
    }
}

@Composable
private fun HouseholdSummaryCard(
    household: Household,
    memberCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = household.name,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = stringResource(
                        R.string.household_member_count,
                        memberCount
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HouseholdMemberCard(
    member: User,
    isOwner: Boolean,
    canRemove: Boolean,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = memberDisplayName(member),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.size(2.dp))

                Text(
                    text = stringResource(
                        if (isOwner) {
                            R.string.household_owner
                        } else {
                            R.string.household_member
                        }
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (canRemove) {
                TextButton(
                    onClick = onRemoveClick
                ) {
                    Text(
                        text = stringResource(
                            R.string.household_remove_member
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun HouseholdInviteCard(
    inviteCode: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Key,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = stringResource(
                        R.string.household_invite_title
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = inviteCode,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(
                    R.string.household_invite_description
                ),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HouseholdErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer
            )

            TextButton(
                onClick = onRetry,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = stringResource(
                        R.string.common_retry
                    )
                )
            }
        }
    }
}

@Composable
private fun CreateHouseholdDialog(
    uiState: HouseholdUiState,
    onEvent: (HouseholdEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isSubmitting) {
                onEvent(
                    HouseholdEvent
                        .CreateHouseholdDismissed
                )
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.household_create_title
                )
            )
        },
        text = {
            Column {
                BiMutfakTextField(
                    value = uiState.householdName,
                    onValueChange = {
                        onEvent(
                            HouseholdEvent
                                .HouseholdNameChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.household_name_label
                    ),
                    placeholder = stringResource(
                        R.string.household_name_label
                    ),
                    enabled = !uiState.isSubmitting
                )

                HouseholdDialogError(
                    errorMessageResId =
                        uiState.errorMessageResId
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent
                            .CreateHouseholdClicked
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                DialogButtonContent(
                    isSubmitting = uiState.isSubmitting,
                    text = stringResource(
                        R.string.household_create
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent
                            .CreateHouseholdDismissed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                Text(
                    text = stringResource(
                        R.string.household_cancel
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun JoinHouseholdDialog(
    uiState: HouseholdUiState,
    onEvent: (HouseholdEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isSubmitting) {
                onEvent(
                    HouseholdEvent.JoinHouseholdDismissed
                )
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.household_join_title
                )
            )
        },
        text = {
            Column {
                BiMutfakTextField(
                    value = uiState.inviteCode,
                    onValueChange = {
                        onEvent(
                            HouseholdEvent
                                .InviteCodeChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string
                            .household_invite_code_label
                    ),
                    placeholder = stringResource(
                        R.string
                            .household_invite_code_label
                    ),
                    enabled = !uiState.isSubmitting
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = stringResource(
                        R.string.household_join_data_notice
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                HouseholdDialogError(
                    errorMessageResId =
                        uiState.errorMessageResId
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.JoinHouseholdClicked
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                DialogButtonContent(
                    isSubmitting = uiState.isSubmitting,
                    text = stringResource(
                        R.string.household_join
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.JoinHouseholdDismissed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                Text(
                    text = stringResource(
                        R.string.household_cancel
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun LeaveHouseholdDialog(
    uiState: HouseholdUiState,
    onEvent: (HouseholdEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isSubmitting) {
                onEvent(
                    HouseholdEvent.LeaveHouseholdDismissed
                )
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.household_leave_title
                )
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(
                        R.string.household_leave_description
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                HouseholdDialogError(
                    errorMessageResId =
                        uiState.errorMessageResId
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.LeaveHouseholdConfirmed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                DialogButtonContent(
                    isSubmitting = uiState.isSubmitting,
                    text = stringResource(
                        R.string.household_leave
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.LeaveHouseholdDismissed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                Text(
                    text = stringResource(
                        R.string.household_cancel
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun RemoveMemberDialog(
    uiState: HouseholdUiState,
    memberName: String,
    onEvent: (HouseholdEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!uiState.isSubmitting) {
                onEvent(
                    HouseholdEvent.RemoveMemberDismissed
                )
            }
        },
        title = {
            Text(
                text = stringResource(
                    R.string.household_remove_member_title
                )
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(
                        R.string.household_remove_member_description,
                        memberName
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )

                HouseholdDialogError(
                    errorMessageResId =
                        uiState.errorMessageResId
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.RemoveMemberConfirmed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                DialogButtonContent(
                    isSubmitting = uiState.isSubmitting,
                    text = stringResource(
                        R.string.household_remove_member
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(
                        HouseholdEvent.RemoveMemberDismissed
                    )
                },
                enabled = !uiState.isSubmitting
            ) {
                Text(
                    text = stringResource(
                        R.string.household_cancel
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun HouseholdDialogError(
    errorMessageResId: Int?
) {
    errorMessageResId?.let { messageResId ->
        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = stringResource(messageResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DialogButtonContent(
    isSubmitting: Boolean,
    text: String
) {
    if (isSubmitting) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
    } else {
        Text(text = text)
    }
}

private fun memberDisplayName(
    member: User
): String {
    val fullName = listOf(
        member.firstName,
        member.lastName
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")

    return fullName.ifBlank {
        member.email
    }
}