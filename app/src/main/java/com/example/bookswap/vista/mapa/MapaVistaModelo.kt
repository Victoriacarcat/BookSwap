// vista/mapa/MapaVistaModelo.kt
package com.example.bookswap.vista.mapa

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookswap.data.ubicacion.ProveedorUbicacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapaVistaModelo : ViewModel() {

    private val _ubicacion = MutableStateFlow<Location?>(null)
    val ubicacion: StateFlow<Location?> = _ubicacion.asStateFlow()

    fun cargarUbicacion(proveedor: ProveedorUbicacion) {
        viewModelScope.launch {
            _ubicacion.value = proveedor.obtenerUbicacionActual()
        }
    }
}