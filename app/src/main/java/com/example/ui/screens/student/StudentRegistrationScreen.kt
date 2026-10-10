package com.example.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusTextField

@Composable
fun StudentRegistrationScreen(
    onRegister: (String, String, String) -> Unit, // name, email, password
    onLogin: (String, String) -> Unit, // email, password
    onBack: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit,
    registrationError: String? = null
) {
    val backgroundColor = Color(0xFFFCFBF8)
    val charcoalPrimary = Color(0xFF191A1C)
    val mutedGrey = Color(0xFF71717A)

    var isLoginMode by rememberSaveable { mutableStateOf(false) }

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_registration_screen"),
        color = backgroundColor
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
                        tint = charcoalPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isLoginMode) "Student Sign In" else "Student Registration",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = charcoalPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isLoginMode)
                    "Sign in with your existing Proofolio account to view your verified projects and interview assessments."
                else
                    "Create your Proofolio account to start showcasing verified projects and matching with top internships.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = mutedGrey,
                    lineHeight = 22.sp
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (registrationError != null) {
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = registrationError,
                        color = Color(0xFF991B1B),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Name Field (Only in Registration Mode)
            if (!isLoginMode) {
                Text(
                    text = "Full Name",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = charcoalPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                NexusTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    placeholder = "e.g. Alex Johnson",
                    leadingIcon = Icons.Default.Person,
                    errorText = nameError,
                    modifier = Modifier.testTag("name_input")
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Email Field
            Text(
                text = "Email Address",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = charcoalPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            NexusTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                placeholder = "e.g. alex@university.edu",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = emailError,
                modifier = Modifier.testTag("email_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = charcoalPrimary
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            NexusTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                placeholder = "At least 6 characters",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                errorText = passwordError,
                modifier = Modifier.testTag("password_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Terms & Conditions Checkbox (Only in Registration Mode)
            if (!isLoginMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = {
                            termsAccepted = it
                            termsError = null
                        },
                        modifier = Modifier.testTag("terms_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "I agree to the Terms & Conditions and acknowledge the Privacy Policy.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = charcoalPrimary,
                                lineHeight = 18.sp
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            TextButton(
                                onClick = onTermsClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = "Terms", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC5A059), fontWeight = FontWeight.Bold))
                            }
                            Text(text = "·", color = mutedGrey)
                            TextButton(
                                onClick = onPrivacyPolicyClick,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = "Privacy Policy", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC5A059), fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
                if (termsError != null) {
                    Text(text = termsError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(start = 32.dp, top = 4.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit / Action Button (Complete Registration or Login)
            NexusButton(
                onClick = {
                    var valid = true
                    if (!isLoginMode && name.isBlank()) {
                        nameError = "Name cannot be empty"
                        valid = false
                    }
                    if (!email.matches(emailRegex)) {
                        emailError = "Please enter a valid email"
                        valid = false
                    }
                    if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        valid = false
                    }
                    if (!isLoginMode && !termsAccepted) {
                        termsError = "You must accept the terms and privacy policy"
                        valid = false
                    }

                    if (valid) {
                        if (isLoginMode) {
                            onLogin(email.trim(), password)
                        } else {
                            onRegister(name.trim(), email.trim(), password)
                        }
                    }
                },
                variant = NexusButtonVariant.Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(if (isLoginMode) "login_submit_button" else "register_submit_button")
            ) {
                Text(
                    text = if (isLoginMode) "Sign In" else "Complete Registration",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Toggle between Registration and Login Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedGrey
                )
                TextButton(
                    onClick = {
                        isLoginMode = !isLoginMode
                        nameError = null
                        emailError = null
                        passwordError = null
                        termsError = null
                    },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.testTag("toggle_login_mode_button")
                ) {
                    Text(
                        text = if (isLoginMode) "Register" else "Sign In",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC5A059)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
