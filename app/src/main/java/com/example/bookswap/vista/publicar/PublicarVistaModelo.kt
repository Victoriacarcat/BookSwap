package com.example.bookswap.vista.publicar

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookswap.BookSwapApp
import com.example.bookswap.data.imagenes.AlmacenImagenes
import com.example.bookswap.data.modelo.CategoriaLibro
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.repo.LibroRepositorio
import com.example.bookswap.data.ubicacion.ProveedorUbicacion
import kotlinx.coroutines.launch
import java.util.UUID
import kotlinx.coroutines.withTimeoutOrNull

class PublicarVistaModelo(
    private val repositorio: LibroRepositorio,
    private val almacenImagenes: AlmacenImagenes,
    private val proveedorUbicacion: ProveedorUbicacion
) : ViewModel() {

    var titulo by mutableStateOf("")
    var autor by mutableStateOf("")
    var genero by mutableStateOf<CategoriaLibro?>(null)
    var estado by mutableStateOf(EstadoLibro.USADO)
    var descripcion by mutableStateOf("")
    var notaPublicante by mutableStateOf("")
    var fotoUri by mutableStateOf<Uri?>(null)

    var guardando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val formularioValido: Boolean
        get() = titulo.isNotBlank() &&
                autor.isNotBlank() &&
                genero != null &&
                fotoUri != null

    val puedePublicar: Boolean
        get() = formularioValido && !guardando

    fun reportarError(mensaje: String) {
        error = mensaje
    }

    fun publicar(alTerminar: () -> Unit) {
        val foto = fotoUri
        val categoria = genero
        if (!puedePublicar || foto == null || categoria == null) return

        guardando = true
        error = null

        viewModelScope.launch {
            val ubicacion = withTimeoutOrNull(10_000L) {
                proveedorUbicacion.obtenerUbicacionActual()
            }
            if (ubicacion == null) {
                error = "No se pudo obtener tu ubicación. Activa el GPS e inténtalo de nuevo."
                guardando = false
                return@launch
            }

            val rutaFoto = almacenImagenes.guardar(foto)
            if (rutaFoto == null) {
                error = "No se pudo guardar la portada. Elige la foto de nuevo."
                guardando = false
                return@launch
            }

            val libro = Libro(
                id = UUID.randomUUID().toString(),
                titulo = titulo.trim(),
                autor = autor.trim(),
                genero = categoria,
                estado = estado,
                imagenResId = 0,
                lat = ubicacion.latitude,
                lng = ubicacion.longitude,
                idDueño = ID_USUARIO_ACTUAL,
                descripcion = descripcion.trim(),
                notaPublicante = notaPublicante.trim(),
                imagenUri = rutaFoto
            )
            repositorio.publicarLibro(libro)
            limpiar()
            guardando = false
            alTerminar()
        }
    }

    private fun limpiar() {
        titulo = ""
        autor = ""
        genero = null
        estado = EstadoLibro.USADO
        descripcion = ""
        notaPublicante = ""
        fotoUri = null
    }

    companion object {
        const val ID_USUARIO_ACTUAL = "yo"

        val Fabrica = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookSwapApp
                PublicarVistaModelo(
                    app.libroRepositorio,
                    AlmacenImagenes(app),
                    ProveedorUbicacion(app)
                )
            }
        }
    }
}