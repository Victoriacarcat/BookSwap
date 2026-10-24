package com.example.bookswap.vista.home

import android.location.Location
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.CategoriaLibro
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.ubicacion.ProveedorUbicacion
import com.example.bookswap.data.ubicacion.distanciaEnMetros
import com.example.bookswap.data.ubicacion.formatearDistancia
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.Normalizer


enum class EstadoUbicacion { BUSCANDO, DISPONIBLE, NO_DISPONIBLE }

private data class LibroConDistancia(val libro: Libro, val metros: Float?)

private fun LibroConDistancia.estaCerca(): Boolean =
    metros == null || metros <= HomeVistaModelo.RADIO_METROS

private fun normalizar(texto: String): String =
    Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()

class HomeVistaModelo(
    repositorio: LibroRepositorio,
    private val proveedorUbicacion: ProveedorUbicacion
) : ViewModel() {

    private val ubicacion = MutableStateFlow<Location?>(null)
    private var ultimaActualizacion = 0L
    private var trabajoUbicacion: Job? = null

    private val _estadoUbicacion = MutableStateFlow(EstadoUbicacion.BUSCANDO)
    val estadoUbicacion: StateFlow<EstadoUbicacion> = _estadoUbicacion.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<CategoriaLibro?>(null)
    val categoriaSeleccionada: StateFlow<CategoriaLibro?> = _categoriaSeleccionada.asStateFlow()

    private val _consulta = MutableStateFlow("")
    val consulta: StateFlow<String> = _consulta.asStateFlow()

    private val _busquedaAmpliada = MutableStateFlow(false)
    val busquedaAmpliada: StateFlow<Boolean> = _busquedaAmpliada.asStateFlow()

    private val conDistancia: Flow<List<LibroConDistancia>> = combine(
        repositorio.observarLibros(),
        ubicacion
    ) { lista, ubicacionActual ->
        if (ubicacionActual == null) {
            lista.map { LibroConDistancia(it, null) }
        } else {
            lista
                .map { libro ->
                    val metros = distanciaEnMetros(
                        ubicacionActual.latitude, ubicacionActual.longitude,
                        libro.lat, libro.lng
                    )
                    LibroConDistancia(
                        libro.copy(distanciaTexto = formatearDistancia(metros)),
                        metros
                    )
                }
                .sortedBy { it.metros }
        }
    }

    val destacado: StateFlow<Libro?> = conDistancia
        .map { lista -> lista.firstOrNull { it.estaCerca() }?.libro }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val libros: StateFlow<List<Libro>> = combine(
        conDistancia,
        _categoriaSeleccionada,
        _consulta,
        _busquedaAmpliada
    ) { todos, categoria, consulta, ampliada ->
        val texto = normalizar(consulta.trim())
        todos
            .filter { ampliada || it.estaCerca() }
            .map { it.libro }
            .filter { categoria == null || it.genero == categoria }
            .filter {
                texto.isEmpty() ||
                        normalizar(it.titulo).contains(texto) ||
                        normalizar(it.autor).contains(texto)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun seleccionarCategoria(categoria: CategoriaLibro) {
        _categoriaSeleccionada.value =
            if (_categoriaSeleccionada.value == categoria) null else categoria
    }

    fun cambiarConsulta(nueva: String) {
        _consulta.value = nueva
        if (nueva.isBlank()) _busquedaAmpliada.value = false
    }

    fun ampliarBusqueda() {
        _busquedaAmpliada.value = true
    }

    fun actualizarUbicacion() {
        val ahora = SystemClock.elapsedRealtime()
        val reciente = ubicacion.value != null &&
                ahora - ultimaActualizacion < INTERVALO_MINIMO_MS
        if (reciente) {
            _estadoUbicacion.value = EstadoUbicacion.DISPONIBLE
            return
        }
        if (trabajoUbicacion?.isActive == true) return

        trabajoUbicacion = viewModelScope.launch {
            if (ubicacion.value == null) _estadoUbicacion.value = EstadoUbicacion.BUSCANDO

            val nueva = withTimeoutOrNull(TIEMPO_MAXIMO_MS) {
                proveedorUbicacion.obtenerUbicacionActual()
            }
            if (nueva != null) {
                ubicacion.value = nueva
                ultimaActualizacion = SystemClock.elapsedRealtime()
            }

            _estadoUbicacion.value =
                if (ubicacion.value != null) EstadoUbicacion.DISPONIBLE
                else EstadoUbicacion.NO_DISPONIBLE
        }
    }

    fun detenerBusquedaUbicacion() {
        trabajoUbicacion?.cancel()
        trabajoUbicacion = null
    }

    fun permisoDenegado() {
        _estadoUbicacion.value = EstadoUbicacion.NO_DISPONIBLE
    }

    companion object {
        const val RADIO_METROS = 10_000f
        private const val TIEMPO_MAXIMO_MS = 10_000L
        private const val INTERVALO_MINIMO_MS = 60_000L

        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                HomeVistaModelo(app.libroRepositorio, ProveedorUbicacion(app))
            }
        }
    }
}