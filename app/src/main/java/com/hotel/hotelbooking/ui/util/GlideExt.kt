package com.hotel.hotelbooking.ui.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hotel.hotelbooking.R
import java.io.File

fun ImageView.loadImage(
    path: String?,
    @DrawableRes placeholder: Int = R.drawable.ic_default_image
) {
    val source: Any = if (!path.isNullOrBlank() && File(path).exists()) File(path) else placeholder
    Glide.with(this)
        .load(source)
        .placeholder(placeholder)
        .error(placeholder)
        .transition(DrawableTransitionOptions.withCrossFade())
        .centerCrop()
        .into(this)
}
