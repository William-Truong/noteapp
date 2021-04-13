package com.example.william.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.william.R;
import com.example.william.adapter.NoteAdapter;
import com.example.william.database.NoteDatabase;
import com.example.william.entities.Notes;
import com.example.william.entities.Reminders;
import com.example.william.listener.NoteListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.william.R.drawable.ic_stack;

public class NoteFrament extends Fragment implements NoteListener {
    private ImageView btnAddNote,imgSetLayoutNote;
    private RecyclerView NoteRecyclerView;
    private NoteAdapter noteAdapter;
    List<Notes> notesList;

    private static final int REQUESR_CODE_ADD = 1;
    private static final int REQUESR_CODE_UPDATE = 3;
    private static final int REQUESR_CODE_SHOW = 2;
    private int noteClickPosition = -1;

    //search note
    EditText edtSearch;

    //refresh note
    SwipeRefreshLayout refreshLayoutNote;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_notes,container,false);

        //Nút tạo ghi chú
        btnAddNote = v.findViewById(R.id.btnAddNote);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(),AddNoteScreen.class),REQUESR_CODE_ADD);
            }
        });

        imgSetLayoutNote = v.findViewById(R.id.imgSetLayoutNote);
        NoteRecyclerView = v.findViewById(R.id.lvNoteData);

        //changed layout note start
        imgSetLayoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    NoteRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    imgSetLayoutNote.setImageResource(R.drawable.ic_gird);

            }
        });
        imgSetLayoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                imgSetLayoutNote.setImageResource(ic_stack);
                return true;
            }
        });
        NoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //change LayoutNote end

        notesList = new ArrayList<>();
        noteAdapter = new NoteAdapter(notesList,this);
        NoteRecyclerView.setAdapter(noteAdapter);

        getNote(REQUESR_CODE_SHOW,false);


        //search note
        edtSearch = v.findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(notesList.size() != 0)
                    noteAdapter.searchNote(s.toString());
            }
        });

        //refresh note
        refreshLayoutNote = v.findViewById(R.id.swipeRefreshNote);
        refreshLayoutNote.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                noteAdapter.notifyDataSetChanged();
                refreshLayoutNote.setRefreshing(false);
            }
        });

        return v;
    }

    public void getNote(final int request,final boolean isNoteDeleted){

        class GetNoteTask extends AsyncTask<Void, Void, List<Notes>>  {

            @Override
            protected List<Notes> doInBackground(Void... voids) {
                return NoteDatabase.getDatabase(getActivity()).noteDB().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Notes> notes) {
                super.onPostExecute(notes);
                if(request == REQUESR_CODE_SHOW){ //hiển thị danh sách
                    notesList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else if(request == REQUESR_CODE_ADD){ //thêm note
                    notesList.add(0,notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    NoteRecyclerView.smoothScrollToPosition(0);
                }else if(request == REQUESR_CODE_UPDATE){ //sửa và xóa note
                    notesList.remove(noteClickPosition);//dùng chung cho cả update và delete
                    if(isNoteDeleted){
                        noteAdapter.notifyItemRemoved(noteClickPosition);//xóa
                    }else {//sửa
                        notesList.add(noteClickPosition,notes.get(noteClickPosition));
                        noteAdapter.notifyItemChanged(noteClickPosition);
                    }
                }
            }
        }
        new GetNoteTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUESR_CODE_ADD && resultCode == RESULT_OK)
            getNote(REQUESR_CODE_ADD,false);
        else if(requestCode == REQUESR_CODE_UPDATE && resultCode == RESULT_OK){
            if(data != null)
                getNote(REQUESR_CODE_UPDATE,data.getBooleanExtra("isRemoveNote",false));
        }
    }

    //view and update note
    @Override
    public void onNoteClicked(Notes temp, int position) {
        noteClickPosition = position;
        Intent i = new Intent(getActivity(),AddNoteScreen.class);
        i.putExtra("isViewUpdate",true);
        i.putExtra("data",temp);
        startActivityForResult(i,REQUESR_CODE_UPDATE);
    }

}
