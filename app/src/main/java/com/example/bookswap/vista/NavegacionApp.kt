package com.example.bookswap.vista

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookswap.vista.componentes.BarraInferior
import com.example.bookswap.vista.componentes.BarraSuperior
import com.example.bookswap.vista.detalle.DetalleVista
import com.example.bookswap.vista.home.HomeVista
import com.example.bookswap.vista.intercambio.SolicitarIntercambioVista
import com.example.bookswap.vista.mapa.MapaVista
import com.example.bookswap.vista.perfil.PerfilVista
import com.example.bookswap.vista.publicar.PublicarVista

@Composable
fun NavegacionApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val rutasPrincipales = setOf(Rutas.HOME, Rutas.MAPA, Rutas.PUBLICAR, Rutas.PERFIL)
    val mostrarBarraInferior = rutaActual in rutasPrincipales

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = {
            if (mostrarBarraInferior) {
                BarraInferior(
                    rutaActual = rutaActual,
                    alNavegar = { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(Rutas.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.HOME) {
                HomeVista(
                    alTocarLibro = { libro ->
                        navController.navigate(Rutas.detalleConId(libro.id))
                    }
                )
            }

            composable(Rutas.MAPA) {
                MapaVista(
                    alTocarLibro = { libro ->
                        navController.navigate(Rutas.detalleConId(libro.id))
                    }
                )
            }
            composable(Rutas.PUBLICAR) {
                PublicarVista(
                    alTerminar = {
                        navController.navigate(Rutas.HOME) {
                            popUpTo(Rutas.HOME)
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Rutas.PERFIL) {
                PerfilVista(
                    alEditarSolicitud = { solicitud ->
                        navController.navigate(
                            Rutas.solicitarConId(solicitud.idLibroSolicitado, solicitud.id)
                        )
                    }
                )
            }

            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument("idLibro") { type = NavType.StringType })
            ) { backStackEntry ->
                val idLibro = backStackEntry.arguments?.getString("idLibro") ?: return@composable
                DetalleVista(
                    idLibro = idLibro,
                    alProponerIntercambio = {
                        navController.navigate(Rutas.solicitarConId(idLibro))
                    },
                    alEditar = {
                        navController.navigate(Rutas.editarLibroConId(idLibro))
                    },
                    alVolver = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.EDITAR_LIBRO,
                arguments = listOf(navArgument("idLibro") { type = NavType.StringType })
            ) { backStackEntry ->
                val idLibro = backStackEntry.arguments?.getString("idLibro") ?: return@composable
                PublicarVista(
                    idLibro = idLibro,
                    alTerminar = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.SOLICITAR,
                arguments = listOf(
                    navArgument("idLibro") { type = NavType.StringType },
                    navArgument("idSolicitud") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val idLibro = backStackEntry.arguments?.getString("idLibro") ?: return@composable
                SolicitarIntercambioVista(
                    idLibro = idLibro,
                    idSolicitud = backStackEntry.arguments?.getString("idSolicitud"),
                    alEnviar = { navController.popBackStack() },
                    alVolver = { navController.popBackStack() },
                    alPublicar = {
                        navController.navigate(Rutas.PUBLICAR) {
                            popUpTo(Rutas.HOME)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}