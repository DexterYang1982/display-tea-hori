package net.gridtech.display.tea_hori.ui

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import io.reactivex.Observable
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder
import net.gridtech.machine.model.entity.Display
import net.gridtech.machine.model.entityField.CustomField

class UIService : Service(), ServiceConnection {
    private var coreServiceBinder: CoreServiceBinder? = null
    private val handler = Handler()
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

    private fun listenToValueChange(csb: CoreServiceBinder) {
        trigger(csb, "openEntityId", "openFieldId", "openValueId") {
            handler.post {
                (binder as UIServiceBinder).openView("opening")
            }

        }
        trigger(csb, "closeEntityId", "closeFieldId", "closeValueId") {
            handler.post {
                (binder as UIServiceBinder).openView("closing")
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun trigger(
        csb: CoreServiceBinder,
        nodeIdAlias: String,
        fieldIdAlias: String,
        valueIdAlias: String,
        triggeredFunction: () -> Unit
    ) {
        csb.bootstrap.connectionObservable().filter { it }
            .switchMap {
                csb.dataHolder.getEntityByIdObservable<Display>(csb.hostInfo!!.nodeId)
            }.switchMap { display ->
                val nodeIdV = csb.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == nodeIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                val fieldIdV = csb.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == fieldIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                val valueV = csb.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == valueIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                Observable.combineLatest(listOf(nodeIdV, fieldIdV, valueV)) {
                    Triple(it[0], it[1], it[2])
                }
            }.switchMap {
                csb.dataHolder.getEntityFieldByConditionObservable { entityField ->
                    entityField is CustomField && entityField.id == it.second
                }.switchMap { entityField ->
                    (entityField as CustomField).getFieldValue(it.first as String).observable
                }.filter { valueDescription ->
                    valueDescription.id == it.third
                }
            }.subscribe {
                triggeredFunction()
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
    var orientation: Int
    fun openView(viewName: String)
}
