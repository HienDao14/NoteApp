package com.example.notesapp.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.entities.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val RQ_SPECCH_REC = 1
    private val notes : ArrayList<Note> = ArrayList()
    private lateinit var adapter : NoteAdapter
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
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1, "Add Note", false, "")
            findNavController().navigate(action)
        }
        adapter = NoteAdapter{
            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(it.id, "Update Note", false, "")
            findNavController().navigate(action)
        }

        binding.rvNotes.adapter = adapter
        viewModel.unpinnedNotes.observe(viewLifecycleOwner){ notes ->
            notes.let {
                adapter.submitList(it)
            }
        }

        viewModel.pinnedNotes.observe(viewLifecycleOwner){notes ->
            if(notes.isNotEmpty()){
                binding.pinnedLayout.visibility = View.VISIBLE
                val pinnedAdapter = NoteAdapter{
                    val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(it.id, "Update Note", false, "")
                    findNavController().navigate(action)
                }
                binding.rvPinnedNotes.adapter = pinnedAdapter
                notes.let{
                    pinnedAdapter.submitList(it)
                }
            } else {
                binding.pinnedLayout.visibility = View.GONE
            }
        }
        onBottomItemsClicked()
        onToolBarClicked()
    }

    private fun onToolBarClicked() {
        binding.topAppBar.setNavigationOnClickListener {

        }
        binding.topAppBar.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)
        }
    }

    private fun onBottomItemsClicked() {
        binding.bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.bottom_image_picker -> {
                    onImagePickerClicked()
                    true
                }
                R.id.bottom_voice_record -> {
                    askSpeechInput()
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RQ_SPECCH_REC && resultCode == Activity.RESULT_OK){
            val result: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }

            val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1, "Add Note", false,result!!)
            findNavController().navigate(action)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun askSpeechInput() {
        if(ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), RQ_SPECCH_REC
            )
        } else{
            speechToText()
        }
    }

    private fun speechToText(){
        if(!SpeechRecognizer.isRecognitionAvailable(requireContext())){
            Toast.makeText(requireContext(), "Speech recognition", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Thử nói gì đó!")
            startActivityForResult(intent, RQ_SPECCH_REC)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == RQ_SPECCH_REC){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                speechToText()
            }
        }
    }

    private fun onImagePickerClicked() {
        val action = HomeFragmentDirections.actionHomeFragmentToNoteFragment(-1, "Add Note", true, "")
        findNavController().navigate(action)
    }
}