package com.example.william.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.william.activities.NoteFrament;
import com.example.william.activities.ReminderFrament;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NoteFrament();
            case 1:
                return new ReminderFrament();
            default:
                return new NoteFrament();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
