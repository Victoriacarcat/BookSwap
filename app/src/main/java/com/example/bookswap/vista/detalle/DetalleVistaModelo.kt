package com.example.bookswap.vista.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleVistaModelo(
    private val repositorio: LibroRepositorio
) : ViewModel() {

    private val _libro = MutableStateFlow<Libro?>(null)
    val libro: StateFlow<Libro?> = _libro.asStateFlow()

    fun cargarLibro(id: String) {
        viewModelScope.launch {
            _libro.value = repositorio.obtenerLibroPorId(id)
        }
    }

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                DetalleVistaModelo(app.libroRepositorio)
            }
        }
    }
}