package com.example.william.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.william.Dao.Dao;
import com.example.william.entities.Notes;

@Database(entities = {Notes.class},version = 1,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase db;
    public static synchronized NoteDatabase getDatabase(Context c){
        if(db == null)
            db = Room.databaseBuilder(c,NoteDatabase.class,"note_database").build();
        return db;
    }
    public abstract Dao noteDB();
}
