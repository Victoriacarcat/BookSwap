package com.example.bookswap.vista.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.modelo.CategoriaLibro

@Composable
fun SeccionCategorias(
    categorias: List<CategoriaLibro>,
    seleccionada: CategoriaLibro?,
    alSeleccionar: (CategoriaLibro) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categorias) { categoria ->
            FilterChip(
                selected = categoria == seleccionada,
                onClick = { alSeleccionar(categoria) },
                label = { Text(categoria.etiqueta) }
            )
        }
    }
}