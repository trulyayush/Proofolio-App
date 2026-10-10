package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexusTokens

enum class NexusButtonVariant {
    Primary,
    Secondary,
    Outline,
    Text,
    Destructive
}

/**
 * Reusable NexusButton supporting Primary, Secondary, Outline, Text, Destructive variants,
 * with Pressed, Focused, Disabled, and Loading states.
 */
@Composable
fun NexusButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: NexusButtonVariant = NexusButtonVariant.Primary,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    testTag: String = "nexus_button",
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 11.dp),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val (bgColor, contentColor, borderStroke) = when (variant) {
        NexusButtonVariant.Primary -> {
            val bg = when {
                !enabled -> NexusTokens.Border
                isLoading -> NexusTokens.AccentHover
                isPressed -> NexusTokens.AccentHover
                else -> NexusTokens.Accent
            }
            Triple(bg, NexusTokens.OnAccent, null)
        }
        NexusButtonVariant.Secondary -> {
            val bg = when {
                !enabled -> NexusTokens.SurfaceSecondary.copy(alpha = 0.5f)
                isPressed -> NexusTokens.Border
                else -> NexusTokens.SurfaceSecondary
            }
            val fg = if (enabled) NexusTokens.Foreground else NexusTokens.ForegroundTertiary
            val b = BorderStroke(1.dp, NexusTokens.Border)
            Triple(bg, fg, b)
        }
        NexusButtonVariant.Outline -> {
            val bg = when {
                !enabled -> NexusTokens.Surface.copy(alpha = 0.5f)
                isPressed -> NexusTokens.SurfaceSecondary
                else -> NexusTokens.Surface
            }
            val fg = if (enabled) NexusTokens.Foreground else NexusTokens.ForegroundTertiary
            val b = BorderStroke(1.dp, if (enabled) NexusTokens.BorderStrong else NexusTokens.Border)
            Triple(bg, fg, b)
        }
        NexusButtonVariant.Text -> {
            val bg = if (isPressed) NexusTokens.SurfaceSecondary else Color.Transparent
            val fg = if (enabled) NexusTokens.Accent else NexusTokens.ForegroundTertiary
            Triple(bg, fg, null)
        }
        NexusButtonVariant.Destructive -> {
            val bg = when {
                !enabled -> NexusTokens.ErrorBackground.copy(alpha = 0.5f)
                isPressed -> NexusTokens.ErrorText
                else -> NexusTokens.ErrorBackground
            }
            val fg = if (isPressed) Color.White else NexusTokens.ErrorText
            val b = BorderStroke(1.dp, NexusTokens.ErrorBorder)
            Triple(bg, fg, b)
        }
    }

    Surface(
        modifier = modifier
            .testTag(testTag)
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = bgColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(NexusTokens.Radius.md),
        border = borderStroke
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 44.dp)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = contentColor
                )
                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
            }

            content()

            if (!isLoading && trailingIcon != null) {
                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Clean, lightly outlined text field with label, placeholder, helper text, error text,
 * focus states, and minimum touch target size.
 */
@Composable
fun NexusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    helperText: String? = null,
    errorText: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String = "nexus_text_field"
) {
    var isFocused by remember { mutableStateOf(false) }

    val isError = errorText != null && errorText.isNotBlank()
    val borderColor = when {
        !enabled -> NexusTokens.Border
        isError -> NexusTokens.ErrorText
        isFocused -> NexusTokens.Accent
        else -> NexusTokens.Border
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = NexusTokens.Spacing.xxs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = NexusTokens.Foreground,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(if (isFocused || isError) 1.5.dp else 1.dp, borderColor),
                    RoundedCornerShape(NexusTokens.Radius.md)
                ),
            color = if (enabled) NexusTokens.Surface else NexusTokens.SurfaceSecondary,
            shape = RoundedCornerShape(NexusTokens.Radius.md)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = if (maxLines > 1) 12.dp else 11.dp),
                verticalAlignment = if (maxLines > 1) Alignment.Top else Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) NexusTokens.Accent else NexusTokens.ForegroundTertiary,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(18.dp)
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundTertiary
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        singleLine = singleLine,
                        minLines = minLines,
                        maxLines = maxLines,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = if (enabled) NexusTokens.Foreground else NexusTokens.ForegroundSecondary
                        ),
                        cursorBrush = SolidColor(NexusTokens.Accent),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused = it.isFocused }
                            .testTag(testTag)
                    )
                }

                if (trailingIcon != null) {
                    IconButton(
                        onClick = { onTrailingIconClick?.invoke() },
                        enabled = enabled && onTrailingIconClick != null,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = NexusTokens.ForegroundTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (isError) {
            Row(
                modifier = Modifier.padding(top = NexusTokens.Spacing.xxs, start = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = NexusTokens.ErrorText,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorText ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ErrorText
                )
            }
        } else if (!helperText.isNullOrBlank()) {
            Text(
                text = helperText,
                style = MaterialTheme.typography.labelSmall,
                color = NexusTokens.ForegroundSecondary,
                modifier = Modifier.padding(top = NexusTokens.Spacing.xxs, start = 2.dp)
            )
        }
    }
}

/**
 * Reusable Card Surface with subtle 1px border and warm contrast.
 */
@Composable
fun NexusCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NexusTokens.Surface,
    borderColor: Color = NexusTokens.Border,
    shape: RoundedCornerShape = RoundedCornerShape(NexusTokens.Radius.lg),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

