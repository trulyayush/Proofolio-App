package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ============================================================================
// NEXUSINTERN DESIGN SYSTEM TOKEN DEFINITIONS: Color, Spacing, Typography, Elevation
// ============================================================================

/**
 * NexusIntern Color Tokens
 * Quiet confidence: warm neutral canvas, crisp surfaces, purposeful violet accent, semantic states.
 */
@Immutable
data class NexusColorTokens(
    val background: Color = NexusTokens.Background,
    val surface: Color = NexusTokens.Surface,
    val surfaceSecondary: Color = NexusTokens.SurfaceSecondary,
    val foreground: Color = NexusTokens.Foreground,
    val foregroundSecondary: Color = NexusTokens.ForegroundSecondary,
    val foregroundTertiary: Color = NexusTokens.ForegroundTertiary,
    val border: Color = NexusTokens.Border,
    val borderStrong: Color = NexusTokens.BorderStrong,
    val accent: Color = NexusTokens.Accent,
    val accentHover: Color = NexusTokens.AccentHover,
    val accentSubtle: Color = NexusTokens.AccentSubtle,
    val onAccent: Color = NexusTokens.OnAccent,

    // Semantic Status Colors
    val successText: Color = NexusTokens.SuccessText,
    val successBackground: Color = NexusTokens.SuccessBackground,
    val successBorder: Color = NexusTokens.SuccessBorder,

    val warningText: Color = NexusTokens.WarningText,
    val warningBackground: Color = NexusTokens.WarningBackground,
    val warningBorder: Color = NexusTokens.WarningBorder,

    val errorText: Color = NexusTokens.ErrorText,
    val errorBackground: Color = NexusTokens.ErrorBackground,
    val errorBorder: Color = NexusTokens.ErrorBorder,

    val infoText: Color = NexusTokens.InfoText,
    val infoBackground: Color = NexusTokens.InfoBackground,
    val infoBorder: Color = NexusTokens.InfoBorder
)

/**
 * NexusIntern Spacing Tokens based on the strict 4px baseline grid
 */
@Immutable
data class NexusSpacingTokens(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 40.dp,
    val xxxl: Dp = 48.dp,
    val huge: Dp = 64.dp
)

/**
 * NexusIntern Typography Tokens
 * Strong typography with Inter font family, clear hierarchy and comfortable reading rhythm.
 */
@Immutable
data class NexusTypographyTokens(
    val display: TextStyle = Typography.displayLarge,
    val h1: TextStyle = Typography.headlineLarge,
    val h2: TextStyle = Typography.headlineMedium,
    val h3: TextStyle = Typography.titleLarge,
    val titleMedium: TextStyle = Typography.titleMedium,
    val titleSmall: TextStyle = Typography.titleSmall,
    val bodyLarge: TextStyle = Typography.bodyLarge,
    val body: TextStyle = Typography.bodyMedium,
    val bodySmall: TextStyle = Typography.bodySmall,
    val label: TextStyle = Typography.labelLarge,
    val labelMedium: TextStyle = Typography.labelMedium,
    val caption: TextStyle = Typography.labelSmall
)

/**
 * NexusIntern Elevation Tokens (Subtle depth, controlled lighting, restrained motion)
 */
@Immutable
data class NexusElevationTokens(
    val none: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 2.dp,
    val level3: Dp = 4.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp
)

/**
 * NexusIntern Corner Radius Tokens
 */
@Immutable
data class NexusCornerTokens(
    val sm: Dp = 6.dp,
    val md: Dp = 10.dp,
    val lg: Dp = 14.dp,
    val surface: Dp = 18.dp,
    val pill: Dp = 100.dp
)

// ============================================================================
// COMPOSITION LOCALS
// ============================================================================

val LocalNexusColors = staticCompositionLocalOf { NexusColorTokens() }
val LocalNexusSpacing = staticCompositionLocalOf { NexusSpacingTokens() }
val LocalNexusTypography = staticCompositionLocalOf { NexusTypographyTokens() }
val LocalNexusElevation = staticCompositionLocalOf { NexusElevationTokens() }
val LocalNexusCorners = staticCompositionLocalOf { NexusCornerTokens() }

/**
 * Convenient accessor object for NexusIntern design system tokens
 */
object NexusTheme {
    val colors: NexusColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNexusColors.current

