package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.VerifiedBuildApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: com.example.viewmodel.AppViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            MyApplicationTheme(isDarkMode = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.ui.theme.NexusTheme.colors.background
                ) {
                    VerifiedBuildApp(viewModel)
                }
            }
        }
    }
}
