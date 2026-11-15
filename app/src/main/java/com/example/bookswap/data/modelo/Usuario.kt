// data/modelo/Usuario.kt
package com.example.bookswap.data.modelo

// Dueño que tenían los libros y solicitudes creados antes del inicio de sesión con Google.
// Ahora se usa el uid de Firebase; este valor solo sirve para asociar esos registros a la cuenta
const val ID_USUARIO_SIN_CUENTA = "yo"

data class Usuario(
    val id: String,
    val nombre: String,
    val contacto: String,
    val librosOfrecidosIds: List<String>
)
