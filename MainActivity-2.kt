package com.example.tasklocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                TaskLockerApp()
            }
        }
    }
}

data class TaskItem(
    val id: Int,
    val title: String,
    val targetAppPackage: String, // Örn: com.instagram.android
    var isCompleted: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
def TaskLockerApp() {
    var tasks by remember {
        mutableStateOf(
            listOf(
                TaskItem(1, "Günlük 30 Dk Spor Yap", "com.instagram.android", false),
                TaskItem(2, "20 Sayfa Kitap Oku", "com.zhiliaoapp.musically", false) // TikTok
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskLocker - Önce Görev, Sonra Ödül") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Günlük Görevlerin ve Kilitli Uygulamalar",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskRow(task = task) { updatedTask ->
                        tasks = tasks.map { if (it.id == updatedTask.id) updatedTask else it }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
def TaskRow(task: TaskItem, onTaskChanged: (TaskItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Kilitli Uygulama: ${task.targetAppPackage}", style = MaterialTheme.typography.bodySmall)
                Text(
                    text = if (task.isCompleted) "Durum: Tamamlandı (Kilit Açık)" else "Durum: Bekliyor (Kilitli)",
                    color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Button(onClick = {
                // Burada anlık kamera kanıt doğrulama ekranı tetiklenecek
                onTaskChanged(task.copy(isCompleted = !task.isCompleted))
            }) {
                Text(if (task.isCompleted) "Sıfırla" else "Kanıtla")
            }
        }
    }
}