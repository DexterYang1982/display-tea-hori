package net.gridtech.display.tea_hori.ui

import kotlinx.android.synthetic.main.activity_wall_paper.*
import net.gridtech.display.tea_hori.R

class WallPaper : UIView() {
    override fun setLayout() {
        setContentView(R.layout.activity_wall_paper)
    }

    override fun initLogic() {
        super.initLogic()
        imageView.setOnClickListener {
            uiServiceBinder!!.openView("shit")
        }
    }
}