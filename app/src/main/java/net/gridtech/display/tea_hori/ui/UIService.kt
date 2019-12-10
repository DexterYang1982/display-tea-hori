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
    var orientation: Int? = null
    private var coreServiceBinder: CoreServiceBinder? = null
    override fun onBind(intent: Intent): IBinder {
        return Binder()
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
        orientation = intent?.getIntExtra("orientation", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        intent?.getStringExtra("viewName")?.apply {
            val viewName =
                if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
                    "${this}V"
                else
                    this
            val activityIntent=Intent(viewName)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(activityIntent)
        }
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
