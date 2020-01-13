# Code Explanation

Are you interested in learning how this all works? Below is a complete overview of the steps taken to create this project.

Let's get started!

## Prerequisites

- Computer: preferably 8+ GB RAM and 4+ core CPU
- [Android Studio](https://developer.android.com/studio)
- [Android Emulator](https://developer.android.com/studio/run/emulator)
- [TFLite Model and Labels](https://github.com/tensorflow/examples/blob/master/lite/examples/object_detection/android/README.md#model-used)

## Setup

Setup for this project is not bad at all.

1. Open Android Studio

2. Create a new Kotlin project (select "Empty Activity" and 26 minimum SDK version)

3. In order to not compress the TFLite files, in the `android` section of the `build.gradle` file for the app add 
```kotlin
    aaptOptions {
        noCompress "tflite"
    }
```

4. To add the dependencies that we'll need, in the `dependencies` section of the `build.gradle` file for the app add
```kotlin
    //TF Lite dependency declarations
    implementation 'org.tensorflow:tensorflow-lite:0.0.0-nightly'
    implementation 'org.tensorflow:tensorflow-lite-gpu:0.0.0-nightly'
    implementation 'org.tensorflow:tensorflow-lite-support:0.0.0-nightly'

    //CameraX dependency declarations
    def camerax_version = "1.0.0-alpha08"
    implementation "androidx.camera:camera-core:${camerax_version}"
    implementation "androidx.camera:camera-camera2:${camerax_version}"
    implementation "androidx.core:core-ktx:+"
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
 ````
 
5. Resync the project

## UI

Often overlooked, the UI is a very important part of our app. Everything we will be adding in this stage will be in `activity_main.xml` of our project.

1. To add a `TextureView` for our camera output to go, add
```xml
    <TextureView
        android:id="@+id/texture_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
We set a new id and use `"match_parent"` to make the `TextureView` take up the entire screen space.

2. The top part of our app will consist of the "SeeFood" title and the unofficial motto, "the shazam for food". To add the title, we can use a `TextView` at the top as follows:
```xml
    <TextView
        android:id="@+id/title"
        android:layout_width="match_parent"
        android:layout_height="57dp"
        android:background="#F00"
        android:text="SEEFOOD"
        android:textAlignment="center"
        android:textColor="#FFF"
        android:textSize="18pt"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
```
and the same idea for the motto:
```xml
    <TextView
        android:id="@+id/motto"
        android:layout_width="match_parent"
        android:layout_height="33dp"
        android:layout_below="@id/title"
        android:layout_marginTop="57dp"
        android:background="#FFF"
        android:text="&quot;the shazam for food&quot;"
        android:textAlignment="center"
        android:textColor="#F00"
        android:textSize="10pt"
        android:textStyle="italic"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

3. We also need to add some buttons for our user to interact with. We'll need a button to evaluate the image, as well as a button to reset and go again. For these, we'll use the `Button` feature.
```xml
    <Button
        android:id="@+id/evaluate_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="50dp"
        android:text="EVALUATE"
        android:textAlignment="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
        
    <Button
        android:id="@+id/reset_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="50dp"
        android:text="AGAIN"
        android:visibility="gone"
        android:textAlignment="center"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
```
Note the use of the `android:visibility="gone"`, which indicates that the button will not appear at startup.

4. To display the captured image, we'll use the middle 300dp x 300dp and create an `ImageView` for it to display later. Similar to previously, we'll set it so that it is not visible at first.
```xml
    <ImageView
        android:id="@+id/captured_image"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:visibility="gone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        tools:ignore="contentDescription" />
```

5. We'll also have to add an evaluation text attribute, which will be displayed similarly with a `TextView`.
```xml
    <TextView
        android:id="@+id/food_evaluation_text"
        android:layout_width="match_parent"
        android:layout_height="33dp"
        android:layout_marginBottom="10dp"
        android:background="#FFF"
        android:text="@id/food_evaluation_text"
        android:textAlignment="center"
        android:textColor="#000"
        android:visibility="gone"
        android:textSize="10pt"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
```

6. Lastly but not least, we need to have a picture of Jian Yang :grin: This is actually quite simple to do in xml. First, download the [picture](https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&ved=2ahUKEwjS3fGd0v_mAhXQnuAKHbd1DBUQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.vulture.com%2F2018%2F04%2Fjimmy-o-yang-crazy-rich-asians-silicon-valley-interview.html&psig=AOvVaw2Neaa14AOKWdNHATExeKqL&ust=1578972810634485)(source: Vulture). Place the image in the `res/drawable` folder, and access it as follows:
```xml
    <ImageView
        android:id="@+id/imageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:adjustViewBounds="true"
        android:maxWidth="200dp"
        android:maxHeight="175dp"
        app:srcCompat="@drawable/jian_yang"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintRight_toRightOf="parent" />
```
Note that we rescale with `android:maxWidth="200dp"` and `android:maxHeight="175dp"` to make our favorite character fit into the corner.

If you'd like to remove the green top border that is default with Android Studio apps, replace the `styles.xml` file with
```xml
<resources>
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar" />
</resources>
```

## Main

With the UI completed, the next order of buisness is setting up our main file. This is arguably the most difficult part of the project, but do not fear, with a little support we'll be good to go! All of these changes will be in the `MainActivity` class of `MainActivity.kt` with the notable exception of our `Detector` class.

1. First, we need to add some variables.
```kotlin
    private val CAMERA_REQUEST_CODE = 69420
    private var cameraPreview: TextureView? = null
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var camera: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var detector: Detector? = null
    private var backgroundHandler: Handler? = null
```
The `CAMERA_REQUEST_CODE` can be any number you'd like. Note the use of the null-safety when dealing with these variables. If you're confused by the `detector` variable, that will be covered in the later section :)

2. Next, we'll create a function that will be called when our app is run, `onCreate`. In this, we'll set up our camera and detector, set up our evaluate button.
```kotlin
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
```

3. Our `setupEvaluateButton` function will start a listener for the corresponding button that we made in the `activity_main.xml` file. In this listener, when the user clicks the button, we will generate a bitmap with what the camera is viewing. Then, we have to crop our bitmap to fit the 300x300 size requirement for the TFLite model downloaded from the prereqs. Afterwards, we can display the captured image and make the evaluate button dissapear momentarily. Finally, we will we use our `Detector` class to evaluate the image.
```kotlin
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
```

4. Alright, now to deal with this `Detector` thing I've been mentioning. This is the class where we'll be using our TFLite model to run an inference on the captured image. Everything in this step will be in the `Detector` class of the `Detector.kt` file.

First, we'll add some `private val`s that we'll use when running with TFLite.
```kotlin
    private val tflite: Interpreter
    private val imageData: ByteBuffer
    private val assetManager = context.getAssets()
    private val pixels = IntArray(300 * 300)
    private val labels = Vector<String>()
```
We'll obviously need our `tflite` interpreter, `imageData` as a `ByteBuffer` to work with our image, `assetManager` to read in files, and `pixels` and `labels` for storing the image and labels.

Then, in the `init` function we'll start with converting our image. Let's read in our TFLite model through at `ByteBuffer` and label files adding them to a `Vector`.
```kotlin
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
```
Note that we have to allocate for a size of 300 * 300 * 3, the image size and 3 for the RGB color.

Lastly is the `foodEvaluation` function. 

First, we'll rewind `imageData` to start from the beginning, and get the pixles from the image that we need. Note that we don't have to scale, the image is already of size 300 x 300, excatly what the model wants.
```kotlin
        imageData.rewind()
        picture.getPixels(pixels, 0, 300, 0, 0, 300, 300)
```

We then have to go over all of the pixels and put their values into the `Byte` format that the model will understand. We will use some binary operations to do so, finally converting the values to bytes. We then cast it to an array of `Any` for our input. You can think of it as "cleaning" the input.
```kotlin
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
```

Next is the output, which will be a `HashMap` with the locations, classes, scores, and detections that our model will give.
```kotlin
        val locations = Array(1) { Array(10) { FloatArray(4) } }
        val classes = Array(1) { FloatArray(10) }
        val scores = Array(1) { FloatArray(10) }
        val detections = FloatArray(1)
        val outputMap = HashMap<Int, Any>()
        outputMap[0] = locations
        outputMap[1] = classes
        outputMap[2] = scores
        outputMap[3] = detections
```

Then we can run with TFLite:
```kotlin
        tflite.runForMultipleInputsOutputs(inputArray, outputMap)
```

and return a `Pair<String, Float>` of our predicted class and with what probability.
```kotlin
        val label = labels[classes[0][0].toInt()+1]
        val score = scores[0][0]
        return Pair(label, score)
```

5. Ok, back to the main file. We have to define a reset function, pretty much when the user presses the reset button we'll revert everything back to the way it way originally.
```kotlin
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
```

6. Next comes the fun function (no pun intended), `setFood`. 
```kotlin
private fun setFood(evaluation: Pair<String, Float>)
```
This is where we take in the evaluation returned to us by `Detector` and output to the screen our conclusions.

SEEFOOD V1: hotdog vs not hotdog
```kotlin
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
```
Pretty much a simple if statement and we just update the vibilities of the text and buttons, real great job Jian Yang :joy:

SEEFOOD V2: life, the universe, and everything food
```kotlin
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
```
Now that's better, haha. Pretty simple logic statements.

7. Finally comes a slew of camera helper functions. Be prepared!

The `onResume` function is where we see if the the camera's avaliable, and if it is we can `findAndOpen` it. otherwise, we'll intialize our `surfaceTextureListener`.
```kotlin
    override fun onResume() {
        super.onResume()
        if (cameraPreview?.isAvailable == true) {
            findAndOpenCamera()
        } else {
            cameraPreview?.surfaceTextureListener = surfaceTextureListener
        }
    }
```

First off, we need to find the camera. We'd like the back facing one, thanks. To do this, let's just iterate through all of the cameras and once we get one that's facing the back, that's the one we want. Then we can open it, first checking if we have permission to access it.
```kotlin
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
```

To be continued... :)

