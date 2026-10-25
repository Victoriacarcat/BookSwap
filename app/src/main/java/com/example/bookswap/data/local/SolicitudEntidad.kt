package com.example.bookswap.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntidad(
    @PrimaryKey val id: String,
    val idLibroSolicitado: String,
    val idLibroOfrecido: String,
    val idSolicitante: String,
    val idDueño: String,
    val mensaje: String,
    val estado: String,
    val fecha: Long
)

data class SolicitudConTitulos(
    @Embedded val solicitud: SolicitudEntidad,
    val tituloLibroSolicitado: String?,
    val tituloLibroOfrecido: String?
)
