@file:JvmName("RecyclerViewExt")

package ch.usi.inf.mwc.cusi.utils

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeItemDecorationByClass(clazz: Class<out RecyclerView.ItemDecoration>) {
    for (i in 0 until itemDecorationCount) {
        val itemDecorator = getItemDecorationAt(i)
        if (itemDecorator::class.java != clazz) continue

        removeItemDecorationAt(i)
        break
    }
}
