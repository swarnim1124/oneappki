package com.xsc.oneapp.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DynamicFeatureInstallScreen(
    moduleName: String,
    status: ModuleInstallStatus,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Feature Module: ${moduleName.replaceFirstChar { it.uppercase() }}",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (status) {
            is ModuleInstallStatus.Downloading -> {
                Text(
                    text = "Downloading feature components...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { status.progress },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            is ModuleInstallStatus.Installing -> {
                Text(
                    text = "Installing module...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }
            is ModuleInstallStatus.Failed -> {
                Text(
                    text = "Installation Failed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry Download")
                }
            }
            else -> {
                CircularProgressIndicator()
            }
        }
    }
}
