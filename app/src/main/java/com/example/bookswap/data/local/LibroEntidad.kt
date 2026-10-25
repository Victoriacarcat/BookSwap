package com.example.bookswap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class LibroEntidad(
    @PrimaryKey val id: String,
    val titulo: String,
    val autor: String,
    val genero: String,
    val estado: String,
    val imagenResId: Int,
    val imagenUri: String?,
    val lat: Double,
    val lng: Double,
    val idDueño: String,
    val disponible: Boolean,
    val descripcion: String,
    val notaPublicante: String,
    val fechaPublicacion: Long = System.currentTimeMillis()
)