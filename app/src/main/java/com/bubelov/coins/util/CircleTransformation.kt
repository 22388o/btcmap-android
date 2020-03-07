package com.bubelov.coins.util

import android.graphics.*
import com.squareup.picasso.Transformation

class CircleTransformation : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)

        val x = (source.width - size) / 2
        val y = (source.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)

        if (squaredBitmap != source) {
            source.recycle()
        }

        val paint = Paint().apply {
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            isAntiAlias = true
        }

        val roundBitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(roundBitmap)
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        squaredBitmap.recycle()
        return roundBitmap
    }

    override fun key(): String = javaClass.simpleName
}