package com.example.fullprovodnici

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Insets
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator
import java.io.IOException

import android.app.Notification
import android.app.Service
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class MediaProjectionForegroundService : Service() {

    companion object {
        private val TAG = MediaProjectionForegroundService::class.java.simpleName
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        // Perform any necessary initialization or processing here
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        // Clean up resources or perform any necessary cleanup here
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved")
        // Handle task removal here, if needed
    }
}

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SCREEN_CAPTURE = 1000
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        val startButton: Button = findViewById(R.id.start_recording_button)
        val stopButton: Button = findViewById(R.id.stop_recording_button)

        startButton.setOnClickListener {
            if (!isRecording) {
                startScreenCapture()
            }
        }

        stopButton.setOnClickListener {
            if (isRecording) {
                stopScreenCapture()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val screenCaptureContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // Handle the result as before
            if (data != null) {
                mediaProjection = mediaProjectionManager.getMediaProjection(result.resultCode, data)
                startRecording()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureContract.launch(captureIntent)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        mediaRecorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(getExternalFilesDir(null)?.absolutePath + "/screen_record.mp4")
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoSize(getScreenWidth(this@MainActivity), getScreenHeight(this@MainActivity))
            setVideoFrameRate(30)
            setVideoEncodingBitRate(5 * 1000 * 1000)
            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val surface: Surface = mediaRecorder.surface
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenRecorder",
            getScreenWidth(this), getScreenHeight(this), resources.displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            surface, null, null
        )

        mediaRecorder.start()
        isRecording = true

        findViewById<Button>(R.id.start_recording_button).visibility = View.GONE
        findViewById<Button>(R.id.stop_recording_button).visibility = View.VISIBLE
    }

    private fun stopScreenCapture() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        virtualDisplay?.release()
        mediaProjection?.stop()
        isRecording = false

        findViewById<Button>(R.id.start_recording_button).visibility = View.VISIBLE
        findViewById<Button>(R.id.stop_recording_button).visibility = View.GONE
    }

    fun getScreenWidth(activity: Activity): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.getWindowInsets()
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return windowMetrics.getBounds().width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    }
    private fun getScreenHeight(activity: Activity): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.getWindowInsets()
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            return windowMetrics.getBounds().height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }
    }
}