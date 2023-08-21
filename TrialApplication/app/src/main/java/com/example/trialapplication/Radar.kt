package com.example.trialapplication

import android.graphics.drawable.Drawable
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
        val radar1 = findViewById<ImageView>(R.id.radar1)
        runBlocking {
            radar1.load("https://mausam.imd.gov.in/Radar/caz_cni.gif")
        }
        val radar1Button = findViewById<Button>(R.id.radar1Button)
        val radar2Button = findViewById<Button>(R.id.radar2Button)
        val radar3Button = findViewById<Button>(R.id.radar3Button)
        val radar4Button = findViewById<Button>(R.id.radar4Button)
        val radar5Button = findViewById<Button>(R.id.radar5Button)
        val radar6Button = findViewById<Button>(R.id.radar6Button)

        radar1Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/caz_cni.gif")
            }
        }
        radar2Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/sri_cni.gif")
            }
        }
        radar3Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/pac_cni.gif")
            }
        }
        radar4Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/ppi_cni.gif")
            }
        }
        radar5Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/ppz_cni.gif")
            }
        }
        radar6Button.setOnClickListener {
            runBlocking {
                radar1.load("https://mausam.imd.gov.in/Radar/vp2_cni.gif")
            }
        }
    }
}