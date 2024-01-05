package com.example.notesapp.ui.fragment

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentDeletedBinding
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory
import com.google.android.material.snackbar.Snackbar

class DeletedFragment : Fragment() {
    private lateinit var binding: FragmentDeletedBinding
    private val viewModel : NoteViewModel by activityViewModels{
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeletedBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = NoteAdapter{
            val action = DeletedFragmentDirections.actionDeletedFragmentToNoteFragment(it.id, "Deleted Note", false, "")
            findNavController().navigate(action)
        }
        viewModel.allDeletedNotes.observe(viewLifecycleOwner){listNote ->
            listNote.let {
                adapter.submitList(it)
            }
        }
        //Set overflow icon tint
        binding.topAppBar.overflowIcon?.setColorFilter(resources.getColor(R.color.text), PorterDuff.Mode.DST_IN)
        binding.rvDeletedNote.adapter = adapter
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            viewModel.clearAllDeletedNotes()
            Snackbar.make(requireView(), "Clear all deleted notes", Snackbar.LENGTH_SHORT).show()
            true
        }
    }
}