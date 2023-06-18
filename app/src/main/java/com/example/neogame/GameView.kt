//GameView.kt
package com.example.neogame

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.random.Random
import kotlin.concurrent.fixedRateTimer

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {
    private var thread: GameThread? = null
    private val squarePaint: Paint = Paint()
    private val scorePaint: Paint = Paint()
    private var squareX: Float = 0f
    private var squareY: Float = 0f
    private var isMovingLeft: Boolean = false
    private var isMovingRight: Boolean = false
    private val squareCreationDelay = 1000L // Настройте значение задержки по необходимости (в миллисекундах)
    private val playerImage = BitmapFactory.decodeResource(resources, R.drawable.neoboy)
    private val meteorImage = BitmapFactory.decodeResource(resources, R.drawable.meteor)
    private lateinit var mainHandler: Handler
    private var gameContainer: FrameLayout
    private var startButton: Button
    private var gameOverText: TextView
    private var restartButton: Button
    private var scoreText: TextView
    private val gameOverRunnable: Runnable = Runnable {
        showGameOverUI()
    }

    val squares: MutableList<Square> = mutableListOf()
    private var score: Int = 0

    init {
        holder.addCallback(this)
        squarePaint.color = Color.RED
        scorePaint.color = Color.BLACK
        scorePaint.textSize = 48f
        setOnTouchListener(this)
        gameContainer = (context as Activity).findViewById(R.id.gameContainer)
        startButton = context.findViewById(R.id.startButton)
        gameOverText = context.findViewById(R.id.gameOverText)
        restartButton = context.findViewById(R.id.restartButton)
        scoreText = context.findViewById(R.id.scoreText)


    }

    private fun gameOver() {
        thread?.running = false // Остановка игры

        // Показать экран "Game Over" с задержкой 1 секунда
        mainHandler.postDelayed(gameOverRunnable, 1000)}

        private fun showGameOverUI() {
        gameContainer.visibility = View.GONE
        startButton.visibility = View.GONE
        gameOverText.visibility = View.VISIBLE
        restartButton.visibility = View.VISIBLE
        scoreText.text = "Score: $score"
        scoreText.visibility = View.VISIBLE
    }





    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        squareX = (width / 2).toFloat()
        squareY = height.toFloat() + 50f - playerImage.height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        thread?.running = false
        while (retry) {
            try {
                thread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(playerImage, squareX - 50, squareY - 50, null)

        val iterator = squares.iterator()
        while (iterator.hasNext()) {
            val square = iterator.next()
            canvas.drawBitmap(meteorImage, null, square.rect, null)
        }

        // Отрисовка счетчика очков
        canvas.drawText("Score: $score", width - 300f, 80f, scorePaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val buttonWidth = width / 2
                if (x < buttonWidth) {
                    isMovingLeft = true
                } else {
                    isMovingRight = true
                }
            }
            MotionEvent.ACTION_UP -> {
                isMovingLeft = false
                isMovingRight = false
            }
        }
        return true
    }

    private fun moveSquareLeft() {
        if (squareX - 10 >= -50) {
            squareX -= 10
        }
    }

    private fun moveSquareRight() {
        if (squareX + 10 <= width - 50) {
            squareX += 10
        }
    }

    fun update() {
        if (isMovingLeft) {
            moveSquareLeft()
        }
        if (isMovingRight) {
            moveSquareRight()
        }

        // Проверка на границу нижнего края экрана
        if (squareY + 10 <= height - 100) {
            squareY += 10
        }

        val iterator = squares.iterator()
        while (iterator.hasNext()) {
            val square = iterator.next()
            square.move()

            // Удаление падающих квадратов, вышедших за пределы экрана
            if (square.rect.top >= height - 50) {
                iterator.remove()
            }

            // Проверка столкновения с игроком
            if (square.intersects(squareX, squareY - 50, squareX + 100, squareY + 50)) {
                gameOver()
            }
        }
    }

//    private fun gameOver() {
//        thread?.running = false // Остановка игры
//
//    }

    fun startGame() {
        score = 0
        squares.clear()
        gameOverText.visibility = View.GONE
        restartButton.visibility = View.GONE
        scoreText.visibility = View.GONE
        gameContainer.visibility = View.VISIBLE
        startButton.visibility = View.GONE
        thread = GameThread(holder, this)
        thread?.running = true
        thread?.start()

        // Запустить таймер для увеличения счетчика очков каждую секунду
        fixedRateTimer("ScoreTimer", false, 1000L, 1000L) {
            score++
        }
        for (i in 0 until 100) {
            postDelayed(
                {
                    val square = Square(
                        Random.nextFloat() * width,
                        -100f,
                        Random.nextInt(50, 150).toFloat(),
                        Random.nextInt(3, 8).toFloat()
                    )
                    squares.add(square)
                },
                i * squareCreationDelay
            )
        }
        mainHandler = Handler(Looper.getMainLooper())
    }
}

