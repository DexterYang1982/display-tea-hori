package net.gridtech.display.tea_hori.ui

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import net.gridtech.core.util.currentTime
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder
import net.gridtech.machine.model.entity.Display
import net.gridtech.machine.model.entityField.CustomField

class UIService : Service(), ServiceConnection {
    private var coreServiceBinder: CoreServiceBinder? = null
    private val binder: Binder = object : Binder(), UIServiceBinder {
        override val startActivityPublisher = PublishSubject.create<String>()
        override var orientation = 0
        override var closingTimeout: Long = 3000L
        override fun getCoreServiceBinder(): CoreServiceBinder? = coreServiceBinder
        override fun openView(viewName: String) {
            val vn =
                if (orientation == 1)
                    "${viewName}V"
                else
                    viewName

            startActivityPublisher.onNext(vn)
        }
    }

    private fun listenToValueChange(csb: CoreServiceBinder) {
        closingTimeoutWatcher(csb)
    }

    @SuppressLint("CheckResult")
    private fun closingTimeoutWatcher(csb: CoreServiceBinder) {
        csb.bootstrap.connectionObservable().filter { it }
            .switchMap {
                csb.dataHolder.getEntityByIdObservable<Display>(csb.hostInfo!!.nodeId)
            }.switchMap { display ->
                csb.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == "closingTimeout" && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
            }.subscribe {
                (binder as UIServiceBinder).closingTimeout = try {
                    it.toLong()
                } catch (e: Throwable) {
                    3000L
                }
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
        listenToValueChange(coreServiceBinder!!)
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
    val startActivityPublisher: Observable<String>
    var closingTimeout: Long
    var orientation: Int
    fun openView(viewName: String)
    fun getCoreServiceBinder(): CoreServiceBinder?
}
