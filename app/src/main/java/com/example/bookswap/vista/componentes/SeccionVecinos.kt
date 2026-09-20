package com.example.bookswap.vista.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bookswap.ui.theme.VerdeBosque

@Composable
fun SeccionVecinos(nombres: List<String>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Vecinos con libros",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(nombres) { nombre ->
                Column(
                    modifier = Modifier.width(70.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape),
                        color = VerdeBosque
                    ) {
                        Text(
                            text = nombre.take(1),
                            color = androidx.compose.ui.graphics.Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize().wrapContentHeight()
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = nombre, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
        }
    }
}