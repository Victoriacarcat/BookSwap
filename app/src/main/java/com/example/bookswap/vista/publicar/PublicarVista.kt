package com.example.bookswap.vista.publicar

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.ubicacion.tienePermisoUbicacion
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.example.bookswap.data.modelo.CategoriaLibro



@Composable
fun PublicarVista(
    alTerminar: () -> Unit,
    vistaModelo: PublicarVistaModelo = viewModel(factory = PublicarVistaModelo.Fabrica)
) {
    val contexto = LocalContext.current

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) vistaModelo.fotoUri = uri
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) {
            vistaModelo.publicar(alTerminar)
        } else {
            vistaModelo.reportarError("Necesitamos tu ubicación para publicar el libro cerca de ti.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Publicar un libro", style = MaterialTheme.typography.headlineSmall)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    selectorFoto.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (vistaModelo.fotoUri != null) {
                AsyncImage(
                    model = vistaModelo.fotoUri,
                    contentDescription = "Portada del libro",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Toca para elegir una portada (obligatoria)")
            }
        }

        OutlinedTextField(
            value = vistaModelo.titulo,
            onValueChange = { vistaModelo.titulo = it },
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vistaModelo.autor,
            onValueChange = { vistaModelo.autor = it },
            label = { Text("Autor") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CategoriaLibro.entries) { categoria ->
                FilterChip(
                    selected = vistaModelo.genero == categoria,
                    onClick = { vistaModelo.genero = categoria },
                    label = { Text(categoria.etiqueta) }
                )
            }
        }

        Text("Estado del libro", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EstadoLibro.entries.forEach { opcion ->
                FilterChip(
                    selected = vistaModelo.estado == opcion,
                    onClick = { vistaModelo.estado = opcion },
                    label = { Text(opcion.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        OutlinedTextField(
            value = vistaModelo.descripcion,
            onValueChange = { vistaModelo.descripcion = it },
            label = { Text("Descripción (opcional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vistaModelo.notaPublicante,
            onValueChange = { vistaModelo.notaPublicante = it },
            label = { Text("Nota para quien lo reciba (opcional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        vistaModelo.error?.let { mensaje ->
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (tienePermisoUbicacion(contexto)) {
                    vistaModelo.publicar(alTerminar)
                } else {
                    lanzadorPermiso.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            enabled = vistaModelo.puedePublicar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (vistaModelo.guardando) "Guardando..." else "Publicar libro")
        }
    }
}