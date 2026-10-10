package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NexusIntern Centralized Design Tokens
 * Defined according to the Visual Design System specification:
 * - Quiet confidence: simple layouts, strong typography, carefully selected details
 * - Subtle depth: minimal 3D elements, controlled lighting, restrained motion
 * - Functional clarity: every component aids comprehension and task completion
 */
object NexusTokens {

    // --- Core Palette ---
    val Background = Color(0xFFF7F6F2)          // Main warm-neutral canvas
    val Surface = Color(0xFFFFFFFF)             // Inputs, elevated surfaces and work areas
    val SurfaceSecondary = Color(0xFFEFEEE9)    // Subtle section backgrounds
    val Foreground = Color(0xFF191A1C)          // Primary text
    val ForegroundSecondary = Color(0xFF66686C) // Supporting text
    val ForegroundTertiary = Color(0xFF898A8D)  // Secondary metadata
    val Border = Color(0xFFE4E2DC)              // Dividers and component outlines
    val BorderStrong = Color(0xFFC9C7C0)        // Emphasized outlines
    val Accent = Color(0xFF5558A6)              // Primary interactive accent
    val AccentHover = Color(0xFF434687)         // Hover or pressed emphasis
    val AccentSubtle = Color(0xFFECECF7)        // Selected navigation and soft highlights
    val OnAccent = Color(0xFFFFFFFF)            // Text on accent-filled components

    // --- Semantic State Tokens ---
    // Success: dark green text on pale green background
    val SuccessText = Color(0xFF165331)
    val SuccessBackground = Color(0xFFEAF5EE)
    val SuccessBorder = Color(0xFFC8E6D3)

    // Warning: dark amber text on pale amber background
    val WarningText = Color(0xFF7A4B04)
    val WarningBackground = Color(0xFFFEF5E7)
    val WarningBorder = Color(0xFFF9DDB3)

    // Error: dark red text on pale red background
    val ErrorText = Color(0xFF8A1F1D)
    val ErrorBackground = Color(0xFFFBEAE9)
    val ErrorBorder = Color(0xFFF5C2C0)

    // Information: dark blue text on pale blue background
    val InfoText = Color(0xFF1E3A8A)
    val InfoBackground = Color(0xFFEFF6FF)
    val InfoBorder = Color(0xFFBFDBFE)

    // --- Spacing Tokens (4px base) ---
    object Spacing {
        val xxs: Dp = 4.dp
        val xs: Dp = 8.dp
        val sm: Dp = 12.dp
        val md: Dp = 16.dp
        val lg: Dp = 24.dp
        val xl: Dp = 32.dp
        val xxl: Dp = 40.dp
        val xxxl: Dp = 48.dp
        val huge: Dp = 64.dp
    }

    // --- Corner Radii Tokens ---
    object Radius {
        val sm: Dp = 6.dp
        val md: Dp = 10.dp
        val lg: Dp = 14.dp
        val surface: Dp = 18.dp
        val pill: Dp = 100.dp
    }

    // --- Motion Duration Tokens ---
    object Motion {
        const val FastMs = 140       // Fast feedback (taps, toggles)
        const val StandardMs = 200   // Standard transitions (cards, tabs)
        const val PanelMs = 280      // Larger panels, modal reveals
    }
}
