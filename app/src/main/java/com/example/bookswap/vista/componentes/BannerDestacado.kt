package com.example.bookswap.vista.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bookswap.data.modelo.Libro
import com.example.bookswap.ui.theme.Mostaza
import com.example.bookswap.ui.theme.TintaOscura
import com.example.bookswap.ui.theme.Papel

@Composable
fun BannerDestacado(libro: Libro, alTocar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = TintaOscura)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Intercambio destacado", color = Mostaza, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(libro.titulo, color = Papel, style = MaterialTheme.typography.titleMedium)
                Text("A ${libro.distanciaTexto} de ti", color = Papel, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = alTocar,
                    colors = ButtonDefaults.buttonColors(containerColor = Mostaza, contentColor = TintaOscura)
                ) {
                    Text("Ver detalle")
                }
            }
            Image(
                painter = painterResource(id = libro.imagenResId),
                contentDescription = libro.titulo,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }
    }
}