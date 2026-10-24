package com.example.bookswap.data.ubicacion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

fun tienePermisoUbicacion(contexto: Context): Boolean =
    ContextCompat.checkSelfPermission(
        contexto, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                contexto, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

fun abrirAjustesApp(contexto: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", contexto.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    contexto.startActivity(intent)
}