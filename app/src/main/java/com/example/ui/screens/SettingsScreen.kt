package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexusTheme

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onBack: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        color = NexusTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexusTheme.colors.foreground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexusTheme.colors.foreground
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Preferences",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = NexusTheme.colors.foregroundSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Appearance Section Card (Dark Mode Toggle)
            Surface(
                color = NexusTheme.colors.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NexusTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = NexusTheme.colors.accent,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Dark Mode",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NexusTheme.colors.foreground
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Switch between default ivory-white theme and dark mode aesthetic",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTheme.colors.foregroundSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onToggleDarkMode,
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Legal & About",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = NexusTheme.colors.foregroundSecondary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = NexusTheme.colors.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NexusTheme.colors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Privacy Policy Item
                    Surface(
                        onClick = onPrivacyPolicyClick,
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_privacy_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = NexusTheme.colors.foregroundSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = NexusTheme.colors.foreground
                                )
                            )
                        }
                    }

                    Divider(color = NexusTheme.colors.border, thickness = 1.dp)

                    // Terms & Conditions Item
                    Surface(
                        onClick = onTermsClick,
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_terms_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = NexusTheme.colors.foregroundSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Terms & Conditions",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = NexusTheme.colors.foreground
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
