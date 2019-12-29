package net.gridtech.display.tea_hori.ui

import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_opening.*
import net.gridtech.core.util.currentTime
import net.gridtech.display.core.CoreServiceBinder
import net.gridtech.display.tea_hori.R
import net.gridtech.machine.model.entity.Display
import net.gridtech.machine.model.entityField.CustomField
import java.util.concurrent.TimeUnit

class ShitActivity : UIView() {
    private val urlInfo
        get() = Uri.parse("android.resource://${packageName}/${R.raw.info}")
    private val urlClosing
        get() = Uri.parse("android.resource://${packageName}/${R.raw.closing}")
    private val urlOpening
        get() = Uri.parse("android.resource://${packageName}/${R.raw.opening}")
    private var player: MediaPlayer? = null
    private val disposables = ArrayList<Disposable>()
    override fun setLayout() {
        setContentView(R.layout.activity_shit)
    }


    private fun showInfo() {
        handler.post {

            player?.release()
            player = MediaPlayer().apply {
                setOnPreparedListener {
                    start()
                }
                setDataSource(baseContext, urlInfo)
                prepareAsync()
                setDisplay(surfaceView.holder)
            }
            val l = surfaceView.layoutParams
            l.width = 1440
            l.height = 900
            surfaceView.layoutParams = l
        }
    }

    private fun showOpening() {
        handler.post {

            player?.release()
            player = MediaPlayer().apply {
                setOnPreparedListener {
                    start()
                }
                setDataSource(baseContext, urlOpening)
                prepareAsync()
                setDisplay(surfaceView.holder)
            }
            val l = surfaceView.layoutParams
            l.width = 1920
            l.height = 1080
            surfaceView.layoutParams = l
        }
    }


    private fun showClosing() {
        handler.post {
            player?.release()
            player = MediaPlayer().apply {
                setOnPreparedListener {
                    start()
                }
                setDataSource(baseContext, urlClosing)
                prepareAsync()
                setDisplay(surfaceView.holder)
            }
            Observable.timer(uiServiceBinder!!.closingTimeout, TimeUnit.MILLISECONDS).subscribe {
                showInfo()
            }

            val l = surfaceView.layoutParams
            l.width = 1920
            l.height = 1080
            surfaceView.layoutParams = l
        }
    }

    override fun initLogic() {
        super.initLogic()
        surfaceView.holder.addCallback(
            object : SurfaceHolder.Callback {
                override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                }

                override fun surfaceCreated(p0: SurfaceHolder?) {
                    showInfo()
                }

                override fun surfaceDestroyed(p0: SurfaceHolder?) {
                }
            }
        )
    }

    override fun onServiceBind() {
        super.onServiceBind()
        uiServiceBinder?.getCoreServiceBinder()?.apply {
            disposables.add(trigger(this, "openEntityId", "openFieldId", "openValueId") {
                showOpening()
            })
            disposables.add(trigger(this, "closeEntityId", "closeFieldId", "closeValueId") {
                showClosing()
            })
        }
    }

    private fun trigger(
        coreServiceBinder: CoreServiceBinder,
        nodeIdAlias: String,
        fieldIdAlias: String,
        valueIdAlias: String,
        triggeredFunction: () -> Unit
    ) =
        coreServiceBinder.bootstrap.connectionObservable().filter { it }
            .switchMap {
                coreServiceBinder.dataHolder.getEntityByIdObservable<Display>(coreServiceBinder.hostInfo!!.nodeId)
            }.switchMap { display ->
                val nodeIdV = coreServiceBinder.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == nodeIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                val fieldIdV = coreServiceBinder.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == fieldIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                val valueV = coreServiceBinder.dataHolder.getEntityFieldByConditionObservable { f ->
                    f.alias.value == valueIdAlias && f.nodeClassId() == display.nodeClassId()
                }.switchMap {
                    (it as CustomField).getFieldValue(display).observable
                }.map { it.name }
                Observable.combineLatest(listOf(nodeIdV, fieldIdV, valueV)) {
                    Triple(it[0], it[1], it[2])
                }
            }.switchMap {
                coreServiceBinder.dataHolder.getEntityFieldByConditionObservable { entityField ->
                    entityField is CustomField && entityField.id == it.second
                }.switchMap { entityField ->
                    val fieldValue = (entityField as CustomField).getFieldValue(it.first as String)
                    fieldValue.observable
                        .filter { currentTime() - fieldValue.updateTime < 10000L }
                }.filter { valueDescription ->
                    valueDescription.id == it.third
                }
            }.subscribe {
                triggeredFunction()
            }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach { it.dispose() }
    }
}