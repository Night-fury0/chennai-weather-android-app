package nightfury0.android.chennaiweather

import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import coil.load
import kotlinx.coroutines.runBlocking


class Radar : AppCompatActivity() {
    private lateinit var radarImageView: ImageView
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
        setContentView(R.layout.radar)

        radarImageView = findViewById(R.id.radarImageView)
        val radar1Url = getString(R.string.radar1_url)
        val radar2Url = getString(R.string.radar2_url)
        val radar3Url = getString(R.string.radar3_url)
        val radar4Url = getString(R.string.radar4_url)
        val radar5Url = getString(R.string.radar5_url)
        val radar6Url = getString(R.string.radar6_url)

        runBlocking {
            radarImageView.load(radar1Url)
        }
        val radar1Button = findViewById<Button>(R.id.radar1Button)
        val radar2Button = findViewById<Button>(R.id.radar2Button)
        val radar3Button = findViewById<Button>(R.id.radar3Button)
        val radar4Button = findViewById<Button>(R.id.radar4Button)
        val radar5Button = findViewById<Button>(R.id.radar5Button)
        val radar6Button = findViewById<Button>(R.id.radar6Button)

        radarImageView.setOnTouchListener { _, event ->
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
                        radarImageView.imageMatrix = matrix
                        lastTouchY = event.x
                        lastTouchY = event.y
                    }
                    else if (mode == ZOOM) {
                        radarImageView.scaleType = ImageView.ScaleType.MATRIX
                        val newDistance = calculateDistance(event)
                        if (newDistance > 10f) {
                            val scale = newDistance / startDistance
                            matrix.setScale(scale,scale,radarImageView.width / 2f,radarImageView.height / 2f)
                        }
                    }
                    radarImageView.imageMatrix = matrix
                    lastTouchY = event.y
                    lastTouchX = event.x
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }
            return@setOnTouchListener true
        }


        radar1Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar1Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        radar2Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar2Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        radar3Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar3Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        radar4Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar4Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        radar5Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar5Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
        radar6Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar6Url)
            }
            radarImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }
}