package com.example.fullprovodnici

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.window.layout.WindowMetricsCalculator
import android.view.WindowManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SCREEN_CAPTURE = 1000
    }

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var mediaRecorder: MediaRecorder
    private var isRecording = false

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

    private fun startScreenCapture() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        screenCaptureContract.launch(captureIntent)
    }

    private fun startRecording() {
        val context = activity.applicationContext
        mediaRecorder = MediaRecorder(activity).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(getExternalFilesDir(null)?.absolutePath + "/screen_record.mp4")
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoSize(getScreenWidth(), getScreenHeight())
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
            getScreenWidth(), getScreenHeight(), resources.displayMetrics.densityDpi,
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

    private fun getScreenWidth(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
            val bounds = windowMetrics.bounds
            val width = bounds.width() - insets.left - insets.right
            width
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    private fun getScreenHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(android.view.WindowInsets.Type.systemBars())
            val bounds = windowMetrics.bounds
            val height = bounds.height() - insets.top - insets.bottom
            height
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
}
