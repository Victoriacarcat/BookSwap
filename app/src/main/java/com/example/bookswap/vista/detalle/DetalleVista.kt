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
import androidx.compose.material3.OutlinedButton
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
import com.example.bookswap.data.modelo.ID_USUARIO_ACTUAL
import com.example.bookswap.vista.componentes.PortadaLibro


@Composable
fun DetalleVista(
    idLibro: String,
    alProponerIntercambio: () -> Unit,
    alEditar: () -> Unit = {},
    alVolver: () -> Unit = {},
    vistaModelo: DetalleVistaModelo = viewModel(factory = DetalleVistaModelo.Fabrica)
) {
    val libro by vistaModelo.libro.collectAsState()
    val solicitudPendiente by vistaModelo.solicitudPendiente.collectAsState()

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

        PortadaLibro(
            libro = libroActual,
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = libroActual.titulo, style = MaterialTheme.typography.headlineSmall)
        Text(text = libroActual.autor, style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Categoría: ${libroActual.genero.etiqueta}", style = MaterialTheme.typography.bodyMedium)
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

        val esMio = libroActual.idDueño == ID_USUARIO_ACTUAL
        val yaSolicitado = solicitudPendiente != null

        Button(
            onClick = alProponerIntercambio,
            modifier = Modifier.fillMaxWidth(),
            enabled = libroActual.disponible && !esMio && !yaSolicitado
        ) {
            Text(
                when {
                    esMio -> "Es tu libro"
                    !libroActual.disponible -> "No disponible"
                    yaSolicitado -> "Solicitud enviada"
                    else -> "Proponer intercambio"
                }
            )
        }

        if (esMio) {
            OutlinedButton(onClick = alEditar, modifier = Modifier.fillMaxWidth()) {
                Text("Editar publicación")
            }
        }
    }
}