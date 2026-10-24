package com.example.bookswap.vista.componentes

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.example.bookswap.data.modelo.Libro
import java.io.File

@Composable
fun PortadaLibro(
    libro: Libro,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (libro.imagenUri != null) {
        AsyncImage(
            model = File(libro.imagenUri),
            contentDescription = libro.titulo,
            modifier = modifier,
            contentScale = contentScale
        )
    } else if (libro.imagenResId != 0) {
        Image(
            painter = painterResource(id = libro.imagenResId),
            contentDescription = libro.titulo,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}