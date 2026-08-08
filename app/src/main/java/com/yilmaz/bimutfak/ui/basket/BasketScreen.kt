package com.yilmaz.bimutfak.ui.basket

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yilmaz.bimutfak.R
import com.yilmaz.bimutfak.domain.model.BasketItem
import com.yilmaz.bimutfak.ui.components.BiMutfakTextField

// BasketViewModel ile Bi’Sepet ekranı arasındaki bağlantıyı kurar.
@Composable
fun BasketRoute(
    viewModel: BasketViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BasketScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun BasketScreen(
    uiState: BasketUiState,
    onEvent: (BasketEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        BasketHeader(
            onAddItemClick = {
                onEvent(BasketEvent.AddItemRequested)
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

            BasketErrorMessage(
                message = stringResource(
                    uiState.errorMessageResId
                ),
                onRetry = {
                    onEvent(BasketEvent.RetryClicked)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                uiState.items.isEmpty() -> {
                    BasketEmptyCard(
                        modifier = Modifier.align(
                            Alignment.TopCenter
                        )
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = uiState.items,
                            key = { item -> item.id }
                        ) { item ->
                            BasketItemCard(
                                item = item,
                                isProcessing =
                                    uiState.processingItemId ==
                                            item.id,
                                onCheckedChange = { checked ->
                                    onEvent(
                                        BasketEvent
                                            .ItemCheckedChanged(
                                                itemId = item.id,
                                                checked = checked
                                            )
                                    )
                                },
                                onDeleteClick = {
                                    onEvent(
                                        BasketEvent
                                            .DeleteItemClicked(
                                                itemId = item.id
                                            )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isAddItemDialogVisible) {
        AddBasketItemDialog(
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

@Composable
private fun BasketHeader(
    onAddItemClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.basket_title),
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

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = stringResource(
                    R.string.basket_add_item
                ),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun BasketEmptyCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.ShoppingBasket,
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text = stringResource(R.string.basket_empty),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BasketItemCard(
    item: BasketItem,
    isProcessing: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.checked,
                onCheckedChange = onCheckedChange,
                enabled = !isProcessing
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (item.checked) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = "${formatBasketQuantity(item.quantity)} ${item.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(
                            R.string
                                .basket_delete_item_description,
                            item.name
                        ),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun BasketErrorMessage(
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
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onErrorContainer
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
private fun AddBasketItemDialog(
    uiState: BasketUiState,
    onEvent: (BasketEvent) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onEvent(BasketEvent.AddItemDismissed)
        },
        title = {
            Text(
                text = stringResource(
                    R.string.basket_add_item
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
                        onEvent(
                            BasketEvent.NameChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.basket_item_name
                    ),
                    placeholder = stringResource(
                        R.string.basket_item_name
                    ),
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(12.dp))

                BiMutfakTextField(
                    value = uiState.quantity,
                    onValueChange = {
                        onEvent(
                            BasketEvent.QuantityChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.basket_item_quantity
                    ),
                    placeholder = "1",
                    keyboardType = KeyboardType.Decimal,
                    enabled = !uiState.isSaving
                )

                Spacer(modifier = Modifier.size(12.dp))

                BiMutfakTextField(
                    value = uiState.unit,
                    onValueChange = {
                        onEvent(
                            BasketEvent.UnitChanged(it)
                        )
                    },
                    label = stringResource(
                        R.string.basket_item_unit
                    ),
                    placeholder = "kg, litre, adet",
                    enabled = !uiState.isSaving
                )

                uiState.errorMessageResId?.let {
                        messageResId ->

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(
                        text = stringResource(messageResId),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(BasketEvent.SaveItemClicked)
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
                            R.string.basket_save_item
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onEvent(BasketEvent.AddItemDismissed)
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

private fun formatBasketQuantity(
    quantity: Double
): String {
    return if (quantity % 1.0 == 0.0) {
        quantity.toInt().toString()
    } else {
        quantity.toString()
    }
}