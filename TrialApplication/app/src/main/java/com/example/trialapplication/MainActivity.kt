package com.example.trialapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.primary);
        val weatherButton = findViewById<Button>(R.id.weatherButton);
        val radarButton = findViewById<Button>(R.id.radarButton);
        val satelliteButton = findViewById<Button>(R.id.satelliteButton);
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

    }

}
