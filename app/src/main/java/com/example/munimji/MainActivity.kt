package com.example.munimji

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.munimji.data.AppDatabase
import com.example.munimji.data.AppRepository
import com.example.munimji.ui.navigation.AppNavigation
import com.example.munimji.ui.theme.MunimJiTheme
import com.example.munimji.ui.viewmodel.AppViewModel
import com.example.munimji.ui.viewmodel.AppViewModelFactory
import com.example.munimji.utils.DatabaseInitializer

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { AppRepository(database.appDao()) }
    private val viewModel: AppViewModel by viewModels { AppViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            DatabaseInitializer.initializeDatabase(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Database initialization error", e)
        }
        
        enableEdgeToEdge()
        setContent {
            MunimJiTheme {
                AppNavigation(viewModel)
            }
        }
    }
}
