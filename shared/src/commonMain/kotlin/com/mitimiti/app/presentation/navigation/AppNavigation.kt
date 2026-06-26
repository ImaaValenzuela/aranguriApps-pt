package com.mitimiti.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.mitimiti.app.presentation.mesa.TableScreen
import com.mitimiti.app.presentation.mesa.TableViewModel

sealed interface Screen {
    data object TableInput : Screen

    data class ExpenseInput(val tableId: String) : Screen

    data class SummaryView(val tableId: String) : Screen
}

private sealed interface AuthScreen {
    data object Login : AuthScreen
    data object Register : AuthScreen
}

@Composable
@Suppress("FunctionNaming")
fun AppNavigation(
    onGoogleSignInClick: () -> Unit = {},
    googleIdToken: String? = null,
    onGoogleTokenConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val authRepository = remember { FirebaseAuthRepository() }
    val authViewModel = remember { AuthViewModel(authRepository) }
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(googleIdToken) {
        if (googleIdToken != null) {
            authViewModel.signInWithGoogle(googleIdToken)
            onGoogleTokenConsumed()
        }
    }

    if (!authState.isAuthenticated) {
        var currentAuthScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

        when (currentAuthScreen) {
            AuthScreen.Login -> {
                LoginScreen(
                    viewModel = authViewModel,
                    onGoogleSignInClick = onGoogleSignInClick,
                    onNavigateToRegister = { currentAuthScreen = AuthScreen.Register },
                    modifier = modifier,
                )
            }
            AuthScreen.Register -> {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { currentAuthScreen = AuthScreen.Login },
                    modifier = modifier,
                )
            }
        }
    } else {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.TableInput) }

        val firebaseRemoteDataSource = remember { FirebaseRemoteDataSource() }
        val tableRepository = remember { FirebaseTableRepository(firebaseRemoteDataSource) }
        val syncRepository = remember { FirebaseRealtimeSyncRepository() }
        val calculateSplitExpensesUseCase = remember { CalculateSplitExpensesUseCase() }

        val tableViewModel = remember { TableViewModel(tableRepository, syncRepository) }
        val expenseViewModel = remember { ExpenseViewModel(tableRepository) }
        val summaryViewModel = remember { SummaryViewModel(tableRepository, calculateSplitExpensesUseCase) }

        when (val screen = currentScreen) {
            is Screen.TableInput -> {
                TableScreen(
                    viewModel = tableViewModel,
                    onNavigateToExpenses = { tableId ->
                        currentScreen = Screen.ExpenseInput(tableId)
                    },
                    onSignOut = { authViewModel.signOut() },
                    modifier = modifier,
                )
            }
            is Screen.ExpenseInput -> {
                ExpenseScreen(
                    tableId = screen.tableId,
                    viewModel = expenseViewModel,
                    onNavigateToSummary = { tableId ->
                        currentScreen = Screen.SummaryView(tableId)
                    },
                    modifier = modifier,
                )
            }
            is Screen.SummaryView -> {
                SummaryScreen(
                    tableId = screen.tableId,
                    viewModel = summaryViewModel,
                    onRestart = {
                        currentScreen = Screen.TableInput
                    },
                    modifier = modifier,
                )
            }
        }
    }
}
