package nightfury0.android.chennaiweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import coil.load
import kotlinx.coroutines.runBlocking

class Radar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.radar)

        val radarImageView = findViewById<ImageView>(R.id.radarImageView)
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

        radar1Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar1Url)
            }
        }
        radar2Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar2Url)
            }
        }
        radar3Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar3Url)
            }
        }
        radar4Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar4Url)
            }
        }
        radar5Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar5Url)
            }
        }
        radar6Button.setOnClickListener {
            runBlocking {
                radarImageView.load(radar6Url)
            }
        }
    }
}