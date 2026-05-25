package com.hotel.hotelbooking.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/** Adds system-bar bottom inset as extra padding (e.g. BottomNavigationView). */
fun View.applySystemBarBottomPadding(extraPx: Int = 0) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
        v.updatePadding(bottom = bottom + extraPx)
        insets
    }
}

/** Adds system-bar bottom inset on top of a static base margin (e.g. FABs). */
fun View.applySystemBarBottomMargin(baseMarginPx: Int) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val systemBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
        (v.layoutParams as? ViewGroup.MarginLayoutParams)?.let { lp ->
            lp.bottomMargin = baseMarginPx + systemBottom
            v.layoutParams = lp
        }
        insets
    }
}

fun RecyclerView.attachFabScrollBehavior(fab: ExtendedFloatingActionButton) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            when {
                dy > 8 -> fab.shrink()
                dy < -8 -> fab.extend()
            }
        }
    })
}
