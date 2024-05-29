package ua.kpi.its.lab.security.ui


import androidx.compose.animation.Crossfade
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ua.kpi.its.lab.security.ui.login.LoginScreen
import ua.kpi.its.lab.security.ui.main.MainScreen

@Composable
@Preview
fun App() {
    var token by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            if (token.isNotBlank()) {
                AppBar()
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Crossfade(
            targetState = token,
            modifier = Modifier.padding(innerPadding)
        ) { t ->
            if (t.isBlank()) {
                LoginScreen(
                    snackbarHostState,
                    updateToken = { token = it },
                )
            } else {
                MainScreen(
                    snackbarHostState,
                    token = t
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Lab 3") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
    )
}
