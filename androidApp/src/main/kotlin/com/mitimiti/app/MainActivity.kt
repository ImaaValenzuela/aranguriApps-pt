package com.mitimiti.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Make window display in immersive full-screen mode, hiding status and navigation bars
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            var googleIdToken by remember { mutableStateOf<String?>(null) }

            val googleSignInOptions = remember {
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            }

            val googleSignInLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val account = try {
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                            .getResult(ApiException::class.java)
                    } catch (e: ApiException) {
                        null
                    }
                    googleIdToken = account?.idToken
                }
            }

            val onGoogleSignInClick = {
                val client = GoogleSignIn.getClient(this@MainActivity, googleSignInOptions)
                googleSignInLauncher.launch(client.signInIntent)
            }

            App(
                onGoogleSignInClick = onGoogleSignInClick,
                googleIdToken = googleIdToken,
                onGoogleTokenConsumed = { googleIdToken = null },
            )
        }
    }
}
