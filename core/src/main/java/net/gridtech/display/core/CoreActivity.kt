package net.gridtech.display.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_core.*
import net.gridtech.core.Bootstrap
import net.gridtech.core.util.hostInfoPublisher

class CoreActivity : AppCompatActivity(),ServiceConnection {
    var binder: CoreServiceBinder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)
        bindService(
            Intent(this, CoreService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )

        updateHostInfo.setOnClickListener {
            hostInfoPublisher.onNext(
                Bootstrap.HostInfoStub(
                    nodeId = nodeId.text.toString(),
                    nodeSecret = nodeSecret.text.toString(),
                    parentEntryPoint = parentAccess.text.toString()
                )
            )
        }
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        binder = service as CoreServiceBinder
        showHostInfo()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    fun showHostInfo() {
        nodeId.setText(binder?.hostInfo?.nodeId)
        nodeSecret.setText(binder?.hostInfo?.nodeSecret)
        parentAccess.setText(binder?.hostInfo?.parentEntryPoint)
    }
}
