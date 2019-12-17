package net.gridtech.display.tea_hori.ui

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_opening.*
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class Closing : UIView() {
    override fun setLayout() {
        setContentView(R.layout.activity_closing)
    }

    override fun initLogic() {
        super.initLogic()
        val uri=Uri.parse("android.resource://${packageName}/${R.raw.closing}")
        val player=MediaPlayer()
        player.setDataSource(baseContext,uri)
        player.setOnPreparedListener {
            player.setDisplay(surfaceView.holder)
            player.start()
        }
        player.prepareAsync()
    }

    override fun onServiceBind(){
        println("======================${uiServiceBinder!!.closingTimeout}")
        Observable.timer(uiServiceBinder!!.closingTimeout,TimeUnit.MILLISECONDS).subscribe {
            uiServiceBinder!!.openView("info")
        }
    }
}