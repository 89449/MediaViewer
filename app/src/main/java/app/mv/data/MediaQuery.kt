package app.mv.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MediaFolder(
    val id: Long,
    val name: String,
    val thumbnailUri: Uri,
    val itemCount: Int
)

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val dateAdded: Long,
    val size: Long,
    val duration: Long?
)

suspend fun getMediaFolders(context: Context): List<MediaFolder> {
    return withContext(Dispatchers.IO) {
        val folders = mutableListOf<MediaFolder>()
        val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val projection = arrayOf(
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE
        )

        val selection = "(${MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'image/%' OR ${MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'video/%')"

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val folderMap = mutableMapOf<Long, MediaFolder>()

        try {
            context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
                val bucketDisplayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getLong(bucketIdColumn)
                    val bucketName = cursor.getString(bucketDisplayNameColumn)
                    val itemId = cursor.getLong(idColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val itemUri = ContentUris.withAppendedId(uri, itemId)

                    folderMap.compute(bucketId) { _, currentFolder ->
                        if (currentFolder == null) {
                            MediaFolder(
                                id = bucketId,
                                name = bucketName ?: "Unknown",
                                thumbnailUri = itemUri,
                                itemCount = 1
                            )
                        } else {
                            currentFolder.copy(itemCount = currentFolder.itemCount + 1)
                        }
                    }
                }
            }
        } catch (e: Exception) {
        	
        }

        folders.addAll(folderMap.values.sortedByDescending { it.itemCount })
        return@withContext folders
    }
}

suspend fun getMediaItemsForFolder(context: Context, folderId: Long): List<MediaItem> {
    return withContext(Dispatchers.IO) {
        val mediaItems = mutableListOf<MediaItem>()
        val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Video.Media.DURATION
        )

        val selection = "${MediaStore.Files.FileColumns.BUCKET_ID} = ? AND " +
                "(${MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'image/%' OR " +
                "${MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'video/%')"

        val selectionArgs = arrayOf(folderId.toString())
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val size = cursor.getLong(sizeColumn)
                    val duration = if (mimeType.startsWith("video/")) cursor.getLong(durationColumn) else null

                    val itemUri = ContentUris.withAppendedId(uri, id)

                    mediaItems.add(
                        MediaItem(
                            id = id,
                            uri = itemUri,
                            displayName = displayName ?: "Unknown",
                            mimeType = mimeType ?: "",
                            dateAdded = dateAdded,
                            size = size,
                            duration = duration
                        )
                    )
                }
            }
        } catch (e: Exception) {
            
        }
        return@withContext mediaItems
    }
}
