package com.example.bookswap.vista.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bookswap.vista.Rutas

@Composable
fun BarraInferior(rutaActual: String?, alNavegar: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == Rutas.HOME,
            onClick = { alNavegar(Rutas.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.MAPA,
            onClick = { alNavegar(Rutas.MAPA) },
            icon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
            label = { Text("Mapa") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.PUBLICAR,
            onClick = { alNavegar(Rutas.PUBLICAR) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Publicar") },
            label = { Text("Publicar") }
        )
        NavigationBarItem(
            selected = rutaActual == Rutas.PERFIL,
            onClick = { alNavegar(Rutas.PERFIL) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}