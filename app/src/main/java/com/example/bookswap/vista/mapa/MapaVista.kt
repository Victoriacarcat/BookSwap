// vista/mapa/MapaVista.kt
package com.example.bookswap.vista.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.ubicacion.tienePermisoUbicacion
import com.example.bookswap.vista.componentes.GestorPermisoUbicacion
import com.example.bookswap.vista.componentes.PortadaLibro
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private val CENTRO_POR_DEFECTO = GeoPoint(4.6097, -74.0817) // Bogotá
private const val ZOOM_INICIAL = 14.0
private const val ZOOM_UBICACION = 15.0

@Composable
fun MapaVista(
    alTocarLibro: (Libro) -> Unit,
    vistaModelo: MapaVistaModelo = viewModel(factory = MapaVistaModelo.Fabrica)
) {
    val contexto = LocalContext.current
    val dueñoCicloVida = LocalLifecycleOwner.current

    val libros by vistaModelo.libros.collectAsStateWithLifecycle()
    var libroSeleccionado by remember { mutableStateOf<Libro?>(null) }
    var tienePermiso by remember { mutableStateOf(tienePermisoUbicacion(contexto)) }
    val tienePermisoActual by rememberUpdatedState(tienePermiso)

    GestorPermisoUbicacion(
        alConcedido = { tienePermiso = true },
        alDenegado = { tienePermiso = false },
        alDetener = {}
    )

    val mapa = remember {
        MapView(contexto).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(ZOOM_INICIAL)
            controller.setCenter(CENTRO_POR_DEFECTO)

            // Tocar una zona vacía del mapa cierra la tarjeta del libro
            overlays.add(MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    libroSeleccionado = null
                    return false
                }

                override fun longPressHelper(p: GeoPoint?): Boolean = false
            }))
        }
    }

    val capaUbicacion = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(contexto), mapa).also { capa ->
            mapa.overlays.add(capa)
            // Centra el mapa en el usuario la primera vez que hay señal
            capa.runOnFirstFix {
                mapa.post {
                    capa.myLocation?.let {
                        mapa.controller.animateTo(it, ZOOM_UBICACION, null)
                    }
                }
            }
        }
    }

    LaunchedEffect(tienePermiso) {
        if (tienePermiso) capaUbicacion.enableMyLocation()
    }

    // osmdroid necesita enterarse de onResume/onPause para cargar y liberar recursos
    DisposableEffect(dueñoCicloVida) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> {
                    mapa.onResume()
                    if (tienePermisoActual) capaUbicacion.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    capaUbicacion.disableMyLocation()
                    mapa.onPause()
                }
                else -> Unit
            }
        }
        dueñoCicloVida.lifecycle.addObserver(observador)
        onDispose { dueñoCicloVida.lifecycle.removeObserver(observador) }
    }

    // Separado del observador: si el LifecycleOwner cambia durante la navegación,
    // el efecto de arriba se reinicia, pero el mapa solo se libera al salir de la pantalla
    DisposableEffect(mapa) {
        onDispose {
            capaUbicacion.disableMyLocation()
            mapa.onDetach()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapa },
            modifier = Modifier.fillMaxSize(),
            update = { vista ->
                vista.overlays.removeAll { it is Marker }
                libros.forEach { libro ->
                    vista.overlays.add(Marker(vista).apply {
                        position = GeoPoint(libro.lat, libro.lng)
                        title = libro.titulo
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            libroSeleccionado = libro
                            true
                        }
                    })
                }
                vista.invalidate()
            }
        )

        // Atribución obligatoria según la política de uso de OpenStreetMap
        Text(
            "© OpenStreetMap contributors",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )

        if (tienePermiso) {
            SmallFloatingActionButton(
                onClick = {
                    capaUbicacion.myLocation?.let {
                        mapa.controller.animateTo(it, ZOOM_UBICACION, null)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
            }
        }

        libroSeleccionado?.let { libro ->
            TarjetaLibroMapa(
                libro = libro,
                alVerDetalle = { alTocarLibro(libro) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
private fun TarjetaLibroMapa(
    libro: Libro,
    alVerDetalle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
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
                Text(libro.autor, style = MaterialTheme.typography.bodySmall)
                Text(libro.genero.etiqueta, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = alVerDetalle) {
                Text("Ver")
            }
        }
    }
}