    val spacing: NexusSpacingTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNexusSpacing.current

    val typography: NexusTypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNexusTypography.current

    val elevation: NexusElevationTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNexusElevation.current

    val corners: NexusCornerTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalNexusCorners.current
}

// ============================================================================
// MATERIAL 3 COLOR SCHEME & SHAPES INTEGRATION
// ============================================================================

private val NexusLightColorScheme = lightColorScheme(
    primary = NexusTokens.Accent,
    onPrimary = NexusTokens.OnAccent,
    primaryContainer = NexusTokens.AccentSubtle,
    onPrimaryContainer = NexusTokens.AccentHover,
    secondary = NexusTokens.ForegroundSecondary,
    onSecondary = Color.White,
    secondaryContainer = NexusTokens.SurfaceSecondary,
    onSecondaryContainer = NexusTokens.Foreground,
    tertiary = NexusTokens.AccentHover,
    onTertiary = Color.White,
    background = NexusTokens.Background,
    onBackground = NexusTokens.Foreground,
    surface = NexusTokens.Surface,
    onSurface = NexusTokens.Foreground,
    surfaceVariant = NexusTokens.SurfaceSecondary,
    onSurfaceVariant = NexusTokens.ForegroundSecondary,
    outline = NexusTokens.Border,
    outlineVariant = NexusTokens.BorderStrong,
    error = NexusTokens.ErrorText,
    onError = Color.White,
    errorContainer = NexusTokens.ErrorBackground,
    onErrorContainer = NexusTokens.ErrorText
)

private val NexusShapes = Shapes(
    extraSmall = RoundedCornerShape(NexusTokens.Radius.sm),
    small = RoundedCornerShape(NexusTokens.Radius.md),
    medium = RoundedCornerShape(NexusTokens.Radius.lg),
    large = RoundedCornerShape(NexusTokens.Radius.surface),
    extraLarge = RoundedCornerShape(NexusTokens.Radius.pill)
)

// ============================================================================
// ROOT APPLICATION THEME
// ============================================================================

/**
 * Root theme for the NexusIntern application.
 * Integrates color scheme, typography, shapes, and custom design tokens (color, spacing, typography, elevation).
 */
@Composable
fun MyApplicationTheme(
    isDarkMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorTokens = if (isDarkMode) {
        NexusColorTokens(
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            surfaceSecondary = Color(0xFF27272A),
            foreground = Color(0xFFF4F4F5),
            foregroundSecondary = Color(0xFFA1A1AA),
            foregroundTertiary = Color(0xFF71717A),
            border = Color(0xFF3F3F46),
            borderStrong = Color(0xFF52525B),
            accent = Color(0xFF818CF8),
            accentHover = Color(0xFF6366F1),
            accentSubtle = Color(0xFF1E1B4B),
            onAccent = Color(0xFFFFFFFF)
        )
    } else {
        NexusColorTokens()
    }

    val materialColorScheme = if (isDarkMode) {
        androidx.compose.material3.darkColorScheme(
            primary = Color(0xFF818CF8),
            onPrimary = Color(0xFF1E1B4B),
            primaryContainer = Color(0xFF312E81),
            onPrimaryContainer = Color(0xFFE0E7FF),
            secondary = Color(0xFFA1A1AA),
            onSecondary = Color(0xFF18181B),
            secondaryContainer = Color(0xFF27272A),
            onSecondaryContainer = Color(0xFFF4F4F5),
            background = Color(0xFF121212),
            onBackground = Color(0xFFF4F4F5),
            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFF4F4F5),
            surfaceVariant = Color(0xFF27272A),
            onSurfaceVariant = Color(0xFFA1A1AA),
            outline = Color(0xFF3F3F46),
            outlineVariant = Color(0xFF52525B)
        )
    } else {
        NexusLightColorScheme
    }

    val spacingTokens = NexusSpacingTokens()
    val typographyTokens = NexusTypographyTokens()
    val elevationTokens = NexusElevationTokens()
    val cornerTokens = NexusCornerTokens()

    CompositionLocalProvider(
        LocalNexusColors provides colorTokens,
        LocalNexusSpacing provides spacingTokens,
        LocalNexusTypography provides typographyTokens,
        LocalNexusElevation provides elevationTokens,
        LocalNexusCorners provides cornerTokens
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            shapes = NexusShapes,
            content = content
        )
    }
}
