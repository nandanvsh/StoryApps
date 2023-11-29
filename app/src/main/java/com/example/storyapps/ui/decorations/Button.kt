package com.example.storyapps.ui.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapps.R

class Button : AppCompatButton {

    private lateinit var enableBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private lateinit var progressDrawable: Drawable
    private var txtColor: Int = 0
    private var isLoading:Boolean = false

    constructor(context: Context) : super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enableBackground else disabledBackground

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = if (isEnabled) text else if (isLoading) "Loading" else text

        if (isLoading){
            setCompoundDrawables(progressDrawable, null, null , null)
            (progressDrawable as? Animatable)?.start()
        } else {
            setCompoundDrawables(null, null, null, null)
            (progressDrawable as? Animatable)?.stop()
        }
    }
    fun setLoading(value: Boolean){
        Log.d("TAG_VALUE", value.toString())
        isLoading = value
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        progressDrawable = ProgressBar(context).indeterminateDrawable.apply {
            gravity = Gravity.CENTER
            this.setBounds(0, 0, intrinsicHeight / 2, intrinsicHeight / 2)
            setTint(txtColor)
        }

        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }

    override fun onDetachedFromWindow() {
        (progressDrawable as? Animatable)?.stop()
        super.onDetachedFromWindow()
    }
}