package net.gridtech.display.core.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import net.gridtech.display.core.CoreService
import net.gridtech.display.core.CoreServiceBinder

abstract class BaseView : AppCompatActivity(), ServiceConnection {
    protected var binder: CoreServiceBinder? = null
    protected val disposables = ArrayList<Disposable>()
    protected val handler = Handler()
    private val tripleClickSubject = PublishSubject.create<Long>().apply {
        this.buffer(3)
            .filter {
                (it.last() - it.first()) < 1000L
            }
            .subscribe {
                onTripleClick()
            }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setLayout()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        if (shouldStartService()) {
            startService(Intent(this, CoreService::class.java))
        }
        if (shouldBindService()) {
            bindService(
                Intent(this, CoreService::class.java),
                this,
                Context.BIND_AUTO_CREATE
            )
        }


        initLogic()
    }

    override fun dispatchTouchEvent(me: MotionEvent): Boolean {
        if (me.action == MotionEvent.ACTION_DOWN) {
            tripleClickSubject.onNext(System.currentTimeMillis())
        }
        return super.dispatchTouchEvent(me)
    }


    open fun setLayout() {

    }

    abstract fun shouldStartService(): Boolean
    abstract fun shouldBindService(): Boolean

    open fun initLogic() {

    }

    open fun onTripleClick() {
        startActivity(Intent("main"))
    }

    open fun onServiceBind(binder: CoreServiceBinder) {

    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val b = service as CoreServiceBinder
        binder = b
        onServiceBind(b)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        binder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binder != null) {
            unbindService(this)
        }
    }
}