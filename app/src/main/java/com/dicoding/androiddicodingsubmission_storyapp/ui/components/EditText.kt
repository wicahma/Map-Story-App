package com.dicoding.androiddicodingsubmission_storyapp.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.androiddicodingsubmission_storyapp.R

class EditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var hidePassImage: Drawable
    private var isPassVisible = false


    init {
        setOnTouchListener(this)
        hidePassImage = ContextCompat.getDrawable(
            context, if (!isPassVisible) R.drawable.eye_visible else R.drawable.eye_not_visible
        ) as Drawable
        setCompoundDrawablesWithIntrinsicBounds(
            null, null, hidePassImage, null
        )
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val passwordButtonStart: Float
            val passwordButtonEnd: Float
            var isPasswordButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                passwordButtonEnd = (hidePassImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < passwordButtonEnd -> isPasswordButtonClicked = true
                }
            } else {
                passwordButtonStart = (width - paddingEnd - hidePassImage.intrinsicWidth).toFloat()
                when {
                    event.x > passwordButtonStart -> isPasswordButtonClicked = true
                }
            }
            if (isPasswordButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hidePassImage = ContextCompat.getDrawable(
                            context,
                            if (isPassVisible) R.drawable.eye_not_visible else R.drawable.eye_visible
                        ) as Drawable
                        setCompoundDrawablesWithIntrinsicBounds(
                            null, null, hidePassImage, null
                        )
                        transformationMethod =
                            if (isPassVisible) null else PasswordTransformationMethod.getInstance()
                        isPassVisible = !isPassVisible
                    }
                }
                return false
            }
            return false
        }
        return false
    }

}