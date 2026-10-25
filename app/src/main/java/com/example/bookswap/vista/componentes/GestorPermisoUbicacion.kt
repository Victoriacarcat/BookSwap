package com.example.bookswap.vista.componentes

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.bookswap.data.ubicacion.tienePermisoUbicacion

@Composable
fun GestorPermisoUbicacion(
    alConcedido: () -> Unit,
    alDenegado: () -> Unit,
    alDetener: () -> Unit
) {
    val contexto = LocalContext.current
    val dueñoCicloVida = LocalLifecycleOwner.current

    val alConcedidoActual by rememberUpdatedState(alConcedido)
    val alDenegadoActual by rememberUpdatedState(alDenegado)
    val alDetenerActual by rememberUpdatedState(alDetener)

    var yaPregunto by rememberSaveable { mutableStateOf(false) }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) alConcedidoActual() else alDenegadoActual()
    }

    DisposableEffect(dueñoCicloVida) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_START -> {
                    when {
                        tienePermisoUbicacion(contexto) -> alConcedidoActual()
                        !yaPregunto -> {
                            yaPregunto = true
                            lanzadorPermiso.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        else -> alDenegadoActual()
                    }
                }
                Lifecycle.Event.ON_STOP -> alDetenerActual()
                else -> Unit
            }
        }
        dueñoCicloVida.lifecycle.addObserver(observador)
        onDispose { dueñoCicloVida.lifecycle.removeObserver(observador) }
    }
}