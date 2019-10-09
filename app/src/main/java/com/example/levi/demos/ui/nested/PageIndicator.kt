package com.example.levi.demos.ui.nested

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.viewpager.widget.ViewPager

const val BG_COLOR = Color.WHITE
const val TV_COLOR = Color.BLACK
const val INDICATOR_COLOR = Color.GREEN

class PageIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mWidth = 0
    private var mHeight = 0
    private var mItemWidth = 0
    private val LINE_HEIGHT = 15.0f
    private val ROUND = LINE_HEIGHT / 5
    private var mTranslation = 0.0f
    private var pager: ViewPager? = null
    private val rectF = RectF()

    init {
        orientation = HORIZONTAL

        setWillNotDraw(false)
        setBackgroundColor(BG_COLOR)

        paint.style = Paint.Style.FILL
        paint.color = INDICATOR_COLOR
    }

    fun setup(pager: ViewPager?) {
        pager?.let {
            this.pager = it
            val count = it.adapter?.count!!
            for (i in 0 until count) {
                val tv = TextView(context)
                val lp = LayoutParams(0, LayoutParams.WRAP_CONTENT)
                val t = it.adapter?.getPageTitle(i)
                lp.weight = 1f
                tv.apply {
                    text = t
                    textSize = 16f
                    setTextColor(TV_COLOR)
                    gravity = Gravity.CENTER
                    setPadding(24)

                    setOnClickListener { _ ->
                        it.setCurrentItem(i, true)
                    }
                }
                // 添加
                addView(tv, lp)
            }

            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    if (positionOffset != 0f) {
                        mTranslation = positionOffset * mItemWidth + position * mItemWidth
                        postInvalidate()
                    }
                }

                override fun onPageSelected(position: Int) {
                }
            })
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        pager?.removeOnPageChangeListener()
        pager = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mItemWidth = getChildAt(0).measuredWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {

            rectF.set(
                mTranslation,
                mHeight - LINE_HEIGHT,
                mItemWidth + mTranslation,
                mHeight.toFloat()
            )

            it.drawRoundRect(
                rectF, ROUND,
                ROUND,
                paint
            )
        }
    }
}