package com.example.bookswap.data.repo

import androidx.room.withTransaction
import com.example.bookswap.data.local.BookSwapBaseDatos
import com.example.bookswap.data.modelo.ID_USUARIO_SIN_CUENTA

/**
 * Los libros y solicitudes creados antes del inicio de sesión quedaron a nombre de
 * [ID_USUARIO_SIN_CUENTA]. La primera cuenta que inicia sesión en el dispositivo los adopta;
 * después ya no queda ninguno, así que repetirlo no cambia nada.
 */
class AsociacionDatosLocales(private val baseDatos: BookSwapBaseDatos) {

    suspend fun adoptarDatosSinCuenta(uid: String) {
        baseDatos.withTransaction {
            baseDatos.libroDao().reasignarDueño(ID_USUARIO_SIN_CUENTA, uid)
            baseDatos.solicitudDao().reasignarSolicitante(ID_USUARIO_SIN_CUENTA, uid)
            baseDatos.solicitudDao().reasignarDueño(ID_USUARIO_SIN_CUENTA, uid)
        }
    }
}
