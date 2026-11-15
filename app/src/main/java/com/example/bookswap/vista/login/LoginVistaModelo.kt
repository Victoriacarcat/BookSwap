package com.example.bookswap.vista.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.auth.AutenticacionRepositorio
import com.example.bookswap.data.auth.ResultadoInicioSesion
import kotlinx.coroutines.launch

class LoginVistaModelo(
    private val autenticacion: AutenticacionRepositorio
) : ViewModel() {

    val configurado: Boolean = autenticacion.configurado

    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Al tener éxito no hace falta navegar desde aquí: NavegacionApp reacciona a la sesión
    fun iniciarConGoogle(contextoActividad: Context) {
        if (cargando) return
        cargando = true
        error = null
        viewModelScope.launch {
            val resultado = autenticacion.iniciarConGoogle(contextoActividad)
            if (resultado is ResultadoInicioSesion.Error) {
                error = resultado.mensaje
            }
            cargando = false
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                LoginVistaModelo(app.autenticacion)
            }
        }
    }
}
