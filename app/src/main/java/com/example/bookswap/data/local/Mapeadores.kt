package com.example.bookswap.data.local

import com.example.bookswap.data.modelo.CategoriaLibro
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.modelo.EstadoSolicitud
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.modelo.SolicitudIntercambio

fun LibroEntidad.aDominio(): Libro = Libro(
    id = id,
    titulo = titulo,
    autor = autor,
    genero = runCatching { CategoriaLibro.valueOf(genero) }.getOrDefault(CategoriaLibro.OTRO),
    estado = EstadoLibro.valueOf(estado),
    imagenResId = imagenResId,
    lat = lat,
    lng = lng,
    idDueño = idDueño,
    disponible = disponible,
    descripcion = descripcion,
    notaPublicante = notaPublicante,
    imagenUri = imagenUri
)

fun Libro.aEntidad(): LibroEntidad = LibroEntidad(
    id = id,
    titulo = titulo,
    autor = autor,
    genero = genero.name,
    estado = estado.name,
    imagenResId = imagenResId,
    imagenUri = imagenUri,
    lat = lat,
    lng = lng,
    idDueño = idDueño,
    disponible = disponible,
    descripcion = descripcion,
    notaPublicante = notaPublicante
)

fun SolicitudEntidad.aDominio(): SolicitudIntercambio = SolicitudIntercambio(
    id = id,
    idLibroSolicitado = idLibroSolicitado,
    idLibroOfrecido = idLibroOfrecido,
    idSolicitante = idSolicitante,
    idDueño = idDueño,
    mensaje = mensaje,
    estado = runCatching { EstadoSolicitud.valueOf(estado) }.getOrDefault(EstadoSolicitud.PENDIENTE),
    fecha = fecha
)

fun SolicitudConTitulos.aDominio(): SolicitudIntercambio = solicitud.aDominio().copy(
    tituloLibroSolicitado = tituloLibroSolicitado.orEmpty(),
    tituloLibroOfrecido = tituloLibroOfrecido.orEmpty()
)

fun SolicitudIntercambio.aEntidad(): SolicitudEntidad = SolicitudEntidad(
    id = id,
    idLibroSolicitado = idLibroSolicitado,
    idLibroOfrecido = idLibroOfrecido,
    idSolicitante = idSolicitante,
    idDueño = idDueño,
    mensaje = mensaje,
    estado = estado.name,
    fecha = fecha
)
