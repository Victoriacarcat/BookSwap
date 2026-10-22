package com.example.bookswap

import android.app.Application
import com.example.bookswap.data.local.BookSwapBaseDatos
import com.example.bookswap.data.repo.LibroRepositorioRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookSwapApp : Application() {

    lateinit var libroRepositorio: LibroRepositorioRoom
        private set

    override fun onCreate() {
        super.onCreate()
        val baseDatos = BookSwapBaseDatos.obtener(this)
        libroRepositorio = LibroRepositorioRoom(baseDatos.libroDao())

        CoroutineScope(Dispatchers.IO).launch {
            libroRepositorio.sembrarSiVacio()
        }
    }
}