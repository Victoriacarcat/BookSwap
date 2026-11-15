package com.example.bookswap

import android.app.Application
import com.example.bookswap.data.auth.AutenticacionRepositorio
import com.example.bookswap.data.local.BookSwapBaseDatos
import com.example.bookswap.data.repo.AsociacionDatosLocales
import com.example.bookswap.data.repo.LibroRepositorioRoom
import com.example.bookswap.data.repo.SolicitudRepositorioRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import java.io.File

class BookSwapApp : Application() {

    lateinit var libroRepositorio: LibroRepositorioRoom
        private set
    lateinit var solicitudRepositorio: SolicitudRepositorioRoom
        private set
    lateinit var autenticacion: AutenticacionRepositorio
        private set

    override fun onCreate() {
        super.onCreate()
        val baseDatos = BookSwapBaseDatos.obtener(this)
        libroRepositorio = LibroRepositorioRoom(baseDatos.libroDao())
        solicitudRepositorio = SolicitudRepositorioRoom(baseDatos.solicitudDao())
        autenticacion = AutenticacionRepositorio(this)
        val asociacionDatos = AsociacionDatosLocales(baseDatos)

        // OpenStreetMap exige identificar la app; la caché de mosaicos va en almacenamiento interno
        Configuration.getInstance().apply {
            userAgentValue = "BookSwap/1.0 (Android; proyecto academico)"
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(osmdroidBasePath, "tiles")
        }

        CoroutineScope(Dispatchers.IO).launch {
            libroRepositorio.sembrarDatosIniciales()
        }

        // Cubre tanto el inicio de sesión nuevo como la sesión que Firebase restaura al abrir la app
        CoroutineScope(Dispatchers.IO).launch {
            autenticacion.usuario
                .map { it?.uid }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { asociacionDatos.adoptarDatosSinCuenta(it) }
        }
    }
}