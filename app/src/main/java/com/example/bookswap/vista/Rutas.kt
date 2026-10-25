package com.example.bookswap.vista

object Rutas {
    const val HOME = "home"
    const val MAPA = "mapa"
    const val PUBLICAR = "publicar"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{idLibro}"
    const val SOLICITAR = "solicitar/{idLibro}?idSolicitud={idSolicitud}"
    const val EDITAR_LIBRO = "editar/{idLibro}"

    fun detalleConId(idLibro: String) = "detalle/$idLibro"
    fun solicitarConId(idLibro: String, idSolicitud: String? = null) =
        if (idSolicitud == null) "solicitar/$idLibro" else "solicitar/$idLibro?idSolicitud=$idSolicitud"
    fun editarLibroConId(idLibro: String) = "editar/$idLibro"
}