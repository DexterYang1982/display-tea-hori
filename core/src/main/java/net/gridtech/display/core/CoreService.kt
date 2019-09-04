package net.gridtech.display.core

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CoreService : Service() {
    private var binder: CoreServiceBinder? = null
    override fun onBind(intent: Intent): IBinder {
        System.err.println("AAAAAAAAAAAAAAA")
        if (binder == null) {
            binder = CoreServiceBinder(this)
        }
        return binder!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
