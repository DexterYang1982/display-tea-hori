package net.gridtech.display.tea_hori.ui

import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_opening.*
import net.gridtech.display.tea_hori.R

class Info : UIView() {
    val player = MediaPlayer()
    override fun setLayout() {
        setContentView(R.layout.activity_info)
    }

    override fun initLogic() {
        super.initLogic()
        player.setOnPreparedListener {
            player.start()
        }
        surfaceView.holder.addCallback(
            object : SurfaceHolder.Callback {
                override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                }

                override fun surfaceCreated(p0: SurfaceHolder?) {
                    player.setDataSource(baseContext, Uri.parse("android.resource://${packageName}/${R.raw.info}"))
                    player.prepareAsync()
                    player.setDisplay(surfaceView.holder)
                }

                override fun surfaceDestroyed(p0: SurfaceHolder?) {
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }
}