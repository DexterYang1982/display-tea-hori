package net.gridtech.display.core

import android.content.Context
import android.graphics.Color
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_core.*
import net.gridtech.core.Bootstrap
import net.gridtech.core.util.hostInfoPublisher
import net.gridtech.display.core.view.BaseView
import net.gridtech.display.core.view.EntityInfo


class CoreActivity : BaseView() {
    override fun shouldStartService(): Boolean = false
    override fun shouldBindService(): Boolean = true
    override fun setLayout() {
        setContentView(R.layout.activity_core)
    }

    override fun initLogic() {
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

    override fun onServiceBind(binder: CoreServiceBinder) {
        showHostInfo()
        bindConnection()
        showEntityInfo()
    }

    private fun showHostInfo() {
        nodeId.setText(coreServiceBinder?.hostInfo?.nodeId)
        nodeSecret.setText(coreServiceBinder?.hostInfo?.nodeSecret)
        parentAccess.setText(coreServiceBinder?.hostInfo?.parentEntryPoint)
    }

    private fun bindConnection() {
        coreServiceBinder?.bootstrap?.connectionObservable()?.subscribe {
            handler.post {
                if (it) {
                    connectionIcon.setColorFilter(Color.GREEN)
                } else {
                    connectionIcon.setColorFilter(Color.RED)
                }
            }
        }?.apply {
            disposables.add(this)
        }
    }

    private fun showEntityInfo() {
        coreServiceBinder?.dataHolder?.getEntityByConditionObservable { entity -> entity.parentId() == null }
            ?.subscribe { entity ->
                val entityInfo = EntityInfo(handler, coreServiceBinder?.dataHolder!!, entity, this.baseContext)
                handler.post {
                    entityInfoContainer.addView(entityInfo)
                }
                entity.onDelete().subscribe { _, _ ->
                    entityInfo.onRemove()
                    entityInfoContainer.removeView(entityInfo)
                }
            }?.apply {
                disposables.add(this)
            }
    }

    override fun dispatchTouchEvent(me: MotionEvent): Boolean {
        if (me.action == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            val v = currentFocus      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v!!.windowToken)   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me)
    }

    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            //得到输入框在屏幕中上下左右的位置
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略
        return false
    }

    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
