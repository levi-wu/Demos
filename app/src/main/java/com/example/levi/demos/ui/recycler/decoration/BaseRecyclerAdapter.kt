package com.example.levi.demos.ui.recycler.decoration

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.levi.demos.R

abstract class BaseRecyclerAdapter<T>(val data: List<T>) : RecyclerView.Adapter<BaseRecyclerAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        println("R onCreateViewHolder:")
        return VH(LayoutInflater.from(parent.context).inflate(getLayoutId(), parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    abstract fun getLayoutId(): Int

    @Suppress("UNCHECKED_CAST")
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cache = SparseArray<View>()

        private fun <T : View> getView(id: Int): T {
            var view = cache[id]
            if (view == null) {
                view = itemView.findViewById<T>(id)
                cache.put(id, view)
            }
            return view as T
        }

        fun setText(id: Int, text: String) {
            val tv = getView<TextView>(id)
            tv.text = text
        }
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        println("R onBindViewHolder: $position")
    }
}

