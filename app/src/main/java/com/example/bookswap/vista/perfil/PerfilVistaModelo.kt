package com.example.bookswap.vista.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.ID_USUARIO_ACTUAL
import com.example.bookswap.data.modelo.SolicitudIntercambio
import com.example.bookswap.data.repo.SolicitudRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilVistaModelo(
    private val solicitudRepositorio: SolicitudRepositorio
) : ViewModel() {

    val solicitudesEnviadas: StateFlow<List<SolicitudIntercambio>> =
        solicitudRepositorio.observarEnviadas(ID_USUARIO_ACTUAL)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cancelar(solicitud: SolicitudIntercambio) {
        viewModelScope.launch {
            solicitudRepositorio.cancelar(solicitud.id)
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                PerfilVistaModelo(app.solicitudRepositorio)
            }
        }
    }
}
