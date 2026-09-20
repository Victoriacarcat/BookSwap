// vista/componentes/BarraSuperior.kt
package com.example.bookswap.vista.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(alTocarBusqueda: () -> Unit = {}) {
    TopAppBar(
        title = { Text("BookSwap", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = alTocarBusqueda) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
