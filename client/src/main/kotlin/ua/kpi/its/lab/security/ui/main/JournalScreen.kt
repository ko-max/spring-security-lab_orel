package ua.kpi.its.lab.security.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ua.kpi.its.lab.security.dto.ArticleRequest
import ua.kpi.its.lab.security.dto.JournalRequest
import ua.kpi.its.lab.security.dto.JournalResponse

@Composable
fun JournalScreen(
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    snackbarHostState: SnackbarHostState
) {
    var journals by remember { mutableStateOf<List<JournalResponse>>(listOf()) }
    var loading by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var selectedJournal by remember { mutableStateOf<JournalResponse?>(null) }

    LaunchedEffect(token) {
        loading = true
        delay(1000)
        journals = withContext(Dispatchers.IO) {
            try {
                val response = client.get("http://localhost:8080/journals") {
                    bearerAuth(token)
                }
                loading = false
                response.body()
            }
            catch (e: Exception) {
                val msg = e.toString()
                snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                journals
            }
        }
    }

    if (loading) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedJournal = null
                    openDialog = true
                },
                content = {
                    Icon(Icons.Filled.Add, "add journal")
                }
            )
        }
    ) {
        if (journals.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No journals to show", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(journals) { journal ->
                    JournalItem(
                        journal = journal,
                        onEdit = {
                            selectedJournal = journal
                            openDialog = true
                        },
                        onRemove = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.delete("http://localhost:8080/journals/${journal.id}") {
                                            bearerAuth(token)
                                        }
                                        require(response.status.isSuccess())
                                    }
                                    catch(e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                    }
                                }

                                loading = true

                                journals = withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.get("http://localhost:8080/journals") {
                                            bearerAuth(token)
                                        }
                                        loading = false
                                        response.body()
                                    }
                                    catch (e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                        journals
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (openDialog) {
            JournalDialog(
                journal = selectedJournal,
                token = token,
                scope = scope,
                client = client,
                onDismiss = {
                    openDialog = false
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                    }
                },
                onConfirm = {
                    openDialog = false
                    loading = true
                    scope.launch {
                        journals = withContext(Dispatchers.IO) {
                            try {
                                val response = client.get("http://localhost:8080/journals") {
                                    bearerAuth(token)
                                }
                                loading = false
                                response.body()
                            }
                            catch (e: Exception) {
                                loading = false
                                journals
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun JournalDialog(
    journal: JournalResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val article = journal?.article

    var name by remember { mutableStateOf(journal?.name ?: "") }
    var topic by remember { mutableStateOf(journal?.topic ?: "") }
    var language by remember { mutableStateOf(journal?.language ?: "") }
    var foundationDate by remember { mutableStateOf(journal?.foundationDate ?: "") }
    var issn by remember { mutableStateOf(journal?.issn ?: "") }
    var recommendedPrice by remember { mutableStateOf(journal?.recommendedPrice ?: "") }
    var periodic by remember { mutableStateOf(journal?.periodic ?: false) }
    var articleTitle by remember { mutableStateOf(article?.title ?: "") }
    var articleAuthor by remember { mutableStateOf(article?.author ?: "") }
    var articleWritingDate by remember { mutableStateOf(article?.writingDate ?: "") }
    var articleWordCount by remember { mutableStateOf(article?.wordCount?.toString() ?: "") }
    var articleReferenceCount by remember { mutableStateOf(article?.referenceCount?.toString() ?: "") }
    var articleOriginalLanguage by remember { mutableStateOf(article?.originalLanguage ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (journal == null) {
                    Text("Create journal")
                }
                else {
                    Text("Update journal")
                }

                HorizontalDivider()
                Text("Journal info")
                TextField(name, { name = it }, label = { Text("Name") })
                TextField(topic, { topic = it }, label = { Text("Topic") })
                TextField(language, { language = it }, label = { Text("Language") })
                TextField(foundationDate, { foundationDate = it }, label = { Text("Foundation date") })
                TextField(issn, { issn = it }, label = { Text("ISSN") })
                TextField(recommendedPrice, { recommendedPrice = it }, label = { Text("Recommended price") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(periodic, { periodic = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Periodic")
                }

                HorizontalDivider()
                Text("Article info")
                TextField(articleTitle, { articleTitle = it }, label = { Text("Title") })
                TextField(articleAuthor, { articleAuthor = it }, label = { Text("Author") })
                TextField(articleWritingDate, { articleWritingDate = it }, label = { Text("Writing date") })
                TextField(articleWordCount, { articleWordCount = it }, label = { Text("Word count") })
                TextField(articleReferenceCount, { articleReferenceCount = it }, label = { Text("Reference count") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(articleOriginalLanguage, { articleOriginalLanguage = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Original language")
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = JournalRequest(
                                        name, topic, language, foundationDate, issn, recommendedPrice, periodic,
                                        ArticleRequest(
                                            articleTitle, articleAuthor, articleWritingDate,
                                            articleWordCount.toInt(), articleReferenceCount.toInt(), articleOriginalLanguage
                                        )
                                    )
                                    val response = if (journal == null) {
                                        client.post("http://localhost:8080/journals") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    } else {
                                        client.put("http://localhost:8080/journals/${journal.id}") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    require(response.status.isSuccess())
                                    onConfirm()
                                }
                                catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (journal == null) {
                            Text("Create")
                        }
                        else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JournalItem(journal: JournalResponse, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(shape = CardDefaults.elevatedShape, elevation = CardDefaults.elevatedCardElevation()) {
        ListItem(
            overlineContent = {
                Text(journal.name)
            },
            headlineContent = {
                Text(journal.topic)
            },
            supportingContent = {
                Text("ISSN: ${journal.issn}")
            },
            trailingContent = {
                Row(modifier = Modifier.padding(0.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onEdit)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onRemove)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        )
    }
}
