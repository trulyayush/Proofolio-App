package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Floating3DBackground
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens

enum class AuthMode {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun StudentAuthScreen(
    onLogin: (email: String, password: String, onResult: (Result<Unit>) -> Unit) -> Unit,
    onSignUp: (fullName: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) -> Unit,
    onResetPassword: (email: String, newPassword: String, onResult: (Result<Unit>) -> Unit) -> Unit,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .testTag("student_auth_screen")
    ) {
        Floating3DBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar with Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    color = NexusTokens.Surface,
                    border = BorderStroke(1.dp, NexusTokens.Border)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("student_auth_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NexusTokens.Foreground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "STUDENT PORTAL ACCESS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                    color = NexusTokens.ForegroundSecondary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Masthead Emblem
            Surface(
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                color = NexusTokens.Surface,
                border = BorderStroke(1.5.dp, NexusTokens.BorderStrong),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (mode) {
                    AuthMode.LOGIN -> "Student Login"
                    AuthMode.SIGN_UP -> "Create Student Account"
                    AuthMode.FORGOT_PASSWORD -> "Reset Password"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground,
                textAlign = TextAlign.Center
            )

            Text(
                text = when (mode) {
                    AuthMode.LOGIN -> "Access your verified technical portfolio, repository assessments, and internship matches."
                    AuthMode.SIGN_UP -> "Register with your verified email to submit codebases and generate AI radar evaluation dossiers."
                    AuthMode.FORGOT_PASSWORD -> "Set a new password for your verified student account."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = NexusTokens.ForegroundSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .padding(top = 6.dp, bottom = 20.dp)
            )

            // Auth Box Card
            NexusCard(
                modifier = Modifier.widthIn(max = 480.dp),
                backgroundColor = NexusTokens.Surface,
                borderColor = NexusTokens.Border
            ) {
                // Mode Toggle Tabs (Login vs Sign Up)
                if (mode != AuthMode.FORGOT_PASSWORD) {
                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.dp, NexusTokens.Border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        mode = AuthMode.LOGIN
                                        errorMessage = null
                                        successMessage = null
                                    },
                                color = if (mode == AuthMode.LOGIN) NexusTokens.Surface else Color.Transparent,
                                shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                border = if (mode == AuthMode.LOGIN) BorderStroke(1.dp, NexusTokens.Border) else null
                            ) {
                                Text(
                                    text = "Log In",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (mode == AuthMode.LOGIN) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (mode == AuthMode.LOGIN) NexusTokens.Foreground else NexusTokens.ForegroundSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        mode = AuthMode.SIGN_UP
                                        errorMessage = null
                                        successMessage = null
                                    },
                                color = if (mode == AuthMode.SIGN_UP) NexusTokens.Surface else Color.Transparent,
                                shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                border = if (mode == AuthMode.SIGN_UP) BorderStroke(1.dp, NexusTokens.Border) else null
                            ) {
                                Text(
                                    text = "Create Account",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (mode == AuthMode.SIGN_UP) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (mode == AuthMode.SIGN_UP) NexusTokens.Foreground else NexusTokens.ForegroundSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Error Message Banner
                if (errorMessage != null) {
                    Surface(
                        color = NexusTokens.ErrorBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Success Message Banner
                if (successMessage != null) {
                    Surface(
                        color = NexusTokens.SuccessBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.SuccessBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = successMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.SuccessText,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                // Fields based on Mode
                if (mode == AuthMode.SIGN_UP) {
                    NexusTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        placeholder = "e.g. Priyanshu Sharma",
                        leadingIcon = Icons.Default.Person,
                        testTag = "auth_name_input"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                NexusTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "student@institution.edu",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    testTag = "auth_email_input"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NexusTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = if (mode == AuthMode.FORGOT_PASSWORD) "New Password" else "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    onTrailingIconClick = { passwordVisible = !passwordVisible },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    testTag = "auth_password_input"
                )

                if (mode == AuthMode.SIGN_UP || mode == AuthMode.FORGOT_PASSWORD) {
                    Spacer(modifier = Modifier.height(10.dp))
                    NexusTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        testTag = "auth_confirm_password_input"
                    )
                }

                // Forgot password trigger
                if (mode == AuthMode.LOGIN) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot password?",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.Accent,
                            modifier = Modifier
                                .clickable {
                                    mode = AuthMode.FORGOT_PASSWORD
                                    errorMessage = null
                                    successMessage = null
                                }
                                .padding(4.dp)
                                .testTag("forgot_password_button")
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Primary Action Button
                NexusButton(
                    onClick = {
                        errorMessage = null
                        successMessage = null

                        when (mode) {
                            AuthMode.LOGIN -> {
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please enter both email and password."
                                    return@NexusButton
                                }
                                isLoading = true
                                onLogin(email, password) { result ->
                                    isLoading = false
                                    result.onFailure {
                                        errorMessage = it.message ?: "Authentication failed."
                                    }
                                }
                            }

                            AuthMode.SIGN_UP -> {
                                if (fullName.isBlank()) {
                                    errorMessage = "Please enter your full name."
                                    return@NexusButton
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = "Please enter a valid email address."
                                    return@NexusButton
                                }
                                if (password.length < 6) {
                                    errorMessage = "Password must be at least 6 characters."
                                    return@NexusButton
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Passwords do not match."
                                    return@NexusButton
                                }
                                isLoading = true
                                onSignUp(fullName, email, password) { result ->
                                    isLoading = false
                                    result.onFailure {
                                        errorMessage = it.message ?: "Account registration failed."
                                    }
                                }
                            }

                            AuthMode.FORGOT_PASSWORD -> {
                                if (email.isBlank()) {
                                    errorMessage = "Please enter your email."
                                    return@NexusButton
                                }
                                if (password.length < 6) {
                                    errorMessage = "New password must be at least 6 characters."
                                    return@NexusButton
                                }
                                if (password != confirmPassword) {
                                    errorMessage = "Passwords do not match."
                                    return@NexusButton
                                }
                                isLoading = true
                                onResetPassword(email, password) { result ->
                                    isLoading = false
                                    result.onSuccess {
                                        successMessage = "Password successfully reset. You can now log in."
                                        mode = AuthMode.LOGIN
                                    }.onFailure {
                                        errorMessage = it.message ?: "Failed to reset password."
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = isLoading,
                    enabled = !isLoading,
                    variant = NexusButtonVariant.Primary,
                    testTag = "auth_submit_button"
                ) {
                    Text(
                        text = when (mode) {
                            AuthMode.LOGIN -> "Log In to Student Portal"
                            AuthMode.SIGN_UP -> "Register Account & Continue"
                            AuthMode.FORGOT_PASSWORD -> "Update Password"
                        }
                    )
                }

                if (mode == AuthMode.FORGOT_PASSWORD) {
                    Spacer(modifier = Modifier.height(10.dp))
                    NexusButton(
                        onClick = {
                            mode = AuthMode.LOGIN
                            errorMessage = null
                            successMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        variant = NexusButtonVariant.Text,
                        testTag = "back_to_login_button"
                    ) {
                        Text("Back to Login")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Role Disclaimer Security Note
            Surface(
                color = NexusTokens.SurfaceSecondary,
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                border = BorderStroke(1.dp, NexusTokens.Border),
                modifier = Modifier.widthIn(max = 480.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Zero Privilege Escalation: Student authentication grants access strictly to Candidate dossiers and does not grant Admin or Recruiter portal authority.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
