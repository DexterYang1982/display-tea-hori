package net.gridtech.display.core.view

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.widget.LinearLayout
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.info_entity.view.*
import net.gridtech.core.Bootstrap
import net.gridtech.display.core.R
import net.gridtech.machine.model.DataHolder
import net.gridtech.machine.model.IEntity

class EntityInfo(h: Handler, dataHolder: DataHolder, entity: IEntity<*>, context: Context) : LinearLayout(context) {
    private val disposables = ArrayList<Disposable>()

    init {
        LayoutInflater.from(context).inflate(R.layout.info_entity, this)

        if (Bootstrap.hostInfo?.nodeId == entity.id) {
            nameAndId.setTextColor(Color.RED)
        }

        disposables.add(entity.name.observable.subscribe { name ->
            h.post {
                val a = "$name ( ${entity.id} )"
                nameAndId.text = a
            }
        })
        dataHolder.getEntityFieldByConditionObservable { entityField -> entityField.nodeClassId() == entity.nodeClassId() }
            .subscribe { entityField ->
                val fieldValueInfo = FieldValueInfo(h, entityField, entityField.getFieldValue(entity), context)
                h.post {
                    fieldContainer.addView(fieldValueInfo)
                }
                entityField.onDelete().subscribe { _, _ ->
                    fieldValueInfo.onRemove()
                    fieldContainer.removeView(fieldValueInfo)
                }

            }.apply {
                disposables.add(this)
            }
        dataHolder.getEntityByConditionObservable { childEntity -> childEntity.parentId() == entity.id && childEntity.id != entity.id }
            .subscribe { childEntity ->
                val entityInfo = EntityInfo(h, dataHolder, childEntity, context)
                h.post {
                    childContainer.addView(entityInfo)
                }
                childEntity.onDelete().subscribe { _, _ ->
                    entityInfo.onRemove()
                    childContainer.removeView(entityInfo)
                }
            }.apply {
                disposables.add(this)
            }
    }

    fun onRemove() {
        disposables.forEach { it.dispose() }
    }
}