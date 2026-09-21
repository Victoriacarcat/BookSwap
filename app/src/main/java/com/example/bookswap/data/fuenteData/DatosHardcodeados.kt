package com.example.bookswap.data.fuenteData
import com.example.bookswap.data.modelo.EstadoLibro
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.data.modelo.Usuario
import com.example.bookswap.R

object DatosHardcodeados {
    val libros = listOf(
        Libro("1", "Cien años de soledad", "Gabriel García Márquez", "Novela",
            EstadoLibro.USADO, R.drawable.libro1, 4.6097, -74.0817, "u1",
            descripcion = "La historia de la familia Buendía a lo largo de siete generaciones en el pueblo mágico de Macondo.",
            notaPublicante = "Me encantó porque mezcla realismo mágico con la historia de Colombia. Lo cuidé bien, solo tiene algo de desgaste en el lomo."),
        Libro("2", "El Proceso", "Frank Kafka", "Filosófico",
            EstadoLibro.NUEVO, R.drawable.libro2, 4.6182, -74.0648, "u2",
            descripcion = "Josef K. es arrestado y procesado por una autoridad inaccesible, sin conocer jamás el motivo de su culpa.",
            notaPublicante = "Lo compré dos veces sin darme cuenta, así que este está sin usar. Ideal si te gustan las historias inquietantes."),
        Libro("3", "El principito", "Antoine de Saint-Exupéry", "Fábula",
            EstadoLibro.DESGASTADO, R.drawable.libro3, 4.6280, -74.0710, "u3",
            descripcion = "Un aviador varado en el desierto conoce a un pequeño príncipe que viene de otro planeta y le enseña sobre la vida.",
            notaPublicante = "Es mi libro de toda la vida, tiene notas mías en los márgenes. Lo suelto porque ya me lo sé de memoria y quiero que otro lo disfrute.")
    )

    val usuarios = listOf(
        Usuario("u1", "Camila", "camila@email.com", listOf("1")),
        Usuario("u2", "Andrés", "andres@email.com", listOf("2")),
        Usuario("u3", "Luis", "luis@email.com", listOf("3"))
    )
}