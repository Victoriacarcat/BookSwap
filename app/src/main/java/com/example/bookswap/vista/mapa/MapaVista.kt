// vista/mapa/MapaVista.kt
package com.example.bookswap.vista.mapa

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookswap.data.ubicacion.ProveedorUbicacion

@Composable
fun MapaVista(vistaModelo: MapaVistaModelo = viewModel()) {
    val contexto = LocalContext.current
    val proveedor = remember { ProveedorUbicacion(contexto) }
    val ubicacion by vistaModelo.ubicacion.collectAsState()

    var tienePermiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido -> tienePermiso = concedido }

    LaunchedEffect(Unit) {
        if (!tienePermiso) {
            lanzadorPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(tienePermiso) {
        if (tienePermiso) {
            vistaModelo.cargarUbicacion(proveedor)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            !tienePermiso -> Text("Se necesita permiso de ubicación para ver libros cerca de ti")
            ubicacion == null -> CircularProgressIndicator()
            else -> Text("Lat: ${ubicacion!!.latitude}, Lng: ${ubicacion!!.longitude}")
        }
    }
}