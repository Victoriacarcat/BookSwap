package com.example.bookswap.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VerdeBosque,
    secondary = Mostaza,
    tertiary = VerdeBosqueOscuro,
    background = TintaOscura,
    surface = TintaOscura,
    onPrimary = Blanco,
    onSecondary = TintaOscura,
    onBackground = Papel,
    onSurface = Papel
)

// ui/theme/Theme.kt (ajusta el LightColorScheme, deja el resto del archivo igual)
private val LightColorScheme = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = Blanco,
    secondary = Mostaza,
    onSecondary = TintaOscura,
    background = Papel,
    onBackground = TintaOscura,
    surface = Blanco,
    onSurface = TintaOscura,
    surfaceVariant = Papel,
    onSurfaceVariant = GrisCalido
)

@Composable
fun BookSwapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}