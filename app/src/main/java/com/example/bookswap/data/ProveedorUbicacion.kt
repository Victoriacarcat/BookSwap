package com.example.bookswap.data.ubicacion

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
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

        // Una sola lectura. HIGH_ACCURACY permite usar el GPS: con BALANCED, si la ubicación
        // por red (Wi-Fi/antenas) está apagada, nunca llega respuesta. Una ubicación de hace
        // menos de 1 minuto sirve, y se corta a los 8 s para que alcance a probar lastLocation
        // antes del límite de 10 s de los ViewModels
        val solicitud = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(60_000)
            .setDurationMillis(8_000)
            .build()

        cliente.getCurrentLocation(solicitud, fuenteCancelacion.token)
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