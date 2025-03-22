package com.example.flyaway.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flyaway.feature.about.presentation.AboutUsScreen
import com.example.flyaway.feature.auth.presentation.LoginScreen
import com.example.flyaway.feature.createtrip.presentation.CreateTripScreen
import com.example.flyaway.feature.home.presentation.HomeScreen
import com.example.flyaway.feature.settings.presentation.LanguageSettingsScreen
import com.example.flyaway.feature.settings.presentation.SettingsScreen
import com.example.flyaway.feature.splash.presentation.SplashScreen
import com.example.flyaway.feature.terms.presentation.TermsScreen
import com.example.flyaway.feature.tripdetails.presentation.TripDetailsScreen
import com.example.flyaway.feature.profile.presentation.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppDestinations.Splash.route
) {
    val actions = remember(navController) {
        AppNavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it }, 
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it }, 
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it }, 
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it }, 
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(AppDestinations.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    actions.navigateToLogin()
                }
            )
        }
        
        composable(AppDestinations.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(AppDestinations.Home.route) {
                        popUpTo(AppDestinations.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(AppDestinations.Home.route) {
            HomeScreen(
                onNavigateToCreateTrip = {
                    navController.navigate(AppDestinations.CreateTrip.route)
                },
                onNavigateToTripDetails = { tripId ->
                    navController.navigate(AppDestinations.TripDetails.createRoute(tripId))
                },
                onNavigateToSettings = {
                    navController.navigate(AppDestinations.Settings.route)
                },
                onNavigateToAboutUs = {
                    navController.navigate(AppDestinations.AboutUs.route)
                },
                onNavigateToTerms = {
                    navController.navigate(AppDestinations.Terms.route)
                }
            )
        }
        
        composable(AppDestinations.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLanguage = {
                    navController.navigate(AppDestinations.LanguageSettings.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.Profile.route)
                },
                onLogout = {
                    navController.navigate(AppDestinations.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(AppDestinations.LanguageSettings.route) {
            LanguageSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppDestinations.AboutUs.route) {
            AboutUsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppDestinations.Terms.route) {
            TermsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(AppDestinations.CreateTrip.route) {
            CreateTripScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppDestinations.TripDetails.route,
            arguments = listOf(navArgument(AppDestinations.TripDetails.TRIP_ID_PARAM) {
                type = NavType.StringType
            })
        ) {
            val tripId = it.arguments?.getString(AppDestinations.TripDetails.TRIP_ID_PARAM) ?: ""
            TripDetailsScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = { navController.navigate(AppDestinations.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }}
            )
        }
        
        composable(AppDestinations.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

class AppNavigationActions(private val navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(AppDestinations.Home.route) {
            popUpTo(AppDestinations.Splash.route) { inclusive = true }
        }
    }
    
    val navigateToLogin: () -> Unit = {
        navController.navigate(AppDestinations.Login.route) {
            popUpTo(AppDestinations.Splash.route) { inclusive = true }
        }
    }
    
    val navigateToSettings: () -> Unit = {
        navController.navigate(AppDestinations.Settings.route)
    }
    
    val navigateToAbout: () -> Unit = {
        navController.navigate(AppDestinations.AboutUs.route)
    }
    
    val navigateToCreateTrip: () -> Unit = {
        navController.navigate(AppDestinations.CreateTrip.route)
    }
    
    val navigateToTripDetails: (String) -> Unit = { tripId ->
        navController.navigate(AppDestinations.TripDetails.createRoute(tripId))
    }
    
    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
} 