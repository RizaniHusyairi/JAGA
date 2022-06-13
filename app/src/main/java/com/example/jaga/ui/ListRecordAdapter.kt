package com.example.jaga.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jaga.Record
import com.example.jaga.databinding.ItemRecordBinding
import com.example.jaga.databinding.ItemRowContactBinding


class ListRecordAdapter(private val listRecord: ArrayList<Record>) : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>()  {
    class ViewHolder(var binding: ItemRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListRecordAdapter.ViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListRecordAdapter.ViewHolder, position: Int) {
        val dataRecord = listRecord[position]
        holder.binding.nameRecord.text = dataRecord.name
    }

    override fun getItemCount(): Int = listRecord.size

}