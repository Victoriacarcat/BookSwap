package com.example.bookswap.data.ubicacion

import android.location.Location
import java.util.Locale
import kotlin.math.roundToInt

fun distanciaEnMetros(latA: Double, lngA: Double, latB: Double, lngB: Double): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(latA, lngA, latB, lngB, resultado)
    return resultado[0]
}

fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) {
        "${metros.roundToInt()} m"
    } else {
        String.format(Locale.getDefault(), "%.1f km", metros / 1000f)
    }