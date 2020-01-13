package com.example.seefood

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.round


class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 69420
    private var cameraPreview: TextureView? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var camera: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var detector: Detector? = null
    private var backgroundHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        detector = Detector(this)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }

        cameraPreview = findViewById(R.id.texture_view)

        val evaluateButton = setupEvaluateButton()
        setupResetButton(evaluateButton)
    }

    private fun setupEvaluateButton(): Button {
        val evaluateButton = findViewById<Button>(R.id.evaluate_button)
        evaluateButton.setOnClickListener {
            val fullBitmap = cameraPreview!!.bitmap

            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics).toInt()
            val leftX = (fullBitmap.width - px) / 2
            val topY = (fullBitmap.height - px) / 2
            val croppedBitmap = Bitmap.createBitmap(fullBitmap, leftX, topY, px, px)

            val capturedImage = findViewById<ImageView>(R.id.captured_image)
            capturedImage.visibility = View.VISIBLE
            capturedImage.setImageBitmap(croppedBitmap)

            val evaluateButton = findViewById<Button>(R.id.evaluate_button)
            evaluateButton.visibility = View.GONE

            Log.d("MAIN", "EVALUATE CALL SENT")
            setFood(detector!!.foodEvaluation(croppedBitmap))
        }
        return evaluateButton
    }

    private fun setupResetButton(evaluateButton: Button) {
        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            evaluateButton.visibility = View.VISIBLE
            resetButton.visibility = View.GONE
            val capturedImage = findViewById<ImageView>(R.id.captured_image)
            val foodEvaluationText = findViewById<TextView>(R.id.food_evaluation_text)
            capturedImage.visibility = View.GONE
            foodEvaluationText.visibility = View.GONE
        }
    }

    private fun setFood(evaluation: Pair<String, Float>) {
        //SEEFOOD V1
        /*
        val resetButton = findViewById<Button>(R.id.reset_button)
        val foodEvaluationText = findViewById<TextView>(R.id.food_evaluation_text)
        foodEvaluationText.setTextColor(Color.RED)
        if (evaluation.first == "hot dog") {
            foodEvaluationText.text = "HOTDOG"
            foodEvaluationText.setTextColor(Color.GREEN)
        }
        else {
            foodEvaluationText.text = "NOT HOTDOG"
        }
        foodEvaluationText.text = round(evaluation.second * 100).toString() + "% " + foodEvaluationText.text.toString()
        resetButton.visibility = View.VISIBLE
        foodEvaluationText.visibility = View.VISIBLE
        */

        //SEEFOOD V2
        val notHotdogFoods = arrayOf("banana", "apple", "sandwich", "orange", "broccoli", "carrot", "pizza", "donut", "cake")
        val resetButton = findViewById<Button>(R.id.reset_button)
        val foodEvaluationText = findViewById<TextView>(R.id.food_evaluation_text)
        foodEvaluationText.setTextColor(Color.RED)
        if (evaluation.first == "hot dog") {
            foodEvaluationText.text = "HOTDOG"
            foodEvaluationText.setTextColor(Color.GREEN)
        }
        else if (evaluation.first in notHotdogFoods){
            foodEvaluationText.text = "NOT HOTDOG, IT IS A " + evaluation.first
        }
        else {
            foodEvaluationText.text = "NOT A FOOD!"
        }
        foodEvaluationText.text = round(evaluation.second * 100).toString() + "% " + foodEvaluationText.text.toString()
        resetButton.visibility = View.VISIBLE
        foodEvaluationText.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        if (cameraPreview?.isAvailable == true) {
            findAndOpenCamera()
        } else {
            cameraPreview?.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onStop() {
        super.onStop()
        closeCamera()
    }

    private fun findAndOpenCamera() {
        for (cameraId in cameraManager?.cameraIdList ?: arrayOf()) {
            val characteristics = cameraManager?.getCameraCharacteristics(cameraId)
            if (characteristics!![CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_BACK) {
                this.cameraId = cameraId
                break
            }
        }
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraManager?.openCamera(cameraId!!, cameraStateCallback, backgroundHandler)
        }
    }

    private fun closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession?.close()
            cameraCaptureSession = null
        }
        if (camera != null) {
            camera?.close()
            camera = null
        }
    }

    private fun createPreviewSession() {
        val texture = cameraPreview!!.surfaceTexture
        val surface = Surface(texture)
        val captureRequestBuilder = camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)

        camera!!.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession) = Unit
            override fun onConfigured(session: CameraCaptureSession) {
                if (camera == null) {
                    return
                }
                val captureRequest = captureRequestBuilder.build()
                cameraCaptureSession = session
                session.setRepeatingRequest(captureRequest, null, backgroundHandler)
            }
        }, backgroundHandler)
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) = Unit
        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit
        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = true
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            findAndOpenCamera()
        }
    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            this@MainActivity.camera = camera
            createPreviewSession()
        }
        override fun onDisconnected(camera: CameraDevice) {
            this@MainActivity.camera?.close()
        }
        override fun onError(camera: CameraDevice, error: Int) {
            this@MainActivity.camera?.close()
            this@MainActivity.camera = null
        }
    }
}
