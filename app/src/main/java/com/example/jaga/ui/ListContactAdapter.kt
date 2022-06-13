package com.example.jaga.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jaga.Contact
import com.example.jaga.databinding.ItemRowContactBinding
import kotlin.collections.ArrayList


class ListContactAdapter(
    private val listContact: ArrayList<Contact>,
) :
    RecyclerView.Adapter<ListContactAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback


    class ViewHolder(var binding: ItemRowContactBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return ViewHolder(binding)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = listContact[position]

        holder.binding.apply {
            tvName.text = contact.nama
            tvNumber.text = contact.nomor
            btnDelete.setOnClickListener {
                onItemClickCallback.onItemClicked(contact)
            }
        }
    }


    override fun getItemCount(): Int {
        return listContact.size
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Contact)
        fun onUpdateClicked(data: Contact)
    }


}