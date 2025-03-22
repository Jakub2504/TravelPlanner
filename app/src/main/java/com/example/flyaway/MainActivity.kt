package com.example.flyaway

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.flyaway.core.data.preferences.PreferencesRepository
import com.example.flyaway.core.navigation.AppNavigation
import com.example.flyaway.core.util.LocaleManager
import com.example.flyaway.ui.theme.FlyAwayTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var preferencesRepository: PreferencesRepository
    
    override fun attachBaseContext(newBase: Context) {
        // Inicializar preferencesRepository de manera segura
        val context = try {
            if (::preferencesRepository.isInitialized) {
                // Aplicar el idioma guardado al crear la actividad
                val languageCode = runBlocking { 
                    preferencesRepository.getLanguage().first() 
                } ?: LocaleManager.DEFAULT_LANGUAGE
                
                Log.d("MainActivity", "Aplicando idioma: $languageCode")
                updateBaseContextLocale(newBase, languageCode)
            } else {
                // Si preferencesRepository no está inicializado, usar el idioma por defecto
                Log.d("MainActivity", "Usando idioma por defecto: ${LocaleManager.DEFAULT_LANGUAGE}")
                updateBaseContextLocale(newBase, LocaleManager.DEFAULT_LANGUAGE)
            }
        } catch (e: Exception) {
            // En caso de cualquier error, usar el contexto original con idioma por defecto
            Log.e("MainActivity", "Error al configurar el idioma", e)
            updateBaseContextLocale(newBase, LocaleManager.DEFAULT_LANGUAGE)
        }
        
        super.attachBaseContext(context)
    }
    
    private fun updateBaseContextLocale(context: Context, languageCode: String): Context {
        val locale = LocaleManager.SUPPORTED_LOCALES[languageCode] ?: LocaleManager.SUPPORTED_LOCALES[LocaleManager.DEFAULT_LANGUAGE]!!
        return LocaleManager.setLocaleForContext(context, locale)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Aplicar el idioma guardado en las preferencias al inicio de la actividad
        runBlocking {
            try {
                val savedLanguage = preferencesRepository.getLanguage().first()
                val languageToApply = savedLanguage ?: LocaleManager.DEFAULT_LANGUAGE
                
                Log.d("MainActivity", "Configurando idioma en onCreate: $languageToApply")
                val context = LocaleManager.setLocale(this@MainActivity, languageToApply)
                resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
                
                // Si no hay idioma guardado, guardar el predeterminado
                if (savedLanguage == null) {
                    Log.d("MainActivity", "Guardando idioma predeterminado: ${LocaleManager.DEFAULT_LANGUAGE}")
                    preferencesRepository.saveLanguage(LocaleManager.DEFAULT_LANGUAGE)
                } else {
                    Log.d("MainActivity", "Idioma ya guardado: $savedLanguage")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al cargar el idioma en onCreate", e)
            }
        }
        
        setContent {
            FlyAwayTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberAnimatedNavController()
                    AppNavigation(navController = navController)
                }
            }
        }
    }
    
    // Método para recrear la actividad cuando cambia el idioma
    fun applyLanguageAndRecreate(languageCode: String) {
        LocaleManager.setLocale(this, languageCode)
        recreate()
    }

    // Sobrescribir métodos del ciclo de vida para asegurar que el idioma persista
    override fun onResume() {
        super.onResume()
        // Verificar y aplicar el idioma actual
        runBlocking {
            try {
                val currentLanguage = preferencesRepository.getLanguage().first() ?: LocaleManager.DEFAULT_LANGUAGE
                val context = LocaleManager.setLocale(this@MainActivity, currentLanguage)
                resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
                Log.d("MainActivity", "Idioma configurado en onResume: $currentLanguage")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al configurar idioma en onResume", e)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Mantener la configuración de idioma seleccionada por el usuario
        runBlocking {
            try {
                val savedLanguage = preferencesRepository.getLanguage().first() ?: LocaleManager.DEFAULT_LANGUAGE
                val context = LocaleManager.setLocale(this@MainActivity, savedLanguage)
                resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
                Log.d("MainActivity", "Idioma mantenido en onConfigurationChanged: $savedLanguage")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error al mantener idioma en onConfigurationChanged", e)
            }
        }
    }
} 