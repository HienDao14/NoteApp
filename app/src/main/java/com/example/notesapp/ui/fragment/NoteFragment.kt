package com.example.notesapp.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.notesapp.adapter.ColorAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentNoteBinding
import com.example.notesapp.entities.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private val navArgs: NoteFragmentArgs by navArgs()
    private var color: Int = R.color.black
    private var title : String = ""
    private var currentDate: String = ""
    private var imagePath : String = ""
    private var isPinned: Int = 0
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
        if(uri != null){
            Glide.with(requireContext()).load(uri).into(binding.noteImage)
            imagePath = uri.toString()
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    lateinit var note: Note
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navArgs.id
        title = navArgs.title
        val isAddingImage = navArgs.isAddingImage
        if(isAddingImage) {
            pickImage()
        }
        val speechText = navArgs.speechtotext
        binding.noteDetail.setText(speechText)
        Log.d("NoteFragment", speechText)
        binding.bottomAppBar.overflowIcon?.setTint(resources.getColor(R.color.white))
        binding.detailTopAppBar.setTitle(title)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        currentDate = sdf.format(Date())
        binding.tvDateTime.text = currentDate
        if(id != -1){
            bindItem(id)
        }
        topBarMenuItemClick()
        bottomBarMenuItemClick()
        setOnLinkCLicked()
    }

    private fun setOnLinkCLicked() {
        binding.noteDetail.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindItem(id: Int) {
        viewModel.retrieveItem(id).observe(viewLifecycleOwner){note ->
            this.note = note
            bindNoteScreen()
        }
    }

    private fun bindNoteScreen() {
        binding.apply {
            noteTitle.setText(note.title, TextView.BufferType.SPANNABLE)
            noteDetail.setText(note.content, TextView.BufferType.SPANNABLE)
            noteTime.setText(note.dateTime)
            color = note.color
            isPinned = note.isPinned
            binding.root.setBackgroundColor(resources.getColor(note.color))
            binding.bottomAppBar.backgroundTintList = ColorStateList.valueOf(resources.getColor(note.color))
            binding.detailAppBarLayout.setBackgroundColor(resources.getColor(note.color))
            if(note.imgPath != ""){
                imagePath = note.imgPath
                binding.rvImages.visibility = View.VISIBLE
                Glide.with(requireContext()).load(note.imgPath).into(binding.noteImage)
            }
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
                R.id.detail_pin -> {
                    if(isPinned == 0){
                        isPinned = 1
                        Snackbar.make(requireView(), R.string.pinned_success_noti, Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        isPinned = 0
                        Snackbar.make(requireView(), R.string.unpinned_success_noti, Snackbar.LENGTH_SHORT)
                            .show()
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
                binding.noteDetail.text.toString(),
                currentDate,
                color,
                imagePath,
                isPinned
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
                R.id.detail_change_color -> {
                    showColorPicker()
                    true
                }
                R.id.detail_add_image -> {
                    pickImage()
                    true
                }
                else -> false
            }
        }
    }

    private fun pickImage() {
        binding.rvImages.visibility = View.VISIBLE
        pickMedia.launch(
            PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
        )
    }

    private fun showColorPicker() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.color_picker_layout)
        val rv = bottomSheetDialog.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_color)

        val adapter = ColorAdapter {
            color = it
            binding.root.setBackgroundColor(resources.getColor(it))
            binding.bottomAppBar.backgroundTintList = ColorStateList.valueOf(resources.getColor(it))
            binding.detailAppBarLayout.setBackgroundColor(resources.getColor(it))

        }
        val list = ArrayList<Int>()
        list.addAll(listColor)
        adapter.colorList = list
        rv?.adapter = adapter
        bottomSheetDialog.show()
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
                binding.noteDetail.text.toString(),
                currentDate,
                color,
                imagePath,
                isPinned
            )
            findNavController().navigate(R.id.action_noteFragment_to_homeFragment)
        }
    }

    private fun isEntryValid() : Boolean{
        if(binding.rvImages.visibility == View.VISIBLE){
            return true
        }
        return viewModel.isEntryValid(
            binding.noteTitle.text.toString(),
            binding.noteDetail.text.toString(),
        )
    }


    companion object{
        val listColor = listOf(
            R.color.black,
            R.color.note_red,
            R.color.note_dark_brown,
            R.color.note_brown,
            R.color.dark_green,
            R.color.note_green,
            R.color.note_blue_green,
            R.color.note_blue,
            R.color.note_dark_blue,
            R.color.note_purple,
            R.color.note_pink,
            R.color.note_yellow_gray,
            R.color.note_gray
        )
    }
}