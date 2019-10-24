package net.gridtech.display.core.view

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.info_field_value.view.*
import net.gridtech.display.core.R
import net.gridtech.machine.model.EntityFieldValue
import net.gridtech.machine.model.IEntityField
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FieldValueInfo(h: Handler, field: IEntityField<*>, fieldValue: EntityFieldValue<*>, context: Context) :
    LinearLayout(context) {
    private val disposables = ArrayList<Disposable>()

    init {
        LayoutInflater.from(context).inflate(R.layout.info_field_value, this)
        disposables.add(Observable.merge(field.name.observable, field.alias.observable).subscribe {
            h.post {
                val a = "${field.name.value} ( ${field.alias.value} )"
                fieldNameLabel.text = a
            }
        })

        fieldValue.observable.subscribe {
            h.post {
                fieldValueLabel.text = it?.toString()
                val timeAndSession =
                    "${SimpleDateFormat("MM-dd HH:mm:ss").format(Date(fieldValue.updateTime))}  ${fieldValue.session}"
                updateTimeLabel.text = timeAndSession
            }

        }.apply {
            disposables.add(this)
        }
    }


    fun onRemove() {
        disposables.forEach { it.dispose() }
    }
}