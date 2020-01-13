package com.example.seefood

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.BufferedReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.Vector
import android.util.Log

class Detector(context: Context) {

    private val tflite: Interpreter
    private val imageData: ByteBuffer
    private val assetManager = context.getAssets()
    private val pixels = IntArray(300 * 300)
    private val labels = Vector<String>()

    init {
        val fd = context.assets.openFd("detect.tflite")
        val input = FileInputStream(fd.fileDescriptor)
        val channel = input.channel
        val buffer = channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
        tflite = Interpreter(buffer, null)
        imageData = ByteBuffer.allocateDirect( 300 * 300 * 3)
        imageData.order(ByteOrder.nativeOrder())
        val br = BufferedReader(InputStreamReader(assetManager.open("labelmap.txt")))
        while(true) {
            val line = br.readLine()?: break
            labels.add(line)
        }
    }

    fun foodEvaluation(picture: Bitmap): Pair<String, Float> {

        Log.d("DETECTOR","RECEIVED CALL")

        imageData.rewind()
        picture.getPixels(pixels, 0, 300, 0, 0, 300, 300)

        var index = 0
        for (i in 1..300) {
            for (j in 1..300) {
                val pixelValue = pixels[index++]
                imageData.put((pixelValue shr 16 and 0xFF).toByte())
                imageData.put((pixelValue shr 8 and 0xFF).toByte())
                imageData.put((pixelValue and 0xFF).toByte())
            }
        }
        val inputArray = arrayOf<Any>(imageData)


        val locations = Array(1) { Array(10) { FloatArray(4) } }
        val classes = Array(1) { FloatArray(10) }
        val scores = Array(1) { FloatArray(10) }
        val detections = FloatArray(1)
        val outputMap = HashMap<Int, Any>()
        outputMap[0] = locations
        outputMap[1] = classes
        outputMap[2] = scores
        outputMap[3] = detections

        Log.d("DETECTOR","RUNNING TFLITE")
        tflite.runForMultipleInputsOutputs(inputArray, outputMap)
        Log.d("DETECTOR","RETURNING CALL")

        val label = labels[classes[0][0].toInt()+1]
        val score = scores[0][0]
        return Pair(label, score)

    }
}