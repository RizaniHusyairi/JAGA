package com.example.jaga.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jaga.Contact
import com.example.jaga.R
import com.example.jaga.databinding.ItemRowContactBinding

class ListContactAdapter(private val listContact: ArrayList<Contact>) : RecyclerView.Adapter<ListContactAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemRowContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =ItemRowContactBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = listContact[position]
        holder.binding.apply {
            tvName.text = contact.nama
            tvNumber.text = contact.nomor
            btnLonceng.setOnClickListener {

            }

        }
    }



    override fun getItemCount(): Int {
        return listContact.size
    }

}