package com.example.levi.demos.ui.nested

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.EdgeEffect
import android.widget.LinearLayout
import android.widget.OverScroller
import android.widget.TextView
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

class NestedLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent {
    private var tv: TextView? = null
    private var indicator: PageIndicator? = null
    private var pager: ViewPager? = null
    private var mTopHeight = 0f
    private val mConfig = ViewConfiguration.get(context)
    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()
    private val mTouchSlop = mConfig.scaledTouchSlop
    private val mMinVelocity = mConfig.scaledMinimumFlingVelocity
    private val mMayVelocity = mConfig.scaledMaximumFlingVelocity
    private val mScroller = OverScroller(context)
    private val mEdge = EdgeEffect(context)
    private val OVER_SCROLL = 30
    private var mDownY = 0f
    private val mParentHelper = NestedScrollingParentHelper(this)
    @get:JvmName("getState")
    var mState = State.Close
        private set
    @set:JvmName("setListener")
    var mListener: StateChangeListener? = null

    enum class State {
        Open, Close, Drag
    }

    interface StateChangeListener {
        fun onStateChange(state: State)
    }

    init {
        orientation = VERTICAL
    }

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//
//
//        ev?.let {
//
//            mVelocityTracker.addMovement(it)
//            when (it.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    mDownY = it.y
//                    println("onTouchEvent ACTION_DOWN")
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val deltaY = (mDownY - it.y)
//
//                    setState(State.Drag, deltaY.toInt())
//
//                    if (isHidden() && deltaY > 0 && canScrollDown()) {
//                        // 手动分发事件
//                        println("dispatchTouchEvent ACTION_MOVE")
//                        pager?.dispatchTouchEvent(ev)
//                    } else {
//                        touchEventToChild = false
//                    }
//
//                    mDownY = it.y
//
//                    println("onTouchEvent ACTION_MOVE")
//                }
//                MotionEvent.ACTION_UP -> {
//                    release()
//                    println("onTouchEvent ACTION_UP")
//                }
//            }
//        }
//
//        return super.dispatchTouchEvent(ev)
//    }
//
//    /**
//     * 1.头部的view未隐藏，则拦截。向上滑动
//     * 2.头部的view隐藏 && 子view向下滑动到顶部，拦截
//     */
//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//
//        ev?.let {
//            when (it.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    if (!mScroller.isFinished) {
//                        mScroller.abortAnimation()
//                    }
//                    mDownY = it.y
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val deltaY = (it.y - mDownY)
//                    if (Math.abs(deltaY) > mTouchSlop
//                        && (!isHidden()
//                                || isHidden() && deltaY > 0 && canScroll())
//                    ) {
//                        parent.requestDisallowInterceptTouchEvent(true)
//                        println("onInterceptTouchEvent")
//                        return true
//                    }
//
//                    println("onInterceptTouchEvent ACTION_MOVE")
//                }
//                MotionEvent.ACTION_UP -> {
//                }
//            }
//        }
//        return false
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let {

            mVelocityTracker.addMovement(it)
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownY = it.y
                    println("onTouchEvent ACTION_DOWN")
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaY = (mDownY - it.y)

                    setState(State.Drag, deltaY.toInt())

                    mDownY = it.y

                    println("onTouchEvent ACTION_MOVE")
                }
                MotionEvent.ACTION_UP -> {
                    release()
                    println("onTouchEvent ACTION_UP")
                }
            }
        }
        return true
    }

    private fun release() {
        // 只要不是拖动状态，则抬起时，不释放原位置
        if (mState != State.Drag) return
        mVelocityTracker.computeCurrentVelocity(1000, mMayVelocity.toFloat())
        val yV = mVelocityTracker.yVelocity.toInt()
        println("swipe yV: $yV，min：$mMinVelocity，may:$mMayVelocity")
        // 速度达标，则fling，否则弹性滑动
        if (Math.abs(yV) in mMinVelocity..mMayVelocity) {
            releaseFling(yV)
        } else {
            releaseScroll()
        }
        mVelocityTracker.clear()

        if (!mEdge.isFinished) {
            mEdge.onRelease()
        }
    }

    private fun releaseScroll() {
        if (scrollY >= mTopHeight / 2) {
            setState(State.Open)
        } else {
            setState(State.Close)
        }
    }

    private fun releaseFling(yVelocity: Int) {
        fling(-yVelocity)
    }

    private fun scrollByHand(y: Int) {
        val yy = scrollY + y
        when {
            yy >= mTopHeight -> scrollTo(0, mTopHeight.toInt())
            yy <= 0 -> {
                scrollTo(0, 0)
                mEdge.onPull(y.toFloat())
                if (!mEdge.isFinished) {
                    postInvalidate()
                }
            }
            else -> scrollTo(0, yy)
        }
    }

    private fun fling(vY: Int) {
        val maxY = if (vY > 0) {
            mTopHeight.toInt()
        } else {
            0
        }
        mScroller.fling(0, scrollY, 0, vY, 0, 0, 0, maxY)
        postInvalidate()
    }

    private fun smoothScroll(y: Int) {
        mScroller.startScroll(0, scrollY, 0, y)
        postInvalidate()
    }

    private fun springBack(y: Int) {
        mScroller.springBack(0, scrollY, 0, 0, 0, mTopHeight.toInt())
        postInvalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.currY)
            mEdge.onAbsorb(mScroller.currVelocity.toInt())
            postInvalidate()
        }
    }

    /**
     * 根据状态来更新item
     */
    private fun setState(s: State, y: Int = 0, fling: Boolean = false) {
        // 如果已经是此状态，则返回。不重复设置，拖动除外
        if (mState == s && s != State.Drag) return
        // 根据状态滑动
        when (s) {
            State.Close -> smoothScroll(-scrollY)
            State.Open -> smoothScroll(mTopHeight.toInt() - scrollY)
            State.Drag -> scrollByHand(y)
        }
        // 更新状态
        mState = s
        // 监听
        mListener?.onStateChange(s)
    }

    private fun isHidden() = scrollY >= mTopHeight

    private fun canScroll(): Boolean {
        var scroll = false

        pager?.let {
            //            post {
            val fp = it.adapter!! as FragmentPagerAdapter
            val view = fp.getItem(it.currentItem).view!!
            println("view ${view.javaClass.canonicalName}")
            if (view is RecyclerView) {
                scroll = !view.canScrollVertically(-1)
                println("view up ${view.canScrollVertically(1)}")
                println("view down ${view.canScrollVertically(-1)}")
            }
        }

        return scroll
    }

    private fun canScrollDown(): Boolean {
        var scroll = false

        pager?.let {
            //            post {
            val fp = it.adapter!! as FragmentPagerAdapter
            val view = fp.getItem(it.currentItem).view!!
            println("view ${view.javaClass.canonicalName}")
            if (view is RecyclerView) {
                scroll = !view.canScrollVertically(1)
                println("view up ${view.canScrollVertically(1)}")
                println("view down ${view.canScrollVertically(-1)}")
            }
        }

        return scroll
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val v = getChildAt(childCount - 1)
        v?.let {
            if (it is ViewPager) {
                it.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(measuredHeight - indicator!!.measuredHeight - 30, MeasureSpec.EXACTLY)
                )
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tv = getChildAt(0) as TextView?
        indicator = getChildAt(1) as PageIndicator?
        pager = getChildAt(2) as ViewPager?
        tv?.post {
            mTopHeight = tv?.height!!.toFloat()
            println("top height $mTopHeight")
        }
    }

    /**
     * 嵌套滑动，子类 RecyclerView，本身支持NestedScroll
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        println("onStartNestedScroll")
        return axes.and(SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        println("onNestedScrollAccepted")
        mParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    /**
     * @param consumed 不为空，表示父view消耗了(dy)一部分或者全部消耗，dispatch中返回true。
     * target 在touch事件处理时，会从consumed数组中取出来父view消耗的，然后消耗剩余的值。
     * 当consumed都为空时，不消耗事件，则也可以重写此方法和子view一起滑动
     */
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        println("onNestedPreScroll ${target.javaClass.simpleName} ${target.canScrollVertically(-1)}")
        // 下滑 && 上滑
        if (dy > 0 && !isHidden() || dy < 0 && scrollY > 0 && !target.canScrollVertically(-1)) {
            // 不断消耗，父view拦截。
            setState(State.Drag, dy)
            consumed[1] = dy
        }
    }

    override fun onStopNestedScroll(target: View) {
        println("onStopNestedScroll")
        mParentHelper.onStopNestedScroll(target)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        println("onNestedScroll")
    }

    /**
     * @param velocityY 手指负数向上滑动，正向下滑动
     */
    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        println("onNestedPreFling  $velocityY")
        if (velocityY.toInt() > 0 && !isHidden()
            || velocityY.toInt() < 0 && scrollY > 0 && !target.canScrollVertically(-1)
        ) {
            fling(velocityY.toInt())
            return true
        }
        // true 表示消耗fling事件，不会再去发送dispatchFling，因此，此onNestedFling收不到事件
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        println("onNestedFling ${velocityY}")
        return false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!mEdge.isFinished) {
            mEdge.setSize(width, height)
            if (mEdge.draw(canvas)) {
                postInvalidate()
            }
        }
    }

}
