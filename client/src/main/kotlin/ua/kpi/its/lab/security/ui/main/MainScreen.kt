package ua.kpi.its.lab.security.ui.main


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import ua.kpi.its.lab.security.LocalHttpClient

@Composable
fun MainScreen(snackbarHostState: SnackbarHostState, token: String) {
    val scope = rememberCoroutineScope()
    val client = LocalHttpClient.current

    Column(modifier = Modifier.fillMaxSize()) {
        JournalScreen(token, scope, client, snackbarHostState)
    }
}