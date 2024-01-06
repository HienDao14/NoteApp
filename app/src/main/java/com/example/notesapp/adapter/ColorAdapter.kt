package com.example.notesapp.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.NotePickerColorBinding


class ColorAdapter(private val onItemCLicked: (Int) -> Unit): RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    var colorList = emptyList<Int>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    inner class ColorViewHolder(val binding: NotePickerColorBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(color: Int){
            binding.notePickerColor.setBackgroundColor(binding.root.resources.getColor(color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(
            NotePickerColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val current = colorList[position]
        Log.d("ColorAdapter", current.toString())
        holder.bind(current)
        holder.binding.notePickerColor.setOnClickListener {
            onItemCLicked(current)
        }
    }

    override fun getItemCount(): Int {
        return colorList.size
    }
}