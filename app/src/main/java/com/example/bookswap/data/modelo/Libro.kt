// data/modelo/Libro.kt
package com.example.bookswap.data.modelo


data class Libro(
    val id: String,
    val titulo: String,
    val autor: String,
    val genero: String,
    val estado: EstadoLibro,
    val imagenResId: Int,
    val lat: Double,
    val lng: Double,
    val idDueño: String,
    val disponible: Boolean = true,
    val distanciaTexto: String = "0.5 km"
)

enum class EstadoLibro {
    NUEVO, USADO, DESGASTADO
}
