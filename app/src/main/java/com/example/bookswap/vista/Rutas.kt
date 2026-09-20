package com.example.bookswap.vista

object Rutas {
    const val HOME = "home"
    const val MAPA = "mapa"
    const val PUBLICAR = "publicar"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{idLibro}"

    fun detalleConId(idLibro: String) = "detalle/$idLibro"
}