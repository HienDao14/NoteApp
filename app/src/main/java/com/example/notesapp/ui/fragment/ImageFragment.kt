package com.example.notesapp.ui.fragment

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.adapter.ImageAdapter
import com.example.notesapp.database.NoteApplication
import com.example.notesapp.databinding.FragmentImageBinding
import com.example.notesapp.viewModel.NoteViewModel
import com.example.notesapp.viewModel.NoteViewModelFactory


class ImageFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).databse.noteDao()
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topAppBar.overflowIcon?.setColorFilter(resources.getColor(R.color.text), PorterDuff.Mode.DST_IN)
        val adapter = ImageAdapter{
        }
        viewModel.allImages.observe(viewLifecycleOwner){list ->
            list.let {
                adapter.images = it
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.rvImages.adapter = adapter
    }
}