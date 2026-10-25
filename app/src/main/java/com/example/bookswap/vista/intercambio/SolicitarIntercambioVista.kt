package com.example.bookswap.vista.intercambio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.fuenteData.DatosHardcodeados
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.vista.componentes.PortadaLibro

@Composable
fun SolicitarIntercambioVista(
    idLibro: String,
    idSolicitud: String? = null,
    alEnviar: () -> Unit,
    alVolver: () -> Unit,
    alPublicar: () -> Unit,
    vistaModelo: SolicitarIntercambioVistaModelo = viewModel(factory = SolicitarIntercambioVistaModelo.Fabrica)
) {
    val libro by vistaModelo.libroSolicitado.collectAsStateWithLifecycle()
    val misLibros by vistaModelo.misLibros.collectAsStateWithLifecycle()

    val esEdicion = idSolicitud != null

    LaunchedEffect(idLibro, idSolicitud) {
        vistaModelo.cargar(idLibro, idSolicitud)
    }

    val libroActual = libro
    val misLibrosActuales = misLibros
    if (libroActual == null || misLibrosActuales == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(onClick = alVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }

        Text(
            if (esEdicion) "Editar solicitud" else "Solicitar intercambio",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Libro que quieres", style = MaterialTheme.typography.titleMedium)
        FilaLibro(
            libro = libroActual,
            detalle = "Publicado por ${DatosHardcodeados.nombreUsuario(libroActual.idDueño)}"
        )

        Text("¿Qué libro ofreces a cambio?", style = MaterialTheme.typography.titleMedium)

        if (misLibrosActuales.isEmpty()) {
            Text(
                "Todavía no has publicado libros. Publica uno para poder ofrecerlo en un intercambio.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(onClick = alPublicar, modifier = Modifier.fillMaxWidth()) {
                Text("Publicar un libro")
            }
        } else {
            misLibrosActuales.forEach { propio ->
                FilaLibro(
                    libro = propio,
                    detalle = propio.autor,
                    seleccionado = vistaModelo.libroOfrecido?.id == propio.id,
                    alSeleccionar = { vistaModelo.libroOfrecido = propio }
                )
            }
        }

        OutlinedTextField(
            value = vistaModelo.mensaje,
            onValueChange = { vistaModelo.mensaje = it },
            label = { Text("Mensaje para el dueño (opcional)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { vistaModelo.enviar(alEnviar) },
            enabled = vistaModelo.puedeEnviar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    vistaModelo.enviando -> "Guardando..."
                    esEdicion -> "Guardar cambios"
                    else -> "Enviar solicitud"
                }
            )
        }
    }
}

@Composable
private fun FilaLibro(
    libro: Libro,
    detalle: String,
    seleccionado: Boolean = false,
    alSeleccionar: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (alSeleccionar != null) Modifier.clickable(onClick = alSeleccionar) else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PortadaLibro(
                libro = libro,
                modifier = Modifier
                    .size(width = 56.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(libro.titulo, style = MaterialTheme.typography.titleSmall)
                Text(detalle, style = MaterialTheme.typography.bodySmall)
            }
            if (alSeleccionar != null) {
                RadioButton(selected = seleccionado, onClick = alSeleccionar)
            }
        }
    }
}
