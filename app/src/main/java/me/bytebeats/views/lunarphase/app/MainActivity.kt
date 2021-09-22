package me.bytebeats.views.lunarphase.app

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import me.bytebeats.views.lunarphase.LunarPhaseView

class MainActivity : AppCompatActivity() {

    private val lunarPhaseView by lazy { findViewById<LunarPhaseView>(R.id.lunar_phase_view) }
    private val btn by lazy { findViewById<Button>(R.id.start) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener { lunarPhaseView.start() }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        lunarPhaseView.rotate = if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            90
        } else {
            0
        }
    }

    override fun onResume() {
        super.onResume()
        lunarPhaseView.resume()
    }

    override fun onPause() {
        super.onPause()
        lunarPhaseView.pause()
    }
}