package net.gridtech.display.tea_hori.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.disposables.Disposable
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.tea_hori.R

open class UIView : BaseView(), ServiceConnection {
    protected var uiServiceBinder: UIServiceBinder? = null
    private var disposable: Disposable? = null
    open fun onServiceBind() {}
    override fun initLogic() {
        bindService(
            Intent(this, UIService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        uiServiceBinder = service as UIServiceBinder
        disposable = uiServiceBinder!!.startActivityPublisher.subscribe { viewName ->
            handler.post {
                val activityIntent = Intent(viewName)
                startActivity(activityIntent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
        onServiceBind()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        uiServiceBinder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (uiServiceBinder != null) {
            disposable?.dispose()
            unbindService(this)
        }
    }
}