package com.example.bookswap.data.fuenteData
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.modelo.Usuario
import com.example.bookswap.R

object DatosHardcodeados {
    val libros = listOf(
        Libro("1", "Cien años de soledad", "Gabriel García Márquez", "Novela",
            EstadoLibro.USADO, R.drawable.libro1, 4.6097, -74.0817, "u1"),
        Libro("2", "El Proceso", "Frank Kafka", "Filosófico",
            EstadoLibro.NUEVO, R.drawable.libro2, 4.6182, -74.0648, "u2"),
        Libro("3", "El principito", "Antoine de Saint-Exupéry", "Fábula",
            EstadoLibro.DESGASTADO, R.drawable.libro3, 4.6280, -74.0710, "u3")
    )

    val usuarios = listOf(
        Usuario("u1", "Camila", "camila@email.com", listOf("1")),
        Usuario("u2", "Andrés", "andres@email.com", listOf("2")),
        Usuario("u3", "Luis", "luis@email.com", listOf("3"))
    )
}