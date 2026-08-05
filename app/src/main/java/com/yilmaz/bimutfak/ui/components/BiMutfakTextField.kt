package com.yilmaz.bimutfak.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Ekranlarında kullanılacak standart metin giriş alanını oluşturur.
@Composable
fun BiMutfakTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation = visualTransformation,
        trailingIcon = trailingContent,
        shape = MaterialTheme.shapes.medium
    )
}

// Şifreyi gizleme ve gösterme özelliğine sahip standart şifre alanını oluşturur.
@Composable
fun BiMutfakPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPasswordVisible: Boolean,
    onVisibilityChange: () -> Unit,
    showPasswordText: String,
    hidePasswordText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    BiMutfakTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        keyboardType = KeyboardType.Password,
        enabled = enabled,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingContent = {
            TextButton(
                onClick = onVisibilityChange
            ) {
                Text(
                    text = if (isPasswordVisible) {
                        hidePasswordText
                    } else {
                        showPasswordText
                    }
                )
            }
        }
    )
}