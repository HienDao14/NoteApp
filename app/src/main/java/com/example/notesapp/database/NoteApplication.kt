package com.example.notesapp.database

import android.app.Application

class NoteApplication: Application() {
    val databse : NoteDatabase by lazy {
        NoteDatabase.getDatabase(this)
    }
}