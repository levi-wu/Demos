package com.example.levi.demos.ui.recycler.decoration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.levi.demos.R
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.android.synthetic.main.activity_recycler_index_bar.*

class RecyclerIndexBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_index_bar)
        initRecyclerView()
    }

    private val strList = arrayListOf(
        "布局xml",
        "15年是Android插件化",
        "技术突飞猛进",
        "的一年，随着",
        "业务的发展各",
        "大厂商都碰",
        "到了Android Native平台",
        "的瓶颈：",
        "从技术上讲",
        "，业务逻辑的复",
        "杂导致代码",
        "量急剧膨胀，各",
        "大厂商陆续",
        "出到65535方法",
        "数的天花板；",
        "同时，运",
        "营为王的时",
        "代对于模块热更新",
        "提出了更高的要求。",
        "在业务层面上",
        "功能模块",
        "的解耦以及维",
        "123",
        "@@ertwef",
        "^^^dfgdg",
        "!!!!",
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

    private fun initRecyclerView() {
        val list = mutableListOf<Bean>()

        val indexList = mutableSetOf<String>()
        strList.forEach {
            val c: Char = it.get(0)
            val pin = Pinyin.toPinyin(c)
            val type = pin[0]
            val text = if (type.isLetter()) type.toString() else "#"
            list.add(Bean(text, it))
            indexList.add(text)
        }

        val sortedList = list.sortedWith(Comparator { o1, o2 ->
            if (o1.type == o2.type) {
                o1.text.compareTo(o2.text)
            } else {
                o1.type.compareTo(o2.type)
            }
        })

        println("sortedList : ${sortedList.toString()}")

        val title = TitleDecoration()

        recycler.apply {
            layoutManager = LinearLayoutManager(this@RecyclerIndexBarActivity)

            // 系统提供默认分割线
            addItemDecoration(DividerItemDecoration(this@RecyclerIndexBarActivity, DividerItemDecoration.VERTICAL))
            // 自定义Title分割线,覆盖上述
            addItemDecoration(title)

            adapter = object : BaseRecyclerAdapter<Bean>(sortedList) {

                override fun onBindViewHolder(holder: VH, position: Int) {
                    println("R onBindViewHolder: $position")
                    holder.setText(R.id.item_tv, data[position].text)
                    holder.itemView.tag = data[position].type
                }

                override fun getLayoutId(): Int {
                    return R.layout.item_recycler
                }
            }
        }

        index_bar.apply {
            setData(indexList.toTypedArray().sortedArray())
            listener = object : IndexBar.IndexBarListener {
                override fun onDown() {
                }

                override fun onUp() {
                    title.text = ""
                    recycler.invalidateItemDecorations()
                }

                override fun choose(index: Int, text: String) {
                    val position = sortedList.indexOfFirst { it.type == text }
                    println("current type :$text and sortedList pos : $position")
                    title.text = text
                    recycler.invalidateItemDecorations()
//                    recycler.scrollToPosition(position)
                    val layout = recycler.layoutManager as LinearLayoutManager
                    layout.scrollToPositionWithOffset(position, 0)
//                    recycler.layoutManager?.apply {
//                        val smoothScroller = object : LinearSmoothScroller(this@RecyclerIndexBarActivity) {
//                            override fun getVerticalSnapPreference(): Int {
//                                return LinearSmoothScroller.SNAP_TO_START
//                            }
//                        }
//
//                        smoothScroller.targetPosition = position
//
//                        startSmoothScroll(smoothScroller)
//                    }
                }
            }
        }
    }
}
