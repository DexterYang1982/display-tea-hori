package net.gridtech.display.tea_hori

import android.content.Intent
import android.content.pm.ActivityInfo
import kotlinx.android.synthetic.main.activity_main.*
import net.gridtech.display.core.view.BaseView


class MainActivity : BaseView() {
    override fun shouldStartService(): Boolean = true
    override fun shouldBindService(): Boolean = false
    override fun setLayout() {
        setContentView(R.layout.activity_main)
    }
    override fun onTripleClick() {
    }

    override fun initLogic() {
        rotateBtn.setOnClickListener {
            requestedOrientation = when (requestedOrientation) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        coreBtn.setOnClickListener {
            startActivity(Intent("core"))
        }
        wallPaperBtn.setOnClickListener {
            startActivity(Intent("wallPaper"))
        }
    }

}
