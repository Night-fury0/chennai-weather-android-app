package nightfury0.android.chennaiweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import coil.load
import kotlinx.coroutines.runBlocking

class Satellite : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.satellite)

        val satellite1 = findViewById<ImageView>(R.id.satellite1)
        runBlocking {
            satellite1.load(getString(R.string.satellite_wv_url))
        }

        val wvButton = findViewById<Button>(R.id.wvButton)
        val visButton = findViewById<Button>(R.id.visButton)
        val irlButton = findViewById<Button>(R.id.irlButton)
        val ctbtButton = findViewById<Button>(R.id.ctbtButton)

        wvButton.setOnClickListener {
            runBlocking {
                satellite1.load(getString(R.string.satellite_wv_url))
            }
        }

        visButton.setOnClickListener {
            runBlocking {
                satellite1.load(getString(R.string.satellite_vis_url))
            }
        }

        irlButton.setOnClickListener {
            runBlocking {
                satellite1.load(getString(R.string.satellite_ir1_url))
            }
        }

        ctbtButton.setOnClickListener {
            runBlocking {
                satellite1.load(getString(R.string.satellite_ctbt_url))
            }
        }



    }
}