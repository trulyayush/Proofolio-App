package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppViewModel

@Composable
fun DemoBanner(
    demoView: AppViewModel.DemoView = AppViewModel.DemoView.STUDENT,
    onSetDemoView: ((AppViewModel.DemoView) -> Unit)? = null,
    onResetDemo: () -> Unit = {},
    onExitDemo: () -> Unit = {}
) {
    Surface(
        color = Color(0xFFFFFBEB),
        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier.fillMaxWidth().testTag("demo_banner")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD97706))
                    )
                    Text(
                        text = "DEMO MODE ACTIVE",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF92400E)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onResetDemo,
                        modifier = Modifier.height(30.dp).testTag("demo_banner_reset_button")
                    ) {
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB45309)
                        )
                    }
                    TextButton(
                        onClick = onExitDemo,
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("exit_demo_banner_button")
                    ) {
                        Text(
                            text = "Exit Demo",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // Optional View Switcher Tabs if onSetDemoView is provided
            if (onSetDemoView != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DemoTabButton(
                        title = "Student View",
                        selected = demoView == AppViewModel.DemoView.STUDENT,
                        onClick = { onSetDemoView(AppViewModel.DemoView.STUDENT) },
                        modifier = Modifier.weight(1f)
                    )
                    DemoTabButton(
                        title = "Admin Dashboard",
                        selected = demoView == AppViewModel.DemoView.ADMIN,
                        onClick = { onSetDemoView(AppViewModel.DemoView.ADMIN) },
                        modifier = Modifier.weight(1f)
                    )
                    DemoTabButton(
                        title = "Recruiter Portal",
                        selected = demoView == AppViewModel.DemoView.RECRUITER,
                        onClick = { onSetDemoView(AppViewModel.DemoView.RECRUITER) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoTabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFFB45309) else Color.White,
        border = BorderStroke(1.dp, if (selected) Color(0xFF92400E) else Color(0xFFFDE68A)),
        modifier = modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                color = if (selected) Color.White else Color(0xFF92400E),
                maxLines = 1
            )
        }
    }
}
