package nightfury0.android.chennaiweather

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        val developerTextView = findViewById<TextView>(R.id.developerTextView)
        developerTextView.movementMethod = LinkMovementMethod.getInstance()

        val weatherButton = findViewById<Button>(R.id.weatherButton)
        val radarButton = findViewById<Button>(R.id.radarButton)
        val satelliteButton = findViewById<Button>(R.id.satelliteButton)
        val lakeLevelButton = findViewById<Button>(R.id.lakeLevelButton)
        val rainfallButton = findViewById<Button>(R.id.rainfallButton)
        val stationsButton = findViewById<Button>(R.id.stationsButton)

        weatherButton.setOnClickListener{
            val intent = Intent(this, Weather::class.java)
            startActivity(intent)
        }
        radarButton.setOnClickListener{
            val intent = Intent(this, Radar::class.java)
            startActivity(intent)
        }
        satelliteButton.setOnClickListener{
            val intent = Intent(this, Satellite::class.java)
            startActivity(intent)
        }
        lakeLevelButton.setOnClickListener{
            val intent = Intent(this, LakeLevel::class.java)
            startActivity(intent)
        }
        rainfallButton.setOnClickListener {
            val intent = Intent(this, Rainfall::class.java)
            startActivity(intent)
        }
        stationsButton.setOnClickListener {
            val intent = Intent(this, Stations::class.java)
            startActivity(intent)
        }

    }

}
