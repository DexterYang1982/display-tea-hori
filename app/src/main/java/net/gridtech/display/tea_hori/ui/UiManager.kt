package net.gridtech.display.tea_hori.ui

import android.view.View
import io.reactivex.subjects.PublishSubject

class UiManager {

    companion object {
        val longClickSubject = PublishSubject.create<View>().apply {
            this.map { it to System.currentTimeMillis() }
                .buffer(3)
                .filter {
                    (it.last().second - it.first().second) < 10000L
                }
                .map { it.last().first }
                .subscribe {
                    println("--------------")
                }
        }
    }
}