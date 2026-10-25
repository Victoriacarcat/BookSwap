// data/modelo/Usuario.kt
package com.example.bookswap.data.modelo

const val ID_USUARIO_ACTUAL = "yo"

data class Usuario(
    val id: String,
    val nombre: String,
    val contacto: String,
    val librosOfrecidosIds: List<String>
)
