@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package app.mv

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.foundation.selection.*
import androidx.compose.ui.semantics.Role
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack


@Composable
fun SettingsScreen() {
	Column(modifier = Modifier.fillMaxSize()) {
		TopAppBar(
		    title = { Text("Settings") },
		    navigationIcon = {
		    	IconButton(onClick = {}) {
		    		Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
		    	}
		    }
		)
		
		SettingsContent()
	}
}

@Composable
fun SettingsContent() {
	var showDialog by remember { mutableStateOf(false) }
	
	
	Text(text = "Appereance", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 24.dp))
	Column {
		Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { showDialog = true}) {
			Row(modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
				Text(
				    text = "Choose which media to include",
				    color = MaterialTheme.colorScheme.onSurfaceVariant,
				    style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}