/**
 * Reusable Status Pill / Badge adhering to semantic colors:
 * - Success: dark green text on pale green bg
 * - Warning: dark amber text on pale amber bg
 * - Error: dark red text on pale red bg
 * - Info: dark blue text on pale blue bg
 * - Neutral: charcoal text on surface secondary bg
 */
@Composable
fun NexusBadge(
    label: String,
    modifier: Modifier = Modifier,
    statusType: String = "NEUTRAL"
) {
    val (bg, fg, border) = when (statusType.uppercase()) {
        "SUCCESS", "COMPLETED", "ACCEPTED", "EVALUATION_COMPLETED", "SHORTLISTED" ->
            Triple(NexusTokens.SuccessBackground, NexusTokens.SuccessText, NexusTokens.SuccessBorder)
        "WARNING", "PENDING", "INTERVIEW_PENDING" ->
            Triple(NexusTokens.WarningBackground, NexusTokens.WarningText, NexusTokens.WarningBorder)
        "ERROR", "DECLINED", "FAILED" ->
            Triple(NexusTokens.ErrorBackground, NexusTokens.ErrorText, NexusTokens.ErrorBorder)
        "INFO", "UNDER_ADMIN_REVIEW", "SHARED_WITH_RECRUITER", "REVIEWING" ->
            Triple(NexusTokens.InfoBackground, NexusTokens.InfoText, NexusTokens.InfoBorder)
        else ->
            Triple(NexusTokens.SurfaceSecondary, NexusTokens.ForegroundSecondary, NexusTokens.Border)
    }

    Surface(
        modifier = modifier,
        color = bg,
        shape = RoundedCornerShape(NexusTokens.Radius.pill),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = fg
            )
        }
    }
}

/**
 * Reusable Skill Chip for technical stack representations.
 */
@Composable
fun NexusSkillChip(
    skill: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = NexusTokens.SurfaceSecondary,
        shape = RoundedCornerShape(NexusTokens.Radius.sm),
        border = BorderStroke(1.dp, NexusTokens.Border)
    ) {
        Text(
            text = skill.trim(),
            style = MaterialTheme.typography.labelSmall,
            color = NexusTokens.Foreground,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
        )
    }
}

/**
 * Reusable App Header with quiet confidence:
 * title, optional subtitle, back action, and optional trailing action.
 */
@Composable
fun NexusHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    badgeText: String? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NexusTokens.Surface,
        border = BorderStroke(1.dp, NexusTokens.Border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("nexus_header_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NexusTokens.Foreground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        if (badgeText != null) {
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            NexusBadge(label = badgeText, statusType = "INFO")
                        }
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }

            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)
                ) {
                    actions()
                }
            }
        }
    }
}

/**
 * Reusable Search Bar Component with subtle border and clear button.
 */
@Composable
fun NexusSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    testTag: String = "nexus_search_bar"
) {
    NexusTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Search,
        trailingIcon = if (query.isNotEmpty()) Icons.Default.Close else null,
        onTrailingIconClick = { onQueryChange("") },
        modifier = modifier,
        testTag = testTag
    )
}

/**
 * Reusable Segmented Tabs with quiet confidence.
 */
@Composable
fun NexusSegmentedTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NexusTokens.SurfaceSecondary,
        shape = RoundedCornerShape(NexusTokens.Radius.md),
        border = BorderStroke(1.dp, NexusTokens.Border)
    ) {
        Row(
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                val isSelected = selectedIndex == index
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectTab(index) },
                    color = if (isSelected) NexusTokens.Surface else Color.Transparent,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = if (isSelected) BorderStroke(1.dp, NexusTokens.Border) else null
                ) {
                    Text(
                        text = tabTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) NexusTokens.Foreground else NexusTokens.ForegroundSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Empty State with placeholder icon, friendly tip and call to action.
 */
@Composable
fun NexusEmptyState(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(NexusTokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = NexusTokens.SurfaceSecondary,
            shape = CircleShape,
            border = BorderStroke(1.dp, NexusTokens.Border),
            modifier = Modifier.size(54.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NexusTokens.ForegroundTertiary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = NexusTokens.Foreground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = NexusTokens.ForegroundSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (!actionButtonText.isNullOrBlank() && onActionClick != null) {
            Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))
            NexusButton(
                onClick = onActionClick,
                variant = NexusButtonVariant.Secondary
            ) {
                Text(actionButtonText)
            }
        }
    }
}

/**
 * Reusable File Upload Area with type/size guidance, progress, retry, and removal states.
 */
@Composable
fun NexusUploadArea(
    fileName: String?,
    fileSizeInfo: String,
    progress: Float?,
    onUploadClick: () -> Unit,
    onRemoveClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onUploadClick),
        color = if (fileName != null) NexusTokens.Surface else NexusTokens.SurfaceSecondary,
        shape = RoundedCornerShape(NexusTokens.Radius.md),
        border = BorderStroke(1.dp, if (fileName != null) NexusTokens.Accent else NexusTokens.Border)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (fileName == null) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = NexusTokens.Accent,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap to upload technical resume",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = NexusTokens.Foreground
                )
                Text(
                    text = fileSizeInfo,
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundTertiary
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NexusTokens.SuccessText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = fileName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = fileSizeInfo,
                                style = MaterialTheme.typography.labelSmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }

                    if (onRemoveClick != null) {
                        IconButton(onClick = onRemoveClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = NexusTokens.ForegroundTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (progress != null && progress < 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = NexusTokens.Accent,
                        trackColor = NexusTokens.Border
                    )
                }
            }
        }
    }
}
