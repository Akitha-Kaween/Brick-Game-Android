package com.example.bricks

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        val plybtn = findViewById<ImageButton>(R.id.plybtn)

        val optionbtn = findViewById<ImageButton>(R.id.optionbtn)

        val quitbtn = findViewById<ImageButton>(R.id.quitbtn)

        plybtn.setOnClickListener {
            startGame()
        }
        optionbtn.setOnClickListener {
            stopGame()
        }
        quitbtn.setOnClickListener {
            stopGame()
        }
    }

    private fun startGame() {
        val gameView = GameView(this)
        setContentView(gameView)
    }

    private fun stopGame() {

        finish()
    }
}