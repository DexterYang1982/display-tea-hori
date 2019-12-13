package net.gridtech.display.tea_hori.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import net.gridtech.display.core.view.BaseView

open class UIView : BaseView(), ServiceConnection {
    protected var uiServiceBinder: UIServiceBinder? = null
    open fun onServiceBind(){}
    override fun initLogic() {
        bindService(
            Intent(this, UIService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        uiServiceBinder = service as UIServiceBinder
        onServiceBind()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        uiServiceBinder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (uiServiceBinder != null) {
            unbindService(this)
        }
    }
}