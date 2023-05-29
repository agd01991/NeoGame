//GameThread.kt
package com.example.neogame

import android.graphics.Canvas

import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    var running: Boolean = false

    override fun run() {
        while (running) {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.draw(canvas)
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
            for (square in gameView.squares) {
                square.move()
            }
        }
    }
}
