package com.example.bookswap.data.imagenes

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class AlmacenImagenes(private val contexto: Context) {

    suspend fun guardar(origen: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val carpeta = File(contexto.filesDir, "portadas").apply { mkdirs() }
            val destino = File(carpeta, "${UUID.randomUUID()}.jpg")
            contexto.contentResolver.openInputStream(origen)?.use { entrada ->
                destino.outputStream().use { salida -> entrada.copyTo(salida) }
            } ?: return@runCatching null
            destino.absolutePath
        }.getOrNull()
    }
}