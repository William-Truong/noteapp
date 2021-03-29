package com.example.william.Dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.william.entities.Notes;

import java.util.List;

@androidx.room.Dao
public interface Dao {

    @Query("SELECT * FROM NoteTable ORDER BY id DESC")
    List<Notes> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNote(Notes temp);

    @Delete
    void deleteNote(Notes temp);

}
