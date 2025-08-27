package app.mv.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import kotlinx.coroutines.launch

import app.mv.data.MediaItem
import app.mv.data.getMediaItemsForFolder


class AppSettingsViewModel : ViewModel() {
	var keepScreenOn by mutableStateOf(false)
        private set

    fun toggleKeepScreenOn(newValue: Boolean) {
        keepScreenOn = newValue
    }
}
