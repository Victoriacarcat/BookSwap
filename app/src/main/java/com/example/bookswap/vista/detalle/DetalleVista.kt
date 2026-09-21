package com.example.bookswap.vista.detalle

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DetalleVista(
    idLibro: String,
    alProponerIntercambio: () -> Unit,
    alVolver: () -> Unit = {},
    vistaModelo: DetalleVistaModelo = viewModel(),
) {
    val libro by vistaModelo.libro.collectAsState()

    LaunchedEffect(idLibro) {
        vistaModelo.cargarLibro(idLibro)
    }

    if (libro == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val libroActual = libro!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = alVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = libroActual.imagenResId),
            contentDescription = libroActual.titulo,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = libroActual.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = libroActual.autor, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Género: ${libroActual.genero}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Estado: ${libroActual.estado.name}", style = MaterialTheme.typography.bodyMedium)

        if (libroActual.descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Descripción", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = libroActual.descripcion, style = MaterialTheme.typography.bodyMedium)
        }

        if (libroActual.notaPublicante.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nota de quien lo publica", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = libroActual.notaPublicante, style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = alProponerIntercambio,
            modifier = Modifier.fillMaxWidth(),
            enabled = libroActual.disponible
        ) {
            Text(if (libroActual.disponible) "Proponer intercambio" else "No disponible")
        }
    }
}