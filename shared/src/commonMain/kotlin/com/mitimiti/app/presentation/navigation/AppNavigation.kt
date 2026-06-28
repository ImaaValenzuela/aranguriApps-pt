package com.mitimiti.app.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.mitimiti.app.presentation.cierre.SummaryScreen
import com.mitimiti.app.presentation.cierre.SummaryViewModel
import com.mitimiti.app.presentation.consumo.ExpenseScreen
import com.mitimiti.app.presentation.consumo.ExpenseViewModel
import com.mitimiti.app.presentation.mesa.TableListScreen
import com.mitimiti.app.presentation.mesa.TableScreen
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

    val authRepository = remember { FirebaseAuthRepository() }
    val authViewModel = remember { AuthViewModel(authRepository) }
    val authState by authViewModel.uiState.collectAsState()

    val firebaseRemoteDataSource = remember { FirebaseRemoteDataSource() }
    val tableRepository = remember { FirebaseTableRepository(firebaseRemoteDataSource) }
    val syncRepository = remember { FirebaseRealtimeSyncRepository() }
    val calculateSplitExpensesUseCase = remember { CalculateSplitExpensesUseCase() }

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
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate("table_list") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startDestination =
        remember {
            if (authState.isAuthenticated) "table_list" else "login"
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
        composable("table_list") {
            TableListScreen(
                viewModel = tableViewModel,
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
            TableScreen(
                tableId = tableId,
                viewModel = tableViewModel,
                onNavigateToExpenses = { id ->
                    navController.navigate("expense/$id") {
                        launchSingleTop = true
                    }
                },
                onNavigateToSummary = { id ->
                    navController.navigate("summary/$id") {
                        launchSingleTop = true
                    }
                },
                onBack = {
                    tableViewModel.resetTableState()
                    navController.popBackStack()
                },
                modifier = Modifier,
            )
        }
        composable(
            route = "expense/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getString("tableId") ?: ""
            ExpenseScreen(
                tableId = tableId,
                viewModel = expenseViewModel,
                onNavigateToLobby = { id ->
                    navController.navigate("table_lobby/$id") {
                        launchSingleTop = true
                    }
                },
                onNavigateToSummary = { id ->
                    navController.navigate("summary/$id") {
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                modifier = Modifier,
            )
        }
        composable(
            route = "summary/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getString("tableId") ?: ""
            SummaryScreen(
                tableId = tableId,
                viewModel = summaryViewModel,
                onNavigateToLobby = { id ->
                    navController.navigate("table_lobby/$id") {
                        launchSingleTop = true
                    }
                },
                onNavigateToExpenses = { id ->
                    navController.navigate("expense/$id") {
                        launchSingleTop = true
                    }
                },
                onRestart = {
                    tableViewModel.resetTableState()
                    navController.navigate("table_list") {
                        popUpTo("table_list") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                },
                modifier = Modifier,
            )
        }
    }
}
