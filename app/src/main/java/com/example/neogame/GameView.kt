//GameView.kt
package com.example.neogame

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import kotlin.random.Random

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback, View.OnTouchListener {
    private var thread: GameThread? = null
    private val squarePaint: Paint = Paint()
    private var squareX: Float = 0f
    private var squareY: Float = 0f
    private var isMovingLeft: Boolean = false
    private var isMovingRight: Boolean = false
    private val squareCreationDelay = 1000L // Настройте значение задержки по необходимости (в миллисекундах)
    private val playerImage = BitmapFactory.decodeResource(resources, R.drawable.neoboy)
    private val meteorImage = BitmapFactory.decodeResource(resources, R.drawable.meteor)

    val squares: MutableList<Square> = mutableListOf()

    init {
        holder.addCallback(this)
        squarePaint.color = Color.RED
        setOnTouchListener(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        squareX = (width / 2).toFloat()
        squareY = 600f // Новое начальное значение squareY
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

    private fun gameOver() {
        thread?.running = false // Остановка игры

    }

    fun startGame() {
        thread = GameThread(holder, this)
        thread?.running = true
        thread?.start()
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
    }
}
