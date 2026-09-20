package com.example.bookswap.vista.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.repo.LibroRepositorioTemp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleVistaModelo(
    private val repositorio: LibroRepositorio = LibroRepositorioTemp(),
) : ViewModel() {

    private val _libro = MutableStateFlow<Libro?>(null)
    val libro: StateFlow<Libro?> = _libro.asStateFlow()

    fun cargarLibro(id: String) {
        viewModelScope.launch {
            _libro.value = repositorio.obtenerLibroPorId(id)
        }
    }
}