package com.example.bookswap.data.ubicacion

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine

class ProveedorUbicacion(private val contexto: Context) {

    @SuppressLint("MissingPermission")
    suspend fun obtenerUbicacionActual(): Location? = suspendCancellableCoroutine { continuacion ->
        val cliente = LocationServices.getFusedLocationProviderClient(contexto)
        val fuenteCancelacion = CancellationTokenSource()
        continuacion.invokeOnCancellation { fuenteCancelacion.cancel() }

        cliente.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            fuenteCancelacion.token
        )
            .addOnSuccessListener { ubicacion ->
                if (ubicacion != null) {
                    continuacion.resume(ubicacion) {}
                } else {
                    cliente.lastLocation
                        .addOnSuccessListener { ultima -> continuacion.resume(ultima) {} }
                        .addOnFailureListener { continuacion.resume(null) {} }
                }
            }
            .addOnFailureListener { continuacion.resume(null) {} }
    }
}