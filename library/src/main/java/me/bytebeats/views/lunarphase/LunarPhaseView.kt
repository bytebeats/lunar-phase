package me.bytebeats.views.lunarphase

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

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

    private val mHandler by lazy { UIHandler(WeakReference(this)) }

    var rotate: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var originalPhase = 0
        set(value) {
            field = value
            invalidate()
        }
    private var phase: Int = 0

    var duration: Int = DEFAULT_DURATION_IN_MILLIS
    var interval: Int = DEFAULT_INTERVAL_IN_MILLIS
    private var passTime = 0L

    private var isStarted = false

    private val mPaint by lazy { Paint() }

    init {
        val a =
            context.obtainStyledAttributes(attributeSet, R.styleable.LunarPhaseView, defStyleRes, 0)
        rotate = a.getInteger(R.styleable.LunarPhaseView_rotate, 0)
        originalPhase = a.getInteger(R.styleable.LunarPhaseView_phase, 0)
        duration = a.getInteger(
            R.styleable.LunarPhaseView_durationInMillis,
            DEFAULT_DURATION_IN_MILLIS
        )
        interval = a.getInteger(
            R.styleable.LunarPhaseView_intervalInMillis,
            DEFAULT_INTERVAL_IN_MILLIS
        )

        a.recycle()
    }

    fun start() {
        reset()
        isStarted = true
        resume()
    }

    fun resume() {
        if (!isStarted) {
            return
        }
        mHandler.sendEmptyMessage(UPDATE_PHASE)
    }

    fun pause() {
        mHandler.removeMessages(UPDATE_PHASE)
    }

    fun reset() {
        pause()
        passTime = 0
        isStarted = false
    }

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
                ContextCompat.getColor(context, android.R.color.holo_orange_light)
            } else {
                ContextCompat.getColor(context, android.R.color.holo_orange_dark)
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

    private class UIHandler(private val reference: WeakReference<LunarPhaseView>) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                UPDATE_PHASE -> {
                    reference.get()?.apply {
                        phase =
                            originalPhase - (originalPhase * passTime / duration).toInt()
                        Log.i(TAG, "phase: $phase")
                        invalidate()
                        if (passTime in 0 until duration) {
                            passTime += interval
                            mHandler.sendEmptyMessage(UPDATE_PHASE)
                        }
                    }
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    companion object {
        private const val TAG = "LunarPhaseView"
        private const val UPDATE_PHASE = 0x10001
        private const val DEFAULT_DURATION_IN_MILLIS = 30000
        private const val DEFAULT_INTERVAL_IN_MILLIS = 100
    }
}