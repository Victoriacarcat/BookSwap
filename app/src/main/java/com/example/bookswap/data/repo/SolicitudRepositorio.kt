package com.example.bookswap.data.repo

import com.example.bookswap.data.modelo.SolicitudIntercambio
import kotlinx.coroutines.flow.Flow

interface SolicitudRepositorio {
    fun observarEnviadas(idSolicitante: String): Flow<List<SolicitudIntercambio>>
    fun observarPendiente(idLibro: String, idSolicitante: String): Flow<SolicitudIntercambio?>
    suspend fun obtenerSolicitud(id: String): SolicitudIntercambio?
    suspend fun enviar(solicitud: SolicitudIntercambio)
    suspend fun actualizar(solicitud: SolicitudIntercambio)
    suspend fun cancelar(id: String)
}
