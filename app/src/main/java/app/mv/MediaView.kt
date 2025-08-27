package app.mv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.view.WindowManager

import androidx.lifecycle.viewmodel.compose.viewModel

import app.mv.data.MediaItem
import app.mv.data.getMediaItemsForFolder
import app.mv.viewmodel.AppSettingsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaView(
	mediaId: Long, 
	folderId: Long,
	appSettingsViewModel: AppSettingsViewModel
) {
    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isToolbarVisible by remember { mutableStateOf(true) }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { mediaItems.size }
    )
      
    LaunchedEffect(folderId, mediaId) {
        mediaItems = getMediaItemsForFolder(context, folderId)
        val initialIndex = mediaItems.indexOfFirst { it.id == mediaId }
        if (initialIndex != -1) {
            pagerState.scrollToPage(initialIndex)
        }
    }
    
    LaunchedEffect(isToolbarVisible) {
        val window = (context as? android.app.Activity)?.window ?: return@LaunchedEffect
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isToolbarVisible) {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
    
    LaunchedEffect(pagerState.currentPage, mediaItems) {
        if (mediaItems.isNotEmpty()) {
            val currentMediaItem = mediaItems[pagerState.currentPage]
            if (currentMediaItem.mimeType.startsWith("video/")) {
                isPlaying = true
            } else {
                isPlaying = false
            }
        }
    }
    
    LaunchedEffect(appSettingsViewModel.keepScreenOn) {
	    val window = (context as? android.app.Activity)?.window ?: return@LaunchedEffect
	    if (appSettingsViewModel.keepScreenOn) {
	        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
	    } else {
	        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
	    }
	}
      
    HorizontalPager(
        state = pagerState
    ) { page ->
        val mediaItem = mediaItems[page]
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isToolbarVisible = !isToolbarVisible
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if(mediaItem.mimeType.startsWith("image/")) {
                AsyncImage(
                    model = mediaItem.uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else if(mediaItem.mimeType.startsWith("video/")) {
                VideoPlayer(
	                mediaUri = mediaItem.uri,
	                isPlaying = isPlaying,
	                onPlayPause = { isPlaying = !isPlaying }
                )
            }
        }
    }
    
    if (isToolbarVisible) {
        Box(contentAlignment = Alignment.BottomCenter) {
            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier.navigationBarsPadding(),
                content = {
                	val currentMediaItem = mediaItems.getOrNull(pagerState.currentPage)
                	
                	if(currentMediaItem?.mimeType?.startsWith("video/") == true) {
                		IconButton(onClick = { isPlaying = !isPlaying } ) {
                			Icon(
                			    if(isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                			    contentDescription = null
                			)
                		}
                		IconButton(onClick = {}) {
                			Icon(Icons.Default.Subtitles, contentDescription = null)
                		}
                	}
                	
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                    FilledIconToggleButton(
	                    checked = appSettingsViewModel.keepScreenOn,
	                    onCheckedChange = { checked -> appSettingsViewModel.toggleKeepScreenOn(checked) }
                    ) {
                    	Icon(
	                    	Icons.Default.Coffee, 
	                    	contentDescription = null
                    	)
                    }
                }
            )
        }
    }
}