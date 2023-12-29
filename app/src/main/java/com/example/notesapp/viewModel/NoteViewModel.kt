package com.example.notesapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.dao.NoteDao
import com.example.notesapp.entities.Note
import kotlinx.coroutines.launch

class NoteViewModel(private val dao: NoteDao): ViewModel() {
    val allNotes: LiveData<List<Note>> = dao.getAllNotes().asLiveData()
    private fun upsertNote(note: Note){
        viewModelScope.launch {
            dao.upsertNote(note)
        }
    }

    fun isEntryValid(title: String, detail: String): Boolean{
        if(title.isBlank() && detail.isBlank()){
            return false
        }
        return true
    }

    private fun getUpdatedItemEntry(id: Int, title: String, detail: String, dateTime: String, color: Int, imgPath: String): Note{
        return Note(id, title, detail, dateTime, color = color, imgPath = imgPath)
    }

    fun updateItem(id: Int, title: String, detail: String, dateTime: String, color: Int, imgPath: String){
        val updatedItem = getUpdatedItemEntry(id, title, detail, dateTime, color, imgPath)
        upsertNote(updatedItem)
    }

    fun addNewItem(title: String, detail:String, dateTime: String, color: Int, imgPath: String){
        val newItem = getNewEntryItem(title, detail, dateTime, color, imgPath)
        upsertNote(newItem)
    }

    private fun getNewEntryItem(title: String, detail: String, dateTime: String, color: Int, imgPath: String) : Note{
        return Note(title = title, content = detail, dateTime = dateTime, color = color, imgPath = imgPath)
    }

    fun deleteItem(note: Note){
        viewModelScope.launch {
            dao.deleteNote(note)
        }
    }

    fun retrieveItem(id: Int): LiveData<Note>{
        return dao.getNoteById(id).asLiveData()
    }

    fun deleteAllNote(){
        viewModelScope.launch {
            dao.deleteAllNotes()
        }
    }
}

class NoteViewModelFactory(private val dao: NoteDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteViewModel::class.java)){
            @Suppress("UNCHECK_CAST")
            return NoteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown")
    }
}