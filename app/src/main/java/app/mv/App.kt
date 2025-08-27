package app.mv

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.decode.VideoFrameDecoder

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        
        val videoEnabledLoader = ImageLoader.Builder(this)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
            
        Coil.setImageLoader(videoEnabledLoader)
    }
}