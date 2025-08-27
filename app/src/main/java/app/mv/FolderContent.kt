package app.mv

import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage

import app.mv.data.MediaItem
import app.mv.data.getMediaItemsForFolder
import app.mvutil.formatDuration

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FolderContent(folderId: Long, folderName: String, toMediaView: (mediaId: Long) -> Unit) {
	var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    
    LaunchedEffect(folderId) {
        isLoading = true
        mediaItems = getMediaItemsForFolder(context, folderId)
        isLoading = false
    }
	
	Column(modifier = Modifier.fillMaxSize()) {
		TopAppBar(
		    title = { Text(folderName) }
		)
		
		if(isLoading) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				LoadingIndicator()
			}
		} else if(mediaItems.isEmpty()) {
			
		} else {
			LazyVerticalGrid(columns = GridCells.Fixed(3)) {
				items(mediaItems) { mediaItem ->
					Card(modifier = Modifier.padding(8.dp).aspectRatio(1f).clickable { toMediaView(mediaItem.id) }) {
						Box(modifier = Modifier.fillMaxSize()) {
							AsyncImage(
							    model = mediaItem.uri,
							    contentDescription = null,
							    contentScale = ContentScale.Crop
							)
							if(mediaItem.mimeType.startsWith("video/")) {
								val duration = formatDuration(mediaItem.duration)
								Text(
								    text = duration,
								    color = MaterialTheme.colorScheme.onPrimary,
								    modifier = Modifier
								        .padding(end = 4.dp, bottom = 4.dp)
								        .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
								        .align(Alignment.BottomEnd)
								        .padding(8.dp)
								)
							}
						}
					}
				}
			}
		}
	}
	
}