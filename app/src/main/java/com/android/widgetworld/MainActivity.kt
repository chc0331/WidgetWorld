package com.android.widgetworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.widgetworld.feature.editor.ui.EditorScreen
import com.android.widgetworld.feature.editor.ui.MainScreen
import com.android.widgetworld.ui.theme.WidgetWorldTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - 섹션 4: UX Flow 구현
 * 
 * Main 화면과 Editor 화면 간 Navigation을 담당합니다.
 * 
 * Navigation Routes:
 * - "main": Main 화면 (진입점)
 * - "editor": Editor 화면 (위젯 편집)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetWorldTheme {
                WidgetWorldApp()
            }
        }
    }
}

/**
 * WidgetWorld 앱의 Navigation 구조
 */
@Composable
fun WidgetWorldApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("main") {
            MainScreen(
                onNavigateToEditor = {
                    navController.navigate("editor")
                }
            )
        }
        
        composable("editor") {
            EditorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
