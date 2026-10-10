package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.data.api.HealthResponse
import com.example.viewmodel.Load

@Composable
fun WelcomeScreen(
    health: Load<HealthResponse>,
    onGetStarted: () -> Unit,
    onExploreDemo: () -> Unit,
    onRetryHealth: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    GetStartedScreen(
        onStudentClick = onGetStarted,
        onAdminClick = onGetStarted,
        onRecruiterClick = onGetStarted,
        onExploreDemoClick = onExploreDemo,
        onPrivacyPolicyClick = onPrivacyPolicyClick,
        onTermsClick = onTermsClick
    )
}
