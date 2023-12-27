package com.example.notesapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import androidx.navigation.fragment.navArgs
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentNoteBinding
import com.example.notesapp.entities.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private val navArgs: NoteFragmentArgs by navArgs()
    private var title : String = ""
    lateinit var note: Note
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        topBarMenuItemClick()
        bottomBarMenuItemClick()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navArgs.id
        title = navArgs.title
        binding.detailTopAppBar.setTitle(title)
        if(id != -1){
            bindItem(id)
        }
    }

    private fun bindItem(id: Int) {
        viewModel.retrieveItem(id).observe(viewLifecycleOwner){note ->
            this.note = note
            bindTextView()
        }
    }

    private fun bindTextView() {
        binding.apply {
            noteTitle.setText(note.title, TextView.BufferType.SPANNABLE)
            noteDetail.setText(note.content, TextView.BufferType.SPANNABLE)
        }
    }

    private fun topBarMenuItemClick(){
        binding.detailTopAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_homeFragment)
        }

        binding.detailTopAppBar.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.detail_done -> {
                    if(title == "Add Note"){
                        addNewItem()
                    } else {
                        updateItem()
                    }

                    true
                }
                else -> false
            }
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                note.id,
                binding.noteTitle.text.toString(),
                binding.noteDetail.text.toString()
            )
        }
        findNavController().navigate(R.id.action_noteFragment_to_homeFragment)
    }

    private fun bottomBarMenuItemClick(){
        binding.bottomAppBar.setOnMenuItemClickListener {item ->
            when(item.itemId){
                R.id.detail_delete -> {
                    if(isEntryValid()){
                        showConfirmationDialog()
                    } else
                        Snackbar.make(requireView(), R.string.snackbar_none, Snackbar.LENGTH_SHORT)
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteItem(item: Note) {

        viewModel.deleteItem(item)
        val action = NoteFragmentDirections.actionNoteFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                Snackbar.make(requireView(), R.string.snackbar_done, Snackbar.LENGTH_SHORT)
                    .show()
                deleteItem(note)
            }
            .show()
    }

    private fun addNewItem() {
        if(isEntryValid()){
            viewModel.addNewItem(
                binding.noteTitle.text.toString(),
                binding.noteDetail.text.toString()
            )
            findNavController().navigate(R.id.action_noteFragment_to_homeFragment)
        }
    }

    private fun isEntryValid() : Boolean{
        return viewModel.isEntryValid(
            binding.noteTitle.text.toString(),
            binding.noteDetail.text.toString()
        )
    }
}