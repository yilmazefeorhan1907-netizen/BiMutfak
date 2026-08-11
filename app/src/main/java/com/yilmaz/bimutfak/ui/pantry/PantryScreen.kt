package com.yilmaz.bimutfak.ui.pantry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.model.PantryItem
import com.yilmaz.bimutfak.domain.model.PantrySection
import com.yilmaz.bimutfak.ui.components.BiMutfakTextField
import com.yilmaz.bimutfak.ui.theme.ButterYellow
import com.yilmaz.bimutfak.ui.theme.ButterYellowSoft
import com.yilmaz.bimutfak.ui.theme.FreezerPlum
import com.yilmaz.bimutfak.ui.theme.FreezerPlumSoft
import com.yilmaz.bimutfak.ui.theme.OliveGreen
import com.yilmaz.bimutfak.ui.theme.OliveGreenSoft
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// PantryViewModel ile Dolabım ekranı arasındaki bağlantıyı kurar.
@Composable
fun PantryRoute(
    viewModel: PantryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PantryScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun PantryScreen(
    uiState: PantryUiState,
    onEvent: (PantryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemPendingDeletion by remember {
        mutableStateOf<PantryItem?>(null)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        PantryHeader(
            onAddItemClick = {
                onEvent(PantryEvent.AddItemRequested())
            }
        )

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.size(12.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            uiState.errorMessageResId != null &&
            !uiState.isAddItemDialogVisible
        ) {
            Spacer(modifier = Modifier.size(12.dp))

            PantryErrorMessage(
                message = stringResource(
                    uiState.errorMessageResId
                ),
                onRetry = {
                    onEvent(PantryEvent.RetryClicked)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        if (uiState.isLoading && uiState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PantrySection.entries.forEach { section ->
                PantrySectionCard(
                    section = section,
                    items = uiState.items.filter {
                        it.section == section
                    },
                    deletingItemId = uiState.deletingItemId,
                    onAddItem = {
                        onEvent(
                            PantryEvent.AddItemRequested(section)
                        )
                    },
                    onDeleteItem = { itemId ->
                        itemPendingDeletion = uiState.items
                            .firstOrNull { item ->
                                item.id == itemId
                            }
                    }
                )

                Spacer(modifier = Modifier.size(14.dp))
            }
        }
    }

    if (uiState.isAddItemDialogVisible) {
        AddPantryItemDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }
    itemPendingDeletion?.let { item ->
        AlertDialog(
            onDismissRequest = {
                itemPendingDeletion = null
            },
            title = {
                Text(
                    text = stringResource(
                        R.string.pantry_delete_dialog_title
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.pantry_delete_dialog_message,
                        item.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(
                            PantryEvent.DeleteItemClicked(
                                item.id
                            )
                        )
                        itemPendingDeletion = null
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.common_delete
                        ),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        itemPendingDeletion = null
                    }
                ) {
                    Text(
                        text = stringResource(
                            R.string.common_cancel
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun PantryHeader(
    onAddItemClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.pantry_title),
            style = MaterialTheme.typography.headlineLarge
        )

        Button(
            onClick = onAddItemClick,
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(
                horizontal = 14.dp,
                vertical = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = stringResource(
                    R.string.pantry_add_item
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun PantrySectionCard(
    section: PantrySection,
    items: List<PantryItem>,
    deletingItemId: String?,
    onAddItem: () -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val sectionColor = pantrySectionColor(section)
    val sectionSoftColor = pantrySectionSoftColor(section)

    Card(
        onAddItem,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = pantrySectionIcon(section),
                    contentDescription = null,
                    tint = sectionColor
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = pantrySectionLabel(section),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            if (items.isEmpty()) {
                Text(
                    text = stringResource(
                        R.string.pantry_empty_section
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = items,
                        key = { item -> item.id }
                    ) { item ->
                        PantryItemCard(
                            item = item,
                            sectionColor = sectionColor,
                            sectionSoftColor = sectionSoftColor,
                            isDeleting =
                                deletingItemId == item.id,
                            onDelete = {
                                onDeleteItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PantryItemCard(
    item: PantryItem,
    sectionColor: Color,
    sectionSoftColor: Color,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = sectionSoftColor
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = sectionColor,
                    modifier = Modifier.size(24.dp)
                )

                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = sectionColor
                    )
                } else {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector =
                                Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(
                                R.string
                                    .pantry_delete_item_description,
                                item.name
                            ),
                            modifier = Modifier.size(19.dp),
                            tint = sectionColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = "${formatQuantity(item.quantity)} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PantryErrorMessage(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            TextButton(onClick = onRetry) {
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
private fun AddPantryItemDialog(
    uiState: PantryUiState,
    onEvent: (PantryEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(PantryEvent.AddItemDismissed)
        },
        title = {
            Text(
                text = stringResource(
                    R.string.pantry_add_item
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {
                BiMutfakTextField(
                    value = uiState.name,
                    onValueChange = {
                        onEvent(PantryEvent.NameChanged(it))
                    },
                    label = stringResource(
                        R.string.pantry_item_name
                    ),
                    placeholder = stringResource(
                        R.string.pantry_item_name
                    ),
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(12.dp))

                BiMutfakTextField(
                    value = uiState.quantity,
                    onValueChange = {
                        onEvent(PantryEvent.QuantityChanged(it))
                    },
                    label = stringResource(
                        R.string.pantry_item_quantity
                    ),
                    placeholder = "1",
                    keyboardType = KeyboardType.Decimal,
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(12.dp))

                BiMutfakTextField(
                    value = uiState.unit,
                    onValueChange = {
                        onEvent(PantryEvent.UnitChanged(it))
                    },
                    label = stringResource(
                        R.string.pantry_item_unit
                    ),
                    placeholder = "kg, litre, adet",
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(
                        R.string.pantry_item_section
                    ),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.size(8.dp))

                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = PantrySection.entries,
                        key = { section -> section.name }
                    ) { section ->
                        FilterChip(
                            selected =
                                uiState.selectedSection == section,
                            onClick = {
                                onEvent(
                                    PantryEvent.SectionChanged(
                                        section
                                    )
                                )
                            },
                            label = {
                                Text(
                                    text = pantrySectionLabel(
                                        section
                                    )
                                )
                            },
                            enabled = !uiState.isSaving
                        )
                    }
                }

                uiState.errorMessageResId?.let { messageResId ->
                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = stringResource(messageResId),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(PantryEvent.SaveItemClicked)
                },
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(
                            R.string.pantry_save_item
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(PantryEvent.AddItemDismissed)
                },
                enabled = !uiState.isSaving
            ) {
                Text(
                    text = stringResource(
                        R.string.common_cancel
                    )
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun pantrySectionLabel(
    section: PantrySection
): String {
    return stringResource(
        when (section) {
            PantrySection.DRY_FOOD ->
                R.string.pantry_section_dry_food

            PantrySection.REFRIGERATOR ->
                R.string.pantry_section_refrigerator

            PantrySection.FREEZER ->
                R.string.pantry_section_freezer
        }
    )
}

private fun pantrySectionIcon(
    section: PantrySection
): ImageVector {
    return when (section) {
        PantrySection.DRY_FOOD ->
            Icons.Outlined.Inventory2

        PantrySection.REFRIGERATOR ->
            Icons.Outlined.Kitchen

        PantrySection.FREEZER ->
            Icons.Outlined.AcUnit
    }
}

private fun pantrySectionColor(
    section: PantrySection
): Color {
    return when (section) {
        PantrySection.DRY_FOOD -> OliveGreen
        PantrySection.REFRIGERATOR -> ButterYellow
        PantrySection.FREEZER -> FreezerPlum
    }
}

private fun pantrySectionSoftColor(
    section: PantrySection
): Color {
    return when (section) {
        PantrySection.DRY_FOOD -> OliveGreenSoft
        PantrySection.REFRIGERATOR -> ButterYellowSoft
        PantrySection.FREEZER -> FreezerPlumSoft
    }
}

private fun formatQuantity(
    quantity: Double
): String {
    return if (quantity % 1.0 == 0.0) {
        quantity.toInt().toString()
    } else {
        quantity.toString()
    }
}

