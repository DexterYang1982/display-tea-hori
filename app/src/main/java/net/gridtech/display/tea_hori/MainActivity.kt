package net.gridtech.display.tea_hori

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.ui.UIService


class MainActivity : BaseView() {
    override fun shouldStartService(): Boolean = true
    override fun shouldBindService(): Boolean = false
    override fun setLayout() {
        setContentView(R.layout.activity_main)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent=Intent(this, UIService::class.java)
        intent.putExtra("orientation",requestedOrientation)
        startService(intent)
    }

    override fun onTripleClick() {
    }

    override fun initLogic() {
        rotateBtn.setOnClickListener {
            requestedOrientation = when (requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            val intent=Intent(this, UIService::class.java)
            intent.putExtra("orientation",requestedOrientation)
            startService(intent)
        }
        coreBtn.setOnClickListener {
            startActivity(Intent("core"))
        }
        wallPaperBtn.setOnClickListener {
            val intent=Intent(this, UIService::class.java)
            intent.putExtra("orientation",requestedOrientation)
            intent.putExtra("viewName","wallPaper")
            startService(intent)
        }
    }

}
