package com.example.bookswap.vista.intercambio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.EstadoSolicitud
import com.example.bookswap.data.modelo.ID_USUARIO_ACTUAL
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.modelo.SolicitudIntercambio
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.repo.SolicitudRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class SolicitarIntercambioVistaModelo(
    private val libroRepositorio: LibroRepositorio,
    private val solicitudRepositorio: SolicitudRepositorio
) : ViewModel() {

    private val _libroSolicitado = MutableStateFlow<Libro?>(null)
    val libroSolicitado: StateFlow<Libro?> = _libroSolicitado.asStateFlow()

    // null mientras carga, para no mostrar "no tienes libros" antes de tiempo
    val misLibros: StateFlow<List<Libro>?> = libroRepositorio.observarLibrosDe(ID_USUARIO_ACTUAL)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    var libroOfrecido by mutableStateOf<Libro?>(null)
    var mensaje by mutableStateOf("")

    var enviando by mutableStateOf(false)
        private set

    // Solicitud que se está editando; null cuando es una solicitud nueva
    private var solicitudOriginal: SolicitudIntercambio? = null

    val puedeEnviar: Boolean
        get() = libroOfrecido != null && !enviando

    fun cargar(idLibro: String, idSolicitud: String?) {
        if (_libroSolicitado.value?.id == idLibro) return
        viewModelScope.launch {
            if (idSolicitud != null) {
                solicitudRepositorio.obtenerSolicitud(idSolicitud)?.let { solicitud ->
                    solicitudOriginal = solicitud
                    mensaje = solicitud.mensaje
                    libroOfrecido = libroRepositorio.obtenerLibroPorId(solicitud.idLibroOfrecido)
                }
            }
            _libroSolicitado.value = libroRepositorio.obtenerLibroPorId(idLibro)
        }
    }

    fun enviar(alTerminar: () -> Unit) {
        val solicitado = _libroSolicitado.value
        val ofrecido = libroOfrecido
        if (!puedeEnviar || solicitado == null || ofrecido == null) return

        enviando = true
        viewModelScope.launch {
            val original = solicitudOriginal
            if (original != null) {
                solicitudRepositorio.actualizar(
                    original.copy(idLibroOfrecido = ofrecido.id, mensaje = mensaje.trim())
                )
                enviando = false
                alTerminar()
                return@launch
            }

            solicitudRepositorio.enviar(
                SolicitudIntercambio(
                    id = UUID.randomUUID().toString(),
                    idLibroSolicitado = solicitado.id,
                    idLibroOfrecido = ofrecido.id,
                    idSolicitante = ID_USUARIO_ACTUAL,
                    idDueño = solicitado.idDueño,
                    mensaje = mensaje.trim(),
                    estado = EstadoSolicitud.PENDIENTE,
                    fecha = System.currentTimeMillis()
                )
            )
            enviando = false
            alTerminar()
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                SolicitarIntercambioVistaModelo(app.libroRepositorio, app.solicitudRepositorio)
            }
        }
    }
}
