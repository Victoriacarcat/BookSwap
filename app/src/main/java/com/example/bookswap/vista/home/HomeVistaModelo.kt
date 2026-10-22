package com.example.bookswap.vista.home

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

class HomeVistaModelo(
    repositorio: LibroRepositorio
) : ViewModel() {

    val libros: StateFlow<List<Libro>> = repositorio.observarLibros()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    companion object {
        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                HomeVistaModelo(app.libroRepositorio)
            }
        }
    }
}