package com.example.bookswap.data.repo

import com.example.bookswap.data.local.SolicitudDAO
import com.example.bookswap.data.local.aDominio
import com.example.bookswap.data.local.aEntidad
import com.example.bookswap.data.modelo.EstadoSolicitud
import com.example.bookswap.data.modelo.SolicitudIntercambio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SolicitudRepositorioRoom(
    private val solicitudDao: SolicitudDAO
) : SolicitudRepositorio {

    override fun observarEnviadas(idSolicitante: String): Flow<List<SolicitudIntercambio>> =
        solicitudDao.observarEnviadas(idSolicitante).map { filas ->
            filas.map { it.aDominio() }
        }

    override fun observarPendiente(idLibro: String, idSolicitante: String): Flow<SolicitudIntercambio?> =
        solicitudDao.observarPendiente(idLibro, idSolicitante).map { it?.aDominio() }

    override suspend fun obtenerSolicitud(id: String): SolicitudIntercambio? =
        solicitudDao.buscarPorId(id)?.aDominio()

    override suspend fun enviar(solicitud: SolicitudIntercambio) {
        solicitudDao.insertar(solicitud.aEntidad())
    }

    // Mismo id: REPLACE sobrescribe la fila conservando la fecha original
    override suspend fun actualizar(solicitud: SolicitudIntercambio) {
        solicitudDao.insertar(solicitud.aEntidad())
    }

    override suspend fun cancelar(id: String) {
        solicitudDao.actualizarEstado(id, EstadoSolicitud.CANCELADA.name)
    }
}
