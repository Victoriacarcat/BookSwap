package com.example.bookswap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDAO {

    @Query(
        """
        SELECT s.*, ls.titulo AS tituloLibroSolicitado, lo.titulo AS tituloLibroOfrecido
        FROM solicitudes s
        LEFT JOIN libros ls ON ls.id = s.idLibroSolicitado
        LEFT JOIN libros lo ON lo.id = s.idLibroOfrecido
        WHERE s.idSolicitante = :idSolicitante
        ORDER BY s.fecha DESC
        """
    )
    fun observarEnviadas(idSolicitante: String): Flow<List<SolicitudConTitulos>>

    @Query(
        """
        SELECT * FROM solicitudes
        WHERE idLibroSolicitado = :idLibro AND idSolicitante = :idSolicitante AND estado = 'PENDIENTE'
        LIMIT 1
        """
    )
    fun observarPendiente(idLibro: String, idSolicitante: String): Flow<SolicitudEntidad?>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun buscarPorId(id: String): SolicitudEntidad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(solicitud: SolicitudEntidad)

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: String)
}
