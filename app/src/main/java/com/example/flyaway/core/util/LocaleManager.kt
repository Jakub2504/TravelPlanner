package com.example.flyaway.core.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.util.Locale

object LocaleManager {

    // Idioma predeterminado
    const val DEFAULT_LANGUAGE = "es"

    // Idiomas soportados
    val SUPPORTED_LOCALES = mapOf(
        "en" to Locale("en"), // Inglés
        "es" to Locale("es"), // Español
        "ca" to Locale("ca"), // Catalán
        "pl" to Locale("pl")  // Polaco
    )
    
    // Establecer el idioma de la aplicación
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = SUPPORTED_LOCALES[languageCode] ?: SUPPORTED_LOCALES[DEFAULT_LANGUAGE]!!
        Log.d("LocaleManager", "Estableciendo idioma: $languageCode, locale: $locale")
        
        // Guardar locale globalmente
        Locale.setDefault(locale)
        
        // Actualizar configuración
        return updateResources(context, locale)
    }
    
    // Método específico para contexto base (utilizado en attachBaseContext)
    fun setLocaleForContext(context: Context, locale: Locale): Context {
        Log.d("LocaleManager", "Estableciendo locale para contexto: $locale")
        
        // Guardar locale globalmente
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
    
    // Actualizar recursos con la nueva configuración de idioma
    private fun updateResources(context: Context, locale: Locale): Context {
        Log.d("LocaleManager", "Actualizando recursos para locale: $locale")
        
        // Para asegurar que el idioma se aplique en todas partes
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            
            // Crear nuevo contexto con la configuración actualizada
            val newContext = context.createConfigurationContext(config)
            
            // Asegurarnos de que los recursos se actualicen globalmente
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            
            return newContext
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            return context
        }
    }
    
    // Obtener la etiqueta del idioma actual
    fun getCurrentLanguageTag(context: Context): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        
        Log.d("LocaleManager", "Obteniendo idioma actual: ${locale.language}")
        
        // Si el idioma no está soportado, usar el predeterminado
        return if (isLanguageSupported(locale.language)) {
            locale.language
        } else {
            DEFAULT_LANGUAGE
        }
    }
    
    // Comprobar si un idioma está en la lista de soportados
    fun isLanguageSupported(languageCode: String): Boolean {
        return SUPPORTED_LOCALES.containsKey(languageCode)
    }
    
    // Aplicar idioma después de la creación de la actividad
    fun applyLanguageAfterActivityCreated(context: Context) {
        try {
            // Obtener el idioma actual del sistema
            val currentLocale = getCurrentLanguageTag(context)
            
            // Aplicar el idioma
            setLocale(context, currentLocale)
            
            Log.d("LocaleManager", "Idioma aplicado después de crear la actividad: $currentLocale")
        } catch (e: Exception) {
            Log.e("LocaleManager", "Error al aplicar idioma después de crear la actividad", e)
            
            // En caso de error, usar el idioma predeterminado
            setLocale(context, DEFAULT_LANGUAGE)
        }
    }
} 