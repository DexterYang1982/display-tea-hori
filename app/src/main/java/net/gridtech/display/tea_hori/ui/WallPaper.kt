package net.gridtech.display.tea_hori.ui

import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R

class WallPaper : BaseView() {
    override fun shouldStartService(): Boolean = false
    override fun shouldBindService(): Boolean = true
    override fun setLayout() {
        setContentView(R.layout.activity_wall_paper)
    }
}