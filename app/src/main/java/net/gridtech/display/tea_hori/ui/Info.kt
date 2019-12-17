package net.gridtech.display.tea_hori.ui

import android.media.MediaPlayer
import android.net.Uri
import kotlinx.android.synthetic.main.activity_opening.*
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R

class Info : UIView() {
    override fun setLayout() {
        setContentView(R.layout.activity_info)
    }
    override fun initLogic() {
        super.initLogic()
        val uri= Uri.parse("android.resource://${packageName}/${R.raw.info}")
        val player= MediaPlayer()
        player.setDataSource(baseContext,uri)
        player.setOnPreparedListener {
            player.setDisplay(surfaceView.holder)
            player.start()
        }
        player.prepareAsync()
    }
}