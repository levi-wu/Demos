package com.example.levi.demos.ui.nested

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.levi.demos.R
import com.example.levi.demos.ui.recycler.decoration.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_nested.*


class NestedFragment : Fragment() {

    private val data = mutableListOf<String>()

    init {
        for (i in 0..50) {
            data.add("第 $i 条数据")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nested, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = object :BaseRecyclerAdapter<String>(data){
                override fun getLayoutId(): Int {
                    return R.layout.item_recycler
                }

                override fun onBindViewHolder(holder: VH, position: Int) {
                    holder.setText(R.id.item_tv, data[position])
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NestedFragment().apply {
                //                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
            }
    }
}
