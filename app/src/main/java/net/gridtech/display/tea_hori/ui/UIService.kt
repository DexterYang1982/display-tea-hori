package net.gridtech.display.tea_hori.ui

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Binder
import android.os.IBinder
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder
import net.gridtech.display.core.R

class UIService : Service(), ServiceConnection {
    private var coreServiceBinder: CoreServiceBinder? = null
    private val binder: Binder = object : Binder(), UIServiceBinder {
        override var orientation = 0
        override fun openView(viewName: String) {
            val vn =
                if (orientation == 1)
                    "${viewName}V"
                else
                    viewName
            val activityIntent = Intent(vn)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(activityIntent)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        bindService(
            Intent(this, CoreService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        coreServiceBinder = service as CoreServiceBinder
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        coreServiceBinder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (coreServiceBinder != null) {
            unbindService(this)
        }
    }
}

interface UIServiceBinder {
    var orientation: Int
    fun openView(viewName: String)
}
