package app.mv

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun Lavender(
    onGranted: @Composable () -> Unit,
    onDenied: @Composable () -> Unit
) {
    var granted by remember { mutableStateOf<Boolean?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
                            permissions[Manifest.permission.READ_MEDIA_VIDEO] == true
        granted = hasPermission
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        )
    }

    when (granted) {
        true -> onGranted()
        false -> onDenied()
        null -> {} // still waiting for user to respond
    }
}
