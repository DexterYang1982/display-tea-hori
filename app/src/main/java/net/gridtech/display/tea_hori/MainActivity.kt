package net.gridtech.display.tea_hori

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder
import net.gridtech.display.tea_hori.ui.UiManager


class MainActivity : AppCompatActivity(), ServiceConnection {
    var binder: CoreServiceBinder? = null
    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)


        businessView.isLongClickable = true
        businessView.setOnLongClickListener {
            println("====")
            UiManager.longClickSubject.onNext(it)
            true
        }



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
        binder = service as CoreServiceBinder
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }
}
