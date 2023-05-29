//Square.kt
package com.example.neogame

import android.graphics.RectF

class Square(
    startX: Float,
    startY: Float,
    val size: Float,
    val speed: Float
) {
    val rect: RectF = RectF(startX, startY, startX + size, startY + size)

    fun move() {
        rect.offset(0f, speed)
    }

    fun intersects(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        return rect.intersects(left, top, right, bottom)
    }
}
