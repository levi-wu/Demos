package com.example.levi.demos.ui.recycler.decoration

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val HEIGHT = 100
const val SIZE = 300

class TitleDecoration : RecyclerView.ItemDecoration() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50.0f
        style = Paint.Style.FILL_AND_STROKE
    }

    private val temp = Rect()
    private val temp1 = RectF()
    private val temp2 = Rect()

    private val metrics: Paint.FontMetrics = mPaint.fontMetrics

    var text = ""

    /**
     * 绘制于RecyclerView之上
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (parent.childCount == 0) return

        val top = 0f
        val bottom = top + HEIGHT

        val data = (parent.adapter as BaseRecyclerAdapter<*>).data
        val left = parent.paddingLeft.toFloat()
        val right = parent.width - parent.paddingRight.toFloat()

        val layoutManager = parent.layoutManager as LinearLayoutManager
        val visiblePosition = layoutManager.findFirstVisibleItemPosition()
        val visibleView = layoutManager.findViewByPosition(visiblePosition)
        val bottomEdge = layoutManager.getDecoratedBottom(visibleView!!)
        val bean = data[visiblePosition] as Bean
        val bean1 = data[visiblePosition + 1] as Bean

        c.save()
        if (bean1.type != bean.type && bottomEdge <= HEIGHT) {
            c.translate(0f, bottomEdge.toFloat() - HEIGHT)
        }
        drawTitle(c, bean.type, left, top, right, bottom)
        c.restore()


        drawSelectedText(parent, c)
    }

    /**
     * 绘制选中后的文本
     */
    private fun drawSelectedText(parent: RecyclerView, c: Canvas) {
        if (text.isNotEmpty()) {
            mPaint.color = Color.GRAY
            val cLeft = parent.right - parent.width / 2 - SIZE / 2
            val cRight = cLeft + SIZE
            val cTop = parent.bottom - parent.height / 2 - SIZE / 2
            val cBottom = cTop + SIZE
            temp.set(cLeft, cTop, cRight, cBottom)
            c.drawRect(temp, mPaint)

            mPaint.color = Color.WHITE
            mPaint.getTextBounds(text, 0, text.length, temp2)
            val textX = temp.right - temp.width() / 2.0f - temp2.width() / 2.0f
            val baseline = (temp.top + temp.bottom - metrics.top - metrics.bottom) / 2.0f
            c.drawText(text, textX, baseline, mPaint)
        }
    }

    /**
     * 内部绘制
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, temp)
            val position = parent.getChildLayoutPosition(child)
            mPaint.color = Color.GRAY
            val top = temp.top + Math.round(child.translationY).toFloat()
            val bottom = top + HEIGHT

            val data = (parent.adapter as BaseRecyclerAdapter<*>).data
            val left = parent.paddingLeft.toFloat()
            val right = parent.width - parent.paddingRight.toFloat()

            if (position == 0) {
                val bean = data[position] as Bean
                drawTitle(c, bean.type, left, top, right, bottom)
            } else {
                val first = data[position - 1] as Bean
                val last = data[position] as Bean
                if (first.type != last.type) {
                    drawTitle(c, last.type, left, top, right, bottom)
                }
            }

        }
    }

    /**
     * 绘制item的标题
     */
    private fun drawTitle(c: Canvas, title: String, left: Float, top: Float, right: Float, bottom: Float) {
        temp1.set(left, top, right, bottom)
        mPaint.color = Color.GRAY
        c.drawRect(temp1, mPaint)

        mPaint.color = Color.WHITE
        val fontMetrics = mPaint.fontMetrics
        val baseLine = top + bottom - fontMetrics.top - fontMetrics.bottom
        c.drawText(title, (right - temp1.width()) / 2 + 20, baseLine / 2, mPaint)
    }

    /**
     * 偏移量，用于Recycler的测量和绘制
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val data = (parent.adapter as BaseRecyclerAdapter<*>).data

        if (position == 0) {
            outRect.set(
                0,
                HEIGHT,
                0,
                0
            )
        } else {
            val first = data[position - 1] as Bean
            val last = data[position] as Bean
            if (first.type != last.type) {
                outRect.set(
                    0,
                    HEIGHT,
                    0,
                    0
                )
            } else {
                outRect.set(0, 0, 0, 0)
            }
        }
    }

}