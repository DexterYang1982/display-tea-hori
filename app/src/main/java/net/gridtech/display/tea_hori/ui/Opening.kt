package net.gridtech.display.tea_hori.ui

import android.media.MediaPlayer
import android.net.Uri
import kotlinx.android.synthetic.main.activity_opening.*
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R

class Opening : BaseView() {
    override fun shouldStartService(): Boolean = false
    override fun shouldBindService(): Boolean = true
    override fun setLayout() {
        setContentView(R.layout.activity_opening)
    }

    override fun initLogic() {
        super.initLogic()
        val uri=Uri.parse("android.resource://${packageName}/${R.raw.opening}")
        val player=MediaPlayer()
        player.setDataSource(baseContext,uri)
        player.setOnPreparedListener {
            player.setDisplay(surfaceView.holder)
            player.start()
            println("===================")
        }
        player.prepareAsync()
    }
}