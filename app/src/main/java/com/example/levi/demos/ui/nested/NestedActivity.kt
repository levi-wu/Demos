package com.example.levi.demos.ui.nested

import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.levi.demos.R
import kotlinx.android.synthetic.main.activity_nested.*

private val TITLES = arrayOf("首页", "动态", "我的", "设置")
private val FRAG = arrayOf(
    NestedFragment.newInstance(),
    NestedFragment.newInstance(),
    NestedFragment.newInstance(),
    NestedFragment.newInstance()
)

class NestedActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_nested)

        pager.adapter = NestedPagerAdapter(supportFragmentManager)
//        pager.offscreenPageLimit = 2
        indicator.setup(pager)
    }

    class NestedPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = FRAG[position]

        override fun getCount(): Int = TITLES.size

        override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    println("NestedActivity onTouchEvent ACTION_DOWN")
                }
                MotionEvent.ACTION_MOVE -> {
                    println("NestedActivity onTouchEvent ACTION_MOVE")
                }
                MotionEvent.ACTION_UP -> {
                    println("NestedActivity onTouchEvent ACTION_UP")
                }
            }
        }
        return super.onTouchEvent(event)
    }
}
