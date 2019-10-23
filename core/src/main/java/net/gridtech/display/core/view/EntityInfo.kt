package net.gridtech.display.core.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import net.gridtech.display.core.R
import kotlinx.android.synthetic.main.info_entity.*

class EntityInfo(context: Context) : LinearLayout(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.info_entity, this)
    }
}