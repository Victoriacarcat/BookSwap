// data/ubicacion/ProveedorUbicacion.kt
package com.example.bookswap.data.ubicacion

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine

class ProveedorUbicacion(private val contexto: Context) {

    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(): Location? = suspendCancellableCoroutine { continuacion ->
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        cliente.lastLocation
            .addOnSuccessListener { ubicacion -> continuacion.resume(ubicacion) {} }
            .addOnFailureListener { continuacion.resume(null) {} }
    }
}