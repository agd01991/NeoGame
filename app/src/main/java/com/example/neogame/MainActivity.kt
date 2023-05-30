//MainActivity.kt
package com.example.neogame

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var gameView: GameView
    private lateinit var startButton: Button
    private lateinit var gameOverText: TextView
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        gameView = GameView(this)
        val gameContainer: FrameLayout = findViewById(R.id.gameContainer)
        gameContainer.addView(gameView)

        startButton = findViewById(R.id.startButton)
        gameOverText = findViewById(R.id.gameOverText)
        restartButton = findViewById(R.id.restartButton)

        startButton.setOnClickListener {
            startGame()
        }
        restartButton.setOnClickListener {
            restartGame()
        }
    }

    private fun startGame() {
        gameView.visibility = View.VISIBLE
        startButton.visibility = View.GONE
        gameOverText.visibility = View.GONE
        restartButton.visibility = View.GONE
        gameView.startGame()
    }
    private fun restartGame() {
        gameView.visibility = View.VISIBLE
        startButton.visibility = View.GONE
        gameOverText.visibility = View.GONE
        restartButton.visibility = View.GONE
        gameView.startGame()
    }
}