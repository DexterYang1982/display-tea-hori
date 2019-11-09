package net.gridtech.display.core

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CoreService : Service() {
    private var binder: CoreServiceBinder? = null
    override fun onCreate() {
        super.onCreate()
        binder = CoreServiceBinder(this)
    }
    override fun onBind(intent: Intent): IBinder {
        return binder!!
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
