// data/modelo/Libro.kt
package com.example.bookswap.data.modelo

data class Libro(
    val id: String,
    val titulo: String,
    val autor: String,
    val genero: CategoriaLibro,
    val estado: EstadoLibro,
    val imagenResId: Int,
    val lat: Double,
    val lng: Double,
    val idDueño: String,
    val disponible: Boolean = true,
    val distanciaTexto: String = "",
    val descripcion: String = "",
    val notaPublicante: String = "",
    val imagenUri: String? = null
)

enum class EstadoLibro {
    NUEVO, USADO, DESGASTADO
}

enum class CategoriaLibro(val etiqueta: String) {
    NOVELA("Novela"),
    DISTOPIA("Distopía"),
    FABULA("Fábula"),
    CIENCIA("Ciencia"),
    POESIA("Poesía"),
    FILOSOFICO("Filosófico"),
    OTRO("Otro")
}