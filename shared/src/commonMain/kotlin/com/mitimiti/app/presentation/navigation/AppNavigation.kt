package com.mitimiti.app.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mitimiti.app.data.datasource.FirebaseRemoteDataSource
import com.mitimiti.app.data.repository.FirebaseAuthRepository
import com.mitimiti.app.data.repository.FirebaseRealtimeSyncRepository
import com.mitimiti.app.data.repository.FirebaseTableRepository
import com.mitimiti.app.domain.usecase.CalculateSplitExpensesUseCase
import com.mitimiti.app.presentation.auth.AuthViewModel
import com.mitimiti.app.presentation.auth.LoginScreen
import com.mitimiti.app.presentation.auth.RegisterScreen
import com.mitimiti.app.presentation.cierre.SummaryViewModel
import com.mitimiti.app.presentation.consumo.ExpenseViewModel
import com.mitimiti.app.presentation.mesa.ActiveTableHubScreen
import com.mitimiti.app.presentation.mesa.MainHubScreen
import com.mitimiti.app.presentation.mesa.TableViewModel

@Composable
@Suppress("FunctionNaming")
fun AppNavigation(
    onGoogleSignInClick: () -> Unit = {},
    googleIdToken: String? = null,
    onGoogleTokenConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    val firebaseRemoteDataSource = remember { FirebaseRemoteDataSource() }
    val tableRepository = remember { FirebaseTableRepository(firebaseRemoteDataSource) }
    val syncRepository = remember { FirebaseRealtimeSyncRepository() }
    val calculateSplitExpensesUseCase = remember { CalculateSplitExpensesUseCase() }

    val authRepository = remember { FirebaseAuthRepository() }
    val authViewModel = remember { AuthViewModel(authRepository, tableRepository) }
    val authState by authViewModel.uiState.collectAsState()

    val tableViewModel =
        remember {
            TableViewModel(
                tableRepository = tableRepository,
                authRepository = authRepository,
                syncRepository = syncRepository,
            )
        }
    val expenseViewModel = remember { ExpenseViewModel(tableRepository) }
    val summaryViewModel = remember { SummaryViewModel(tableRepository, calculateSplitExpensesUseCase) }

    LaunchedEffect(googleIdToken) {
        if (googleIdToken != null) {
            authViewModel.signInWithGoogle(googleIdToken)
            onGoogleTokenConsumed()
        }
    }

    // Monitor authentication state and adjust navigation
    LaunchedEffect(authState.isAuthenticated, authState.isOnboarded) {
        if (authState.isAuthenticated) {
            if (authState.isOnboarded == true) {
                navController.navigate("main_hub") {
                    popUpTo(0) { inclusive = true }
                }
            } else if (authState.isOnboarded == false) {
                navController.navigate("onboarding") {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (authState.isAuthenticated && authState.isOnboarded == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )
        }
        return
    }

    val startDestination =
        remember(authState.isAuthenticated, authState.isOnboarded) {
            if (authState.isAuthenticated) {
                if (authState.isOnboarded == false) "onboarding" else "main_hub"
            } else {
                "login"
            }
        }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideInHorizontally { width -> width } + fadeIn() },
        exitTransition = { slideOutHorizontally { width -> -width } + fadeOut() },
        popEnterTransition = { slideInHorizontally { width -> -width } + fadeIn() },
        popExitTransition = { slideOutHorizontally { width -> width } + fadeOut() },
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onGoogleSignInClick = onGoogleSignInClick,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                modifier = Modifier,
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier,
            )
        }
        composable("onboarding") {
            com.mitimiti.app.presentation.auth.OnboardingScreen(
                viewModel = authViewModel,
                onSignOut = {
                    authViewModel.signOut()
                    tableViewModel.resetTableState()
                },
                modifier = Modifier,
            )
        }
        composable("main_hub") {
            MainHubScreen(
                viewModel = tableViewModel,
                userEmail = authState.user?.email,
                onNavigateToLobby = { tableId ->
                    navController.navigate("table_lobby/$tableId")
                },
                onSignOut = {
                    authViewModel.signOut()
                    tableViewModel.resetTableState()
                },
                modifier = Modifier,
            )
        }
        composable(
            route = "table_lobby/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getString("tableId") ?: ""
            ActiveTableHubScreen(
                tableId = tableId,
                tableViewModel = tableViewModel,
                expenseViewModel = expenseViewModel,
                summaryViewModel = summaryViewModel,
                onBack = {
                    tableViewModel.resetTableState()
                    navController.popBackStack()
                },
                modifier = Modifier,
            )
        }
    }
}
