package com.example.bookswap.vista.componentes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeccionCategorias(categorias: List<String>, alSeleccionar: (String) -> Unit) {
    var seleccionada by remember { mutableStateOf<String?>(null) }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categorias) { categoria ->
            FilterChip(
                selected = categoria == seleccionada,
                onClick = {
                    seleccionada = categoria
                    alSeleccionar(categoria)
                },
                label = { Text(categoria) }
            )
        }
    }
}