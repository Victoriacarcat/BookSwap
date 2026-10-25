package com.example.bookswap.vista.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.ID_USUARIO_ACTUAL
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.modelo.SolicitudIntercambio
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.repo.SolicitudRepositorio
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class DetalleVistaModelo(
    repositorio: LibroRepositorio,
    solicitudRepositorio: SolicitudRepositorio
) : ViewModel() {

    private val idLibro = MutableStateFlow<String?>(null)

    // Se observa (no se lee una sola vez) para reflejar los cambios al volver de editar
    @OptIn(ExperimentalCoroutinesApi::class)
    val libro: StateFlow<Libro?> = idLibro
        .filterNotNull()
        .flatMapLatest { repositorio.observarLibro(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val solicitudPendiente: StateFlow<SolicitudIntercambio?> = idLibro
        .filterNotNull()
        .flatMapLatest { solicitudRepositorio.observarPendiente(it, ID_USUARIO_ACTUAL) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun cargarLibro(id: String) {
        idLibro.value = id
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                DetalleVistaModelo(app.libroRepositorio, app.solicitudRepositorio)
            }
        }
    }
}
