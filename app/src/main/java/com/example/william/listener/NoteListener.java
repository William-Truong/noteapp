package com.example.william.listener;

import com.example.william.entities.Notes;
import com.example.william.entities.Reminders;

public interface NoteListener {
    void onNoteClicked(Notes temp,int position);
    void onNoteLongClicked(Notes temp,int position);
}
