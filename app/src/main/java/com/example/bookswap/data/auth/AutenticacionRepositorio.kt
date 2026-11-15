package com.example.bookswap.data.auth

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

// Solo datos de perfil para mostrar; nunca contraseñas ni tokens
data class UsuarioSesion(
    val uid: String,
    val nombre: String?,
    val correo: String?
)

sealed interface ResultadoInicioSesion {
    data object Exito : ResultadoInicioSesion
    data object Cancelado : ResultadoInicioSesion
    data class Error(val mensaje: String) : ResultadoInicioSesion
}

/**
 * Sesión con Google a través de Firebase Authentication. Firebase guarda y renueva la sesión
 * por su cuenta, así que aquí no se persiste nada: la app solo observa [usuario].
 */
class AutenticacionRepositorio(contexto: Context) {

    // Sin google-services.json no se inicializa Firebase y getInstance() lanzaría excepción
    private val firebaseAuth: FirebaseAuth? =
        if (FirebaseApp.getApps(contexto).isNotEmpty()) FirebaseAuth.getInstance() else null

    private val credentialManager = CredentialManager.create(contexto)

    private val depurable = contexto.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    // Lo genera el plugin google-services a partir de google-services.json. Se busca por nombre
    // para que el proyecto compile aunque el archivo todavía no exista
    private val idClienteWeb: String? = contexto.resources
        .getIdentifier("default_web_client_id", "string", contexto.packageName)
        .takeIf { it != 0 }
        ?.let { contexto.getString(it) }

    val configurado: Boolean
        get() = firebaseAuth != null && !idClienteWeb.isNullOrBlank()

    private val _usuario = MutableStateFlow(firebaseAuth?.currentUser?.aSesion())
    val usuario: StateFlow<UsuarioSesion?> = _usuario.asStateFlow()

    init {
        // También avisa si Firebase invalida la sesión (cuenta deshabilitada, token revocado…)
        firebaseAuth?.addAuthStateListener { _usuario.value = it.currentUser?.aSesion() }
    }

    fun idUsuarioActual(): String =
        checkNotNull(_usuario.value?.uid) { "Se pidió el usuario actual sin una sesión iniciada" }

    // contextoActividad debe ser una Activity: Credential Manager muestra su propia interfaz encima
    suspend fun iniciarConGoogle(contextoActividad: Context): ResultadoInicioSesion {
        val auth = firebaseAuth
        val idCliente = idClienteWeb
        if (auth == null || idCliente.isNullOrBlank()) {
            return ResultadoInicioSesion.Error(MENSAJE_NO_CONFIGURADO)
        }

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(idCliente).build())
            .build()

        return try {
            val credencial = credentialManager.getCredential(contextoActividad, solicitud).credential
            if (credencial !is CustomCredential ||
                credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                return ResultadoInicioSesion.Error(MENSAJE_GENERICO)
            }
            val tokenGoogle = GoogleIdTokenCredential.createFrom(credencial.data).idToken
            auth.signInWithCredential(GoogleAuthProvider.getCredential(tokenGoogle, null)).await()
            ResultadoInicioSesion.Exito
        } catch (e: GetCredentialCancellationException) {
            ResultadoInicioSesion.Cancelado
        } catch (e: NoCredentialException) {
            registrarFallo(e)
            ResultadoInicioSesion.Error("No hay una cuenta de Google disponible en este dispositivo.")
        } catch (e: GetCredentialException) {
            registrarFallo(e)
            ResultadoInicioSesion.Error(MENSAJE_GENERICO)
        } catch (e: GoogleIdTokenParsingException) {
            registrarFallo(e)
            ResultadoInicioSesion.Error(MENSAJE_GENERICO)
        } catch (e: FirebaseNetworkException) {
            registrarFallo(e)
            ResultadoInicioSesion.Error("Sin conexión a internet. Revisa tu red e inténtalo de nuevo.")
        } catch (e: FirebaseException) {
            registrarFallo(e)
            ResultadoInicioSesion.Error(MENSAJE_GENERICO)
        }
    }

    // Solo en compilaciones de depuración y solo el tipo y código del error: nunca tokens ni correos
    private fun registrarFallo(e: Exception) {
        if (!depurable) return
        val detalle = when (e) {
            is GetCredentialException -> "${e.type} ${e.errorMessage ?: ""}"
            is FirebaseAuthException -> e.errorCode
            else -> ""
        }
        Log.w(ETIQUETA_LOG, "Falló el inicio de sesión: ${e.javaClass.simpleName} $detalle")
    }

    suspend fun cerrarSesion() {
        firebaseAuth?.signOut()
        // Olvida la cuenta elegida para que el próximo inicio vuelva a preguntar cuál usar
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: ClearCredentialException) {
            // La sesión de Firebase ya se cerró; esto solo afecta la cuenta sugerida
        }
    }

    /**
     * ID token de Firebase para un servidor propio, que debe verificarlo con el Admin SDK.
     * Se envía como "Authorization: Bearer <token>" solo a ese servidor; no se guarda ni se registra.
     * BookSwap aún no tiene servidor propio, por eso no se usa en ningún lado.
     */
    suspend fun obtenerIdToken(forzarRenovacion: Boolean = false): String? =
        firebaseAuth?.currentUser?.getIdToken(forzarRenovacion)?.await()?.token

    private fun FirebaseUser.aSesion() = UsuarioSesion(
        uid = uid,
        nombre = displayName,
        correo = email
    )

    private companion object {
        const val ETIQUETA_LOG = "BookSwapAuth"
        const val MENSAJE_GENERICO = "No se pudo iniciar sesión con Google. Inténtalo de nuevo."
        const val MENSAJE_NO_CONFIGURADO =
            "El inicio de sesión todavía no está configurado en esta versión de la app."
    }
}
