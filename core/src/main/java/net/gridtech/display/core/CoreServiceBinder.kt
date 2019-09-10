package net.gridtech.display.core

import android.app.Activity
import android.content.Context
import android.os.Binder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import net.gridtech.display.core.dao.FieldDao
import net.gridtech.display.core.dao.FieldValueDao
import net.gridtech.display.core.dao.NodeClassDao
import net.gridtech.display.core.dao.NodeDao
import net.gridtech.core.Bootstrap
import net.gridtech.core.data.IHostInfo
import net.gridtech.core.util.hostInfoPublisher
import net.gridtech.core.util.parse
import net.gridtech.core.util.stringfy
import net.gridtech.machine.model.DataHolder


class CoreServiceBinder(private val coreService: CoreService) : Binder() {
    var viewContext: Context? = null
    val bootstrap: Bootstrap
    val dataHolder: DataHolder

    private val preference = coreService.getSharedPreferences("host_info", Activity.MODE_PRIVATE)
    var hostInfo: IHostInfo? = preference.getString("hostInfo", null)?.let {
        parse<Bootstrap.HostInfoStub>(it)
    }

    init {
        hostInfoPublisher.subscribe { hostInfo ->
            if (this.hostInfo != hostInfo) {
                this.hostInfo = hostInfo
                preference.edit().putString("hostInfo", stringfy(hostInfo)).apply()
            }
        }
        bootstrap = Bootstrap(
            enableCache = false,
            nodeClassDao = NodeClassDao(),
            fieldDao = FieldDao(),
            nodeDao = NodeDao(),
            fieldValueDao = FieldValueDao()
        )
        bootstrap.startHostInfoChangServer(54321)
        dataHolder = DataHolder(bootstrap, null)
        hostInfo?.apply {
            hostInfoPublisher.onNext(this)
        }
    }

}