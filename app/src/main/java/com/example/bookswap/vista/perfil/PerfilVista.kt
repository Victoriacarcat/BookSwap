package com.example.bookswap.vista.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.auth.UsuarioSesion
import com.example.bookswap.data.fuenteData.DatosHardcodeados
import com.example.bookswap.data.modelo.EstadoSolicitud
import com.example.bookswap.data.modelo.SolicitudIntercambio

@Composable
fun PerfilVista(
    alEditarSolicitud: (SolicitudIntercambio) -> Unit = {},
    vistaModelo: PerfilVistaModelo = viewModel(factory = PerfilVistaModelo.Fabrica)
) {
    val solicitudes by vistaModelo.solicitudesEnviadas.collectAsStateWithLifecycle()
    val usuario by vistaModelo.usuario.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        usuario?.let { sesion ->
            item {
                TarjetaSesion(sesion = sesion, alCerrarSesion = vistaModelo::cerrarSesion)
            }
        }

        item {
            Text("Mis solicitudes de intercambio", style = MaterialTheme.typography.headlineSmall)
        }

        if (solicitudes.isEmpty()) {
            item {
                Text(
                    "Aún no has enviado solicitudes. Entra a un libro y toca “Proponer intercambio”.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(solicitudes, key = { it.id }) { solicitud ->
            TarjetaSolicitud(
                solicitud = solicitud,
                alEditar = { alEditarSolicitud(solicitud) },
                alCancelar = { vistaModelo.cancelar(solicitud) }
            )
        }
    }
}

@Composable
private fun TarjetaSolicitud(
    solicitud: SolicitudIntercambio,
    alEditar: () -> Unit,
    alCancelar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    solicitud.tituloLibroSolicitado.ifBlank { "Libro no disponible" },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(onClick = {}, label = { Text(solicitud.estado.etiqueta) })
            }
            Text(
                "Dueño: ${DatosHardcodeados.nombreUsuario(solicitud.idDueño)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Ofreces: ${solicitud.tituloLibroOfrecido.ifBlank { "—" }}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (solicitud.mensaje.isNotBlank()) {
                Text("“${solicitud.mensaje}”", style = MaterialTheme.typography.bodySmall)
            }
            if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
                Row(modifier = Modifier.align(Alignment.End)) {
                    TextButton(onClick = alEditar) {
                        Text("Editar")
                    }
                    TextButton(onClick = alCancelar) {
                        Text("Cancelar solicitud")
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSesion(sesion: UsuarioSesion, alCerrarSesion: () -> Unit) {
    val nombre = sesion.nombre?.takeIf { it.isNotBlank() } ?: "Tu cuenta de Google"
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Inicial en lugar de la foto: decorativa, el nombre ya se lee al lado
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clearAndSetSemantics {},
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        nombre.first().uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sesión iniciada como", style = MaterialTheme.typography.labelMedium)
                    Text(nombre, style = MaterialTheme.typography.titleMedium)
                    sesion.correo?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            OutlinedButton(onClick = alCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Text("Cerrar sesión", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
