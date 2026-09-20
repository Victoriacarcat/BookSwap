package com.example.bookswap.vista.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.repo.LibroRepositorioTemp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeVistaModelo(
    private val repositorio: LibroRepositorio = LibroRepositorioTemp()
) : ViewModel() {

    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros.asStateFlow()

    init {
        cargarLibros()
    }

    private fun cargarLibros() {
        viewModelScope.launch {
            _libros.value = repositorio.obtenerLibros()
        }
    }
}