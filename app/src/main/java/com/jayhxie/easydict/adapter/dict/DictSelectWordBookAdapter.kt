package com.jayhxie.easydict.adapter.dict

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jayhxie.easydict.R

@SuppressLint("NotifyDataSetChanged")
class DictSelectWordBookAdapter(private val bookNameList: List<String>) : RecyclerView.Adapter<DictSelectWordBookAdapter.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dict_select_word_book, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bookNameList[position]
        holder.textBookName.text = item
        holder.textBookName.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return bookNameList.size
    }

    interface  OnItemClickListener {
         fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
         onItemClickListener = listener
    }

    fun getBookName(position: Int) = bookNameList[position]

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textBookName: TextView = itemView.findViewById(R.id.tv_book_name)
    }
}