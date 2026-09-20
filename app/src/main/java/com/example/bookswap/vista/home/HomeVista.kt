// vista/home/HomeVista.kt
package com.example.bookswap.vista.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.vista.componentes.BannerDestacado
import com.example.bookswap.vista.componentes.SeccionCategorias
import com.example.bookswap.vista.componentes.SeccionHorizontalLibros
import com.example.bookswap.vista.componentes.SeccionVecinos

@Composable
fun HomeVista(
    vistaModelo: HomeVistaModelo = viewModel(),
    alTocarLibro: (Libro) -> Unit,
) {
    val libros by vistaModelo.libros.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            if (libros.isNotEmpty()) {
                androidx.compose.foundation.layout.Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BannerDestacado(libro = libros.first(), alTocar = { alTocarLibro(libros.first()) })
                }
            }
        }

        item {
            SeccionCategorias(
                categorias = listOf("Novela", "Distopía", "Fábula", "Ciencia", "Poesía"),
                alSeleccionar = { }
            )
        }

        item {
            SeccionHorizontalLibros(
                titulo = "Cerca de ti esta semana",
                libros = libros,
                alTocarLibro = alTocarLibro
            )
        }

        item {
            SeccionVecinos(nombres = listOf("Camila", "Andrés", "Luis"))
        }
    }
}