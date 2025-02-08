package com.example.bricks
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var tvHighestScoreLabel: TextView
    private lateinit var tvHighestScore: TextView
    private lateinit var ivNewHighest: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        ivNewHighest = findViewById(R.id.ivNewHeighest)
        tvPoints = findViewById(R.id.tvPoints)
        tvHighestScoreLabel = findViewById(R.id.tvHighestScoreLabel)
        tvHighestScore = findViewById(R.id.tvHighestScoreValue)

        val points = intent.getIntExtra("points", 0)
        tvPoints.text = points.toString()

        val sharedPreferences = getSharedPreferences("BrickBreakerPreferences", Context.MODE_PRIVATE)
        var highestScore = sharedPreferences.getInt("highestScore", 0)
        if (points > highestScore) {
            ivNewHighest.visibility = View.VISIBLE
            highestScore = points
            val editor = sharedPreferences.edit()
            editor.putInt("highestScore", highestScore)
            editor.apply()
        }

        tvHighestScore.text = highestScore.toString()
    }

    fun restart(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun exit(view: View) {
        finish()
    }
}
