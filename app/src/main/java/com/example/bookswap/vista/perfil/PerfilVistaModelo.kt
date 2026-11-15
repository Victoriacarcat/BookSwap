package com.example.bookswap.vista.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.auth.AutenticacionRepositorio
import com.example.bookswap.data.auth.UsuarioSesion
import com.example.bookswap.data.modelo.SolicitudIntercambio
import com.example.bookswap.data.repo.SolicitudRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilVistaModelo(
    private val solicitudRepositorio: SolicitudRepositorio,
    private val autenticacion: AutenticacionRepositorio
) : ViewModel() {

    val usuario: StateFlow<UsuarioSesion?> = autenticacion.usuario

    val solicitudesEnviadas: StateFlow<List<SolicitudIntercambio>> =
        solicitudRepositorio.observarEnviadas(autenticacion.idUsuarioActual())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cancelar(solicitud: SolicitudIntercambio) {
        viewModelScope.launch {
            solicitudRepositorio.cancelar(solicitud.id)
        }
    }

    // La navegación vuelve sola al login cuando la sesión deja de existir
    fun cerrarSesion() {
        viewModelScope.launch {
            autenticacion.cerrarSesion()
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                PerfilVistaModelo(app.solicitudRepositorio, app.autenticacion)
            }
        }
    }
}
