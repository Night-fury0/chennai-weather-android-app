package nightfury0.android.chennaiweather

import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import coil.load
import kotlinx.coroutines.runBlocking

class Satellite : AppCompatActivity() {

    private lateinit var satelliteImageView: ImageView
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var startDistance = 0f
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private var matrix = Matrix()

    private fun calculateDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.satellite)

        satelliteImageView = findViewById(R.id.satelliteImageView)
        runBlocking {
            satelliteImageView.load(getString(R.string.satellite_wv_url))
        }

        val wvButton = findViewById<Button>(R.id.wvButton)
        val visButton = findViewById<Button>(R.id.visButton)
        val irlButton = findViewById<Button>(R.id.irlButton)
        val ctbtButton = findViewById<Button>(R.id.ctbtButton)

        satelliteImageView.setOnTouchListener { _, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mode = DRAG
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    startDistance = calculateDistance(event)
                    if (startDistance > 10f) mode = ZOOM
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        val deltaX = event.x - lastTouchX
                        val deltaY = event.y - lastTouchY
                        matrix.postTranslate(deltaX,deltaY)
                        satelliteImageView.imageMatrix = matrix
                        lastTouchY = event.x
                        lastTouchY = event.y
                    }
                    else if (mode == ZOOM) {
                        satelliteImageView.scaleType = ImageView.ScaleType.MATRIX
                        val newDistance = calculateDistance(event)
                        if (newDistance > 10f) {
                            val scale = newDistance / startDistance
                            matrix.setScale(scale, scale, satelliteImageView.width/2f, satelliteImageView.height/2f)
                        }
                    }
                    satelliteImageView.imageMatrix = matrix
                    lastTouchY = event.y
                    lastTouchX = event.x
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }
            return@setOnTouchListener true
        }

        wvButton.setOnClickListener {
            runBlocking {
                satelliteImageView.load(getString(R.string.satellite_wv_url))
            }
            satelliteImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        visButton.setOnClickListener {
            runBlocking {
                satelliteImageView.load(getString(R.string.satellite_vis_url))
            }
            satelliteImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        irlButton.setOnClickListener {
            runBlocking {
                satelliteImageView.load(getString(R.string.satellite_ir1_url))
            }
            satelliteImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        ctbtButton.setOnClickListener {
            runBlocking {
                satelliteImageView.load(getString(R.string.satellite_ctbt_url))
            }
            satelliteImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }



    }
}