package net.gridtech.display.tea_hori

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder

class MainActivity : AppCompatActivity(),ServiceConnection  {
    var binder: CoreServiceBinder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        System.err.println("++++++++++++++++")
        bindService(
            Intent(this, CoreService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )

        coreView.setOnClickListener {
            startActivity(Intent("net.gridtech.display.core.CoreActivity"))
        }
    }
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        System.err.println("========================")
        binder = service as CoreServiceBinder
    }
    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }
}
