package com.example.fullprovodnici

import android.app.Activity
import android.app.Service
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
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.IOException
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody

import okhttp3.Request;
import okhttp3.RequestBody.Companion.asRequestBody

import okhttp3.Response;
import java.io.File
import java.io.FileOutputStream







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
    private val REQUEST_CODE_PICK_VIDEO = 123

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
        val sendButton: Button = findViewById(R.id.send_video_button)
        val statusTextView: TextView = findViewById(R.id.statusTextView)

        val externalFilesDir = applicationContext.getExternalFilesDir(null)
        Log.d("ExternalFilesDir", "External files directory: $externalFilesDir")

        sendButton.setOnClickListener {
            sendVideo(statusTextView)
        }
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

    private fun copyRawVideoToCache(rawResId: Int, fileName: String): File? {
        val inputStream = resources.openRawResource(rawResId)
        val outputFile = File(cacheDir, fileName)
        val outputStream = FileOutputStream(outputFile)

        try {
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            inputStream.close()
            outputStream.close()
        }
        return outputFile
        }
    private fun sendVideo(textView: TextView) {
        //textView.visibility = VISIBLE

        val url = "http://192.168.0.28:5000/record"
        val okhttpclient = OkHttpClient()
//        val formBody: RequestBody = FormBody.Builder()
//            .add("android_tag", "1")
//            .add("video", "video1")
//            .build()
        //val videoFilePath = "../video/screen_recording_20240519_210942_whatsapp.mp4"
        val videoFileName = "screen_recording_20240519_210942_whatsapp"
        val videoFile = copyRawVideoToCache(R.raw.screen_recording_20240519_210942_whatsapp, "screen_recording_20240519_210942_whatsapp.mp4")

        val requestBody = videoFile?.let {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("android_tag", "1")
                .addFormDataPart(
                    "video",
                    videoFileName,
                    it.asRequestBody("video/*".toMediaTypeOrNull())
                )
                .build()
        }

        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody!!)
            .build()

        okhttpclient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.e("SendVideo", "Failed to send video", e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
                if (response.isSuccessful) {
                    Log.d("SendVideo", "Video sent successfully")
                    // You can handle the success response here
                } else {
                    Log.e("SendVideo", "Failed to send video")
                    // Handle other response codes here
                }
            }
        })
    }


}



//var okhttpclient = OkHttpClient()
//var request: Request = Request.Builder().url("127.0.0.1:5000").build()



