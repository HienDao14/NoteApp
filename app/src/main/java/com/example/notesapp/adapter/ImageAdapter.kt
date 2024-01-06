package com.example.notesapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notesapp.databinding.ImageItemBinding

class ImageAdapter(val onClickListener: (String) -> Unit ): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var images = emptyList<String>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    inner class ImageViewHolder(val binding: ImageItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(imgPath : String){
            Glide.with(binding.root.context).load(imgPath.toUri()).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ImageItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val curernt = images[position]
        holder.itemView.setOnClickListener {
            onClickListener(curernt)
        }
        holder.bindItem(curernt)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}