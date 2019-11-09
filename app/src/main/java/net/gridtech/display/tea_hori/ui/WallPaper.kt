package net.gridtech.display.tea_hori.ui

import android.content.Intent
import kotlinx.android.synthetic.main.activity_wall_paper.*
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R

class WallPaper : BaseView() {
    override fun shouldStartService(): Boolean = false
    override fun shouldBindService(): Boolean = true
    override fun setLayout() {
        setContentView(R.layout.activity_wall_paper)
    }

    override fun initLogic() {
        imageView.setImageResource(R.drawable.wallpaper_1)
    }
}