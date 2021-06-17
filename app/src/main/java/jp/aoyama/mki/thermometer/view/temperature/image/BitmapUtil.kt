package jp.aoyama.mki.thermometer.view.temperature.image

import android.graphics.*


fun Bitmap.toGrayScale(): Bitmap {
    val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    canvas.drawBitmap(this, 0f, 0f, paint)
    return grayscale
}

fun Bitmap.lightFilter(): Bitmap {
    val lighter = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(lighter)
    val paint = Paint()
    val filter = LightingColorFilter(0xFFFFFFFF.toInt(), 0x00222222) // lighten
    paint.colorFilter = filter
    canvas.drawBitmap(this, 0f, 0f, paint)
    return lighter
}

fun Bitmap.contrast(contrast: Float, brightness: Float): Bitmap {
    val cm = ColorMatrix(
        floatArrayOf(
            contrast,
            0f,
            0f,
            0f,
            brightness,
            0f,
            contrast,
            0f,
            0f,
            brightness,
            0f,
            0f,
            contrast,
            0f,
            brightness,
            0f,
            0f,
            0f,
            1f,
            0f
        )
    )

    val ret = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(ret)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(this, 0f, 0f, paint)

    return ret
}