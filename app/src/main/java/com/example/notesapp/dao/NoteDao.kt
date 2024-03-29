package com.example.notesapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.notesapp.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getAllDeletedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isPinned = 0 AND isDeleted = 0")
    fun getAllUnpinnedNote() : Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id=:id ")
    fun getNoteById(id: Int): Flow<Note>

    @Query("SELECT * FROM notes WHERE isPinned = 1 AND isDeleted = 0")
    fun getPinnedNotes(): Flow<List<Note>>

    @Query("SELECT img_path FROM notes WHERE isDeleted = 0 AND img_path != :s ")
    fun getAllImages(s: String): Flow<List<String>>

    @Upsert
    suspend fun upsertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes  WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun clearAllDeletedNote()

}
