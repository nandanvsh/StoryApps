package com.example.storyapps.ui.decorations

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class UsernameEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }
    private fun init() {
        val paddingInDp = 10

        val scale = resources.displayMetrics.density
        val paddingInPx = (paddingInDp * scale + 0.5f).toInt()

        compoundDrawablePadding = paddingInPx

    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Enter Username"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}