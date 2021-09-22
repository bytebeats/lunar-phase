package me.bytebeats.views.lunarphase

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * Created by bytebeats on 2021/9/22 : 12:04
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class LunarPhaseView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleRes) {

    var rotate: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var phase: Int = 0
        set(value) {
            field = value
            invalidate()
        }


    private val mPaint by lazy { Paint() }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return
        canvas.drawColor(ContextCompat.getColor(context, android.R.color.black))
        if (width == 0 || height == 0) {
            return
        }
        val rectF = RectF(0F, 0F, width.toFloat(), height.toFloat())
        val color =
            if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            } else {
                ContextCompat.getColor(context, android.R.color.holo_orange_light)
            }
        mPaint.color = color
        val radius = width.coerceAtMost(height) * 0.4F
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), radius, mPaint)

        val layer = canvas.saveLayer(
            RectF(0F, 0F, width.toFloat(), height.toFloat()),
            null,
            Canvas.ALL_SAVE_FLAG
        )
        mPaint.isDither = true
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)

        val rectOval = when {
            phase > 150 -> {
                RectF(
                    rectF.centerX() - radius * (phase - 150) / 150,
                    rectF.centerY() - radius,
                    rectF.centerX() + radius * (phase - 150) / 150,
                    rectF.centerY() + radius
                )
            }
            phase < 150 -> {
                RectF(
                    rectF.centerX() - (radius - radius * phase / 150),
                    rectF.centerY() - radius,
                    rectF.centerX() + (radius - radius * phase / 150),
                    rectF.centerY() + radius
                )
            }
            else -> null
        }

        val rectFCircle = RectF(
            rectF.centerX() - radius,
            rectF.centerY() - radius,
            rectF.centerX() + radius,
            rectF.centerY() + radius
        )
        mPaint.color = ContextCompat.getColor(context, android.R.color.black)
        when {
            phase == 150 -> {
                rectOval?.let { canvas.drawOval(rectOval, mPaint) }
                canvas.drawArc(rectFCircle, 90F, 180F, false, mPaint)
            }
            phase == 0 -> {
            }
            phase < 150 -> {
                canvas.drawArc(rectFCircle, 90F, 180F, false, mPaint)
                mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                if (rectOval != null) {
                    canvas.drawOval(rectOval, mPaint)
                }
            }
            else -> {
                if (rectOval != null) {
                    canvas.drawOval(rectOval, mPaint)
                }
                canvas.drawArc(rectFCircle, 90F, 180F, false, mPaint)
            }
        }
        mPaint.xfermode = null
        canvas.restoreToCount(layer)
        canvas.rotate(rotate.toFloat())
    }
}