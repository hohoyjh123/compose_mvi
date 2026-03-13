package kr.jhsh.testcompose.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Settings 화면
 * - ViewModel을 통해 닉네임 상태를 관리
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToEditNickname: () -> Unit = {}
) {
    val nickname by viewModel.nickname.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileSection(
            nickname = nickname ?: "JSONPlaceholder App",
            onClick = onNavigateToEditNickname
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppInfoSection()
    }
}

@Composable
private fun ProfileSection(
    nickname: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = nickname,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "탭하여 닉네임 변경",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AppInfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ListItem(
                headlineContent = { Text("Architecture") },
                supportingContent = { Text("Clean Architecture + Multi Module") },
                leadingContent = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("UI Pattern") },
                supportingContent = { Text("MVI with StateFlow & SharedFlow") },
                leadingContent = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Modules") },
                supportingContent = { Text(":domain :data :presentation :app") },
                leadingContent = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Network") },
                supportingContent = { Text("Retrofit2 + OkHttp3") },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("API Reference") },
                supportingContent = { Text("https://jsonplaceholder.typicode.com/ \nhttps://randomuser.me/") },
                leadingContent = {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            )
        }
    }
}
