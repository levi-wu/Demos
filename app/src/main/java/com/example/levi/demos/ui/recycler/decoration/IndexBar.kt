package com.example.levi.demos.ui.recycler.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.example.levi.demos.R

class IndexBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var textSize: Int
    private var color: Int
    private var pressColor: Int = Color.argb(125, 0, 0, 0)

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.IndexBar, defStyleAttr, 0)
        color = attr.getColor(R.styleable.IndexBar_bg, Color.TRANSPARENT)
        val defaultSize = 16.toSp()
        textSize = attr.getDimensionPixelSize(R.styleable.IndexBar_textSize, defaultSize.toInt())
        attr.recycle()
    }

    private val mPaint = Paint().apply {
        flags = Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        textSize = this@IndexBar.textSize.toFloat()
    }

    private val fontMetrics: Paint.FontMetrics = mPaint.fontMetrics

    private val temp = Rect()

    var mWidth = 0
    var mHeight = 0
    var gapSize = 0
    var press = false
    var listener: IndexBarListener? = null

    private var mData = arrayOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "#"
    )


    public fun setData(data: Array<String>) {
        this.mData = data
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val withSpec = MeasureSpec.getMode(widthMeasureSpec)
        val withMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpec = MeasureSpec.getMode(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var width = 0
        var height = 0

        // 遍历
        mData.forEach {
            mPaint.getTextBounds(it, 0, it.length, temp)
            width = Math.max(temp.width(), width)
            height = Math.max(temp.height(), height)
        }

        if (withMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            width = if (width > withSpec) width else withSpec
            height *= mData.size
            height = if (height > heightSpec) height else heightSpec
        } else {
            width = withSpec
            height = heightSpec
        }

        setMeasuredDimension(width + paddingLeft + paddingRight, height + paddingRight + paddingLeft)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        gapSize = (mHeight - paddingTop - paddingBottom) / mData.size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            it.drawColor(color)

            mData.forEachIndexed { index, s ->
                mPaint.getTextBounds(s, 0, s.length, temp)
                val x = (width.toFloat() - temp.width()) / 2
                val baseline =
                    paddingTop + (index * gapSize + (index + 1) * gapSize - fontMetrics.top - fontMetrics.bottom) / 2
                it.drawText(s, x, baseline, mPaint)
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.x
            val y = it.y

            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    setPressedBG(true)
                    listener?.onDown()

                    println("转换：${"HHH"[0].isLetter()}")
                    println("转换：${"123"[0].isLetter()}")
                    println("转换：${"##"[0].isLetter()}")
                    println("转换：${"$%"[0].isLetter()}")
                }
                MotionEvent.ACTION_MOVE -> {
                    mData.forEachIndexed { index, s ->
                        temp.set(0, paddingTop + index * gapSize, width, paddingTop + (index + 1) * gapSize)
                        if (temp.contains(x.toInt(), y.toInt())) {
                            println("点击了：$s")
//                            val c = Pinyin.toPinyin(s[0])
//                            println("转换：$c")
                            listener?.choose(index, s)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    setPressedBG(false)
                    listener?.onUp()
                }

                else -> {
                }
            }

        }
        press = true
        return true
    }

    private fun setPressedBG(press: Boolean) {
        color = if (press) pressColor else Color.TRANSPARENT
        invalidate()
    }

    private fun Number.toSp() =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), resources.displayMetrics)

    interface IndexBarListener {
        fun onDown()
        fun onUp()
        fun choose(index: Int, text: String)
    }
}
