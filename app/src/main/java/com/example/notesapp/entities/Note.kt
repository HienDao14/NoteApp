package com.example.notesapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesapp.R

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String = "",
    @ColumnInfo(name = "content")
    var content: String = "",
    @ColumnInfo(name = "date_time")
    var dateTime: String = "",
    @ColumnInfo(name = "img_path")
    var imgPath : String = "",
    @ColumnInfo(name = "web_link")
    var webLink: String = "",
    @ColumnInfo(name = "color")
    var color: Int = R.color.black,
    @ColumnInfo(name = "isPinned")
    var isPinned : Int = 0,
    @ColumnInfo(name = "isDeleted")
    var isDeleted: Int = 0
)