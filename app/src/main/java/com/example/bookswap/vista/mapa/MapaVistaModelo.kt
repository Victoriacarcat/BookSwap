// vista/mapa/MapaVistaModelo.kt
package com.example.bookswap.vista.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MapaVistaModelo(
    repositorio: LibroRepositorio
) : ViewModel() {

    val libros: StateFlow<List<Libro>> = repositorio.observarLibros()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                MapaVistaModelo(app.libroRepositorio)
            }
        }
    }
}
