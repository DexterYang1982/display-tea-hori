package net.gridtech.display.tea_hori

import android.content.Intent
import kotlinx.android.synthetic.main.activity_main.*
import net.gridtech.display.tea_hori.ui.UIView


class MainActivity : UIView() {
    override fun setLayout() {
        super.setLayout()
        setContentView(R.layout.activity_main)
    }

    override fun onTripleClick() {
    }

    override fun onServiceBind() {
        updateRotateLabel()
    }

    private fun updateRotateLabel() {
        rotateBtn.text = if (uiServiceBinder!!.orientation == 0)
            "Orientation : -"
        else
            "Orientation : |"
    }

    override fun initLogic() {
        super.initLogic()
        rotateBtn.setOnClickListener {
            uiServiceBinder!!.orientation = 1 - uiServiceBinder!!.orientation
            updateRotateLabel()
        }
        coreBtn.setOnClickListener {
            startActivity(Intent("core"))
        }
        wallPaperBtn.setOnClickListener {
            uiServiceBinder!!.openView("wallPaper")
        }

        openingViewBtn.setOnClickListener {
            uiServiceBinder!!.openView("opening")
        }

        infoViewBtn.setOnClickListener {
            uiServiceBinder!!.openView("info")
        }
        closingViewBtn.setOnClickListener {
            uiServiceBinder!!.openView("closing")
        }
    }

}
