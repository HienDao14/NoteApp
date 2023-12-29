package com.example.notesapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1, "Add Note")
            findNavController().navigate(action)
        }
        val adapter = NoteAdapter{
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(it.id, "Update Note")
            findNavController().navigate(action)
        }

        binding.rvNotes.adapter = adapter
        viewModel.allNotes.observe(viewLifecycleOwner){notes ->
            notes.let {
                adapter.submitList(it)
            }
        }
    }
}