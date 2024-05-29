package ua.kpi.its.lab.security

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import ua.kpi.its.lab.security.ui.App

/**
 * ***********************************************************************
 * ***********************************************************************
 * ************************ NO NEED TO EDIT ******************************
 * ***********************************************************************
 * ***********************************************************************
 */

fun main(args: Array<String>) = application {
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        width = 576.dp,
        height = 1024.dp,
    )
    val client = http
    Window(
        onCloseRequest = {
            http.close()
            exitApplication()
        },
        state = windowState,
    ) {
        CompositionLocalProvider(
            LocalHttpClient provides client
        ) {
            MaterialTheme {
                App()
            }
        }
    }
}

private val http by lazy {
    HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        engine {
            config {
                followRedirects(true)
            }
        }
    }
}

val LocalHttpClient = staticCompositionLocalOf<HttpClient> {
    error("HttpClient not provided")
}