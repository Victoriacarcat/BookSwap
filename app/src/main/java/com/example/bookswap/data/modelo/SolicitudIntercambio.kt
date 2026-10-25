// data/modelo/SolicitudIntercambio.kt
package com.example.bookswap.data.modelo

data class SolicitudIntercambio(
    val id: String,
    val idLibroSolicitado: String,
    val idLibroOfrecido: String,
    val idSolicitante: String,
    val idDueño: String,
    val mensaje: String,
    val estado: EstadoSolicitud,
    val fecha: Long,
    val tituloLibroSolicitado: String = "",
    val tituloLibroOfrecido: String = ""
)

enum class EstadoSolicitud(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada")
}
