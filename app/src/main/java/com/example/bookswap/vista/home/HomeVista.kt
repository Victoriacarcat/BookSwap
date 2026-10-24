// vista/home/HomeVista.kt
package com.example.bookswap.vista.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.modelo.CategoriaLibro
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.ubicacion.abrirAjustesApp
import com.example.bookswap.vista.componentes.BannerDestacado
import com.example.bookswap.vista.componentes.GestorPermisoUbicacion
import com.example.bookswap.vista.componentes.SeccionCategorias
import com.example.bookswap.vista.componentes.SeccionHorizontalLibros
import com.example.bookswap.vista.componentes.SeccionVecinos

@Composable
fun HomeVista(
    vistaModelo: HomeVistaModelo = viewModel(factory = HomeVistaModelo.Fabrica),
    alTocarLibro: (Libro) -> Unit,
) {
    val contexto = LocalContext.current
    val focoActual = LocalFocusManager.current

    val libros by vistaModelo.libros.collectAsStateWithLifecycle()
    val destacado by vistaModelo.destacado.collectAsStateWithLifecycle()
    val estado by vistaModelo.estadoUbicacion.collectAsStateWithLifecycle()
    val categoria by vistaModelo.categoriaSeleccionada.collectAsStateWithLifecycle()
    val consulta by vistaModelo.consulta.collectAsStateWithLifecycle()
    val ampliada by vistaModelo.busquedaAmpliada.collectAsStateWithLifecycle()

    GestorPermisoUbicacion(
        alConcedido = vistaModelo::actualizarUbicacion,
        alDenegado = vistaModelo::permisoDenegado,
        alDetener = vistaModelo::detenerBusquedaUbicacion
    )

    if (estado == EstadoUbicacion.BUSCANDO) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val radioKm = (HomeVistaModelo.RADIO_METROS / 1000).toInt()
    val lugar = if (estado == EstadoUbicacion.DISPONIBLE) "a menos de $radioKm km de ti" else "por ahora"
    val categoriaActual = categoria
    val buscando = consulta.isNotBlank()

    val mensajeVacio = when {
        buscando && ampliada -> "No encontramos resultados para “$consulta” en ningún libro publicado."
        buscando -> "No hay resultados para “$consulta” $lugar."
        categoriaActual != null -> "No hay libros de ${categoriaActual.etiqueta} $lugar."
        else -> "No hay libros $lugar."
    }

    val tituloSeccion = when {
        buscando && ampliada -> "Resultados (incluye libros lejanos)"
        buscando -> "Resultados"
        else -> "Cerca de ti esta semana"
    }

    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            OutlinedTextField(
                value = consulta,
                onValueChange = vistaModelo::cambiarConsulta,
                placeholder = { Text("Buscar por título o autor") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (buscando) {
                        IconButton(onClick = { vistaModelo.cambiarConsulta("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focoActual.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        if (estado == EstadoUbicacion.NO_DISPONIBLE) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        text = "Activa la ubicación para ver solo los libros cerca de ti.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = { abrirAjustesApp(contexto) }) {
                        Text("Abrir ajustes")
                    }
                }
            }
        }

        if (!buscando) {
            destacado?.let { libroDestacado ->
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        BannerDestacado(
                            libro = libroDestacado,
                            alTocar = { alTocarLibro(libroDestacado) }
                        )
                    }
                }
            }
        }

        item {
            SeccionCategorias(
                categorias = CategoriaLibro.entries,
                seleccionada = categoria,
                alSeleccionar = vistaModelo::seleccionarCategoria
            )
        }

        item {
            if (libros.isEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(text = mensajeVacio, style = MaterialTheme.typography.bodyMedium)
                    if (buscando && !ampliada && estado == EstadoUbicacion.DISPONIBLE) {
                        TextButton(onClick = vistaModelo::ampliarBusqueda) {
                            Text("Buscar también más lejos")
                        }
                    }
                }
            } else {
                SeccionHorizontalLibros(
                    titulo = tituloSeccion,
                    libros = libros,
                    alTocarLibro = alTocarLibro
                )
            }
        }

        if (!buscando) {
            item {
                SeccionVecinos(nombres = listOf("Camila", "Andrés", "Luis"))
            }
        }
    }
}