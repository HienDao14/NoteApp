package com.example.notesapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentSearchBinding
import com.example.notesapp.entities.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory
import com.google.android.material.shape.MaterialShapeDrawable


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var notes: ArrayList<Note> = ArrayList()
    private lateinit var adapter : NoteAdapter
    private val viewModel : NoteViewModel by activityViewModels{
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NoteAdapter {
            val action = SearchFragmentDirections.actionSearchFragmentToNoteFragment(it.id, "Update Note", false, "")
            findNavController().navigate(action)
        }
        viewModel.allNotes.observe(viewLifecycleOwner){ listNote ->
            listNote.let {
                notes = it as ArrayList<Note>
            }
        }
        binding.appBarLayout.setStatusBarForegroundColor(
            resources.getColor(R.color.dark_gray)
        )
        binding.rvSearchingNote.adapter = adapter
        binding.topAppBar.setNavigationOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        onSearchTextChange()
    }

    private fun onSearchTextChange() {
        binding.noteSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var tempNotes = ArrayList<Note>()
                if(newText == ""){
                    adapter.submitList(tempNotes)
                    adapter.notifyDataSetChanged()
                    return true
                }
                for (note in notes){
                    if (note.title.lowercase().contains(newText?.lowercase().toString())
                        || note.content.lowercase().contains(newText?.lowercase().toString())){
                        tempNotes.add(note)
                    }
                }
                adapter.submitList(tempNotes)
                adapter.notifyDataSetChanged()
                return true
            }
        })
    }
}