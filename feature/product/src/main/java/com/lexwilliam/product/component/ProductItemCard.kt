package com.lexwilliam.product.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lexwilliam.core.extensions.toCurrency
import com.lexwilliam.core_ui.component.textfield.InvenceOutlineTextField
import com.lexwilliam.core_ui.theme.InvenceTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductItemCard(
    itemId: Int,
    buyPrice: String,
    quantity: String,
    readOnly: Boolean = true,
    onBuyPriceChanged: (String) -> Unit = {},
    onQuantityChanged: (String) -> Unit = {},
    onRemoveItem: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = "Item $itemId",
                style = InvenceTheme.typography.bodyLarge
            )
            if (!readOnly) {
                IconButton(onClick = onRemoveItem) {
                    Icon(Icons.Default.Remove, contentDescription = "remove item icon")
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InvenceOutlineTextField(
                modifier = Modifier.weight(4f),
                value =
                    if (readOnly) {
                        buyPrice.toDouble().toCurrency()
                    } else {
                        buyPrice
                    },
                onValueChange = onBuyPriceChanged,
                placeholder = {
                    Text(
                        text = "Input Buy Price",
                        style = InvenceTheme.typography.bodyLarge
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                readOnly = readOnly
            )
            InvenceOutlineTextField(
                modifier = Modifier.weight(1f),
                value = quantity,
                onValueChange = onQuantityChanged,
                placeholder = {
                    Text(
                        text = "0",
                        style = InvenceTheme.typography.bodyLarge
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = readOnly
            )
        }
    }
}