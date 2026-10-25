package com.example.fitpulsegym.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightScheme =
  lightColorScheme(
    primary = FitEmerald,
    onPrimary = Color.White,
    primaryContainer = FitLime,
    onPrimaryContainer = FitDark,
    secondary = FitOrangeDeep,
    onSecondary = Color.White,
    secondaryContainer = FitOrangeSoft,
    onSecondaryContainer = Color(0xFF4A1E0A),
    tertiary = Color(0xFF3E7C59),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD9EDDE),
    onTertiaryContainer = FitDeepGreen,
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = Color(0xFF410002),
    background = MistBg,
    onBackground = InkPrimary,
    surface = MistSurface,
    onSurface = InkPrimary,
    surfaceVariant = MistSurfaceVariant,
    onSurfaceVariant = InkSecondary,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF9FAF5),
    surfaceContainer = Color(0xFFEDF1E6),
    surfaceContainerHigh = Color(0xFFE6EBDD),
    surfaceContainerHighest = Color(0xFFDFE5D4),
    outline = MistOutline,
    outlineVariant = Color(0xFFE3E8D9),
    scrim = Color(0x52000000),
  )

private val DarkScheme =
  darkColorScheme(
    primary = FitLime,
    onPrimary = FitDark,
    primaryContainer = FitLimeMuted,
    onPrimaryContainer = FitLimeSoft,
    secondary = FitOrange,
    onSecondary = FitDark,
    secondaryContainer = Color(0xFF4A2413),
    onSecondaryContainer = FitOrangeSoft,
    tertiary = Color(0xFF8FD0A3),
    onTertiary = FitDeepGreen,
    tertiaryContainer = Color(0xFF1C3A28),
    onTertiaryContainer = Color(0xFFD9EDDE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = NightBg,
    onBackground = SnowPrimary,
    surface = NightSurface,
    onSurface = SnowPrimary,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = SnowSecondary,
    surfaceContainerLowest = Color(0xFF0A0E0C),
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightSurfaceVariant,
    surfaceContainerHigh = Color(0xFF243129),
    surfaceContainerHighest = Color(0xFF2E3D34),
    outline = NightOutline,
    outlineVariant = Color(0xFF223028),
    scrim = Color(0x99000000),
  )

val FitShapes =
  Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
  )

@Composable
fun FitPulseGymTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Brand-first: keep dynamic color OFF so Lime / Forest identity survives on all devices.
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  @Suppress("UNUSED_EXPRESSION")
  if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    // Intentionally ignored — brand palette wins over wallpaper colors.
  }
  val colorScheme = if (darkTheme) DarkScheme else LightScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, shapes = FitShapes, content = content)
}
