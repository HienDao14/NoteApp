package com.example.notesapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.NoteItemBinding
import com.example.notesapp.entities.Note

class NoteAdapter(private val onItemClicked: (Note) -> Unit): ListAdapter<Note, NoteAdapter.NoteViewHolder>(DiffCallback) {
    class NoteViewHolder(private val binding: NoteItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(note: Note){
            if(note.title == ""){
                binding.tvTitle.visibility = View.GONE
            } else binding.tvTitle.text = note.title
            if(note.content == ""){
                binding.tvContent.visibility = View.GONE
            } else binding.tvContent.text = note.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(note)
        }
        holder.bindItem(note)
    }

    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<Note>(){
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }
}