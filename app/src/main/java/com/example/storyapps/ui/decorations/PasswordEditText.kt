package com.example.storyapps.ui.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapps.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var showButtonImage: Drawable
    private lateinit var hideButtonImage: Drawable

    private var isVisibilityButtonClicked = false

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

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun init() {
        hideButtonImage =
            ContextCompat.getDrawable(context, R.drawable.baseline_remove_red_eye_24) as Drawable
        showButtonImage =
            ContextCompat.getDrawable(context, R.drawable.baseline_remove_red_eye_24) as Drawable
        setOnTouchListener(this)

        transformationMethod = PasswordTransformationMethod.getInstance()

        val paddingInDp = 10

        val scale = resources.displayMetrics.density
        val paddingInPx = (paddingInDp * scale + 0.5f).toInt()

        compoundDrawablePadding = paddingInPx

        showTextButton()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) {
                    error = "Password must be more than 8 characters"
                    Handler().postDelayed({ error = null }, 1500)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        )
    }

    private fun showTextButton() {
        if (isVisibilityButtonClicked) {
            setButtonDrawables(showButtonImage)
        } else {
            setButtonDrawables(hideButtonImage)
        }
    }

    private fun setButtonDrawables(drawableButton: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawableButton, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Enter Your Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {

            var isShowButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                val clearButtonStart =
                    (width - paddingEnd - showButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isShowButtonClicked = true
                }
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                    }

                    MotionEvent.ACTION_UP -> {
                        if (isShowButtonClicked) {
                            if (isVisibilityButtonClicked) {
                                showTextButton()
                                transformationMethod = HideReturnsTransformationMethod.getInstance()
                            } else {
                                showTextButton()
                                transformationMethod = PasswordTransformationMethod.getInstance()
                            }
                            isVisibilityButtonClicked = !isVisibilityButtonClicked
                        } else return false
                    }
                }
            }
        }
        return false
    }
}
