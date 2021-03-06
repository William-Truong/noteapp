package com.example.william.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.william.R;
import com.example.william.activities.AddNoteScreen;
import com.example.william.database.NoteDatabase;
import com.example.william.entities.Notes;
import com.example.william.listener.NoteListener;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Notes> notesList;

    //view and update
    private NoteListener noteListener;

    //search
    private Timer timer;
    private List<Notes> noteSource;


    public NoteAdapter(List<Notes> temp,NoteListener temp2){
        this.notesList = temp;
        this.noteListener = temp2;
        noteSource = temp;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notesList.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onNoteClicked(notesList.get(position),position);
            }
        });
        holder.layoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                noteListener.onNoteLongClicked(notesList.get(position),position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView txtTitle,txtSubtitle,txtDatetime;
        LinearLayout layoutNote;
        RoundedImageView imageNote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle_Item);
            txtSubtitle = itemView.findViewById(R.id.txtSubtitle_item);
            txtDatetime = itemView.findViewById(R.id.txtDateTime_item);
            layoutNote = itemView.findViewById(R.id.item_note_layout);
            imageNote = itemView.findViewById(R.id.itemImgNote);
        }

        void setNote(Notes temp){
            txtTitle.setText(temp.getTitle());

            //n???u subtitle r???ng th?? ???n view ???? ??i
            if(temp.getSubtilte().trim().isEmpty())
                txtSubtitle.setVisibility(View.GONE);//???n subtitle
            else
                txtSubtitle.setText(temp.getSubtilte());

            //ph???n ?????i m??u note
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (temp.getColorr() != null)
                gradientDrawable.setColor(Color.parseColor(temp.getColorr()));
            else
                gradientDrawable.setColor(Color.parseColor("#333333"));

            //ph???n ???nh cho note
            if(temp.getImgphoto() != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(temp.getImgphoto()));
                imageNote.setVisibility(View.VISIBLE);
            }else
                imageNote.setVisibility(View.GONE);

            txtDatetime.setText(temp.getDatetime());
        }
    }

    public void searchNote(final String keyWord){
        timer = new Timer();
        //truy???n c??ng vi???c ???????c th???c thi v?? th???i gian ch???y l???p l???i
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(keyWord.trim().isEmpty()){
                    notesList = noteSource;
                }else{
                    ArrayList<Notes> temp = new ArrayList<>();

                    for(Notes i : noteSource){
                        if(i.getTitle().toLowerCase().contains(keyWord.toLowerCase())
                        || i.getSubtilte().toLowerCase().contains(keyWord.toLowerCase())
                        || i.getContent().toLowerCase().contains(keyWord.toLowerCase())){
                            temp.add(i); // c??? t??m th???y th?? th??m v??o 1 list
                        }
                        notesList = temp; //g??n danh s??ch t??m ??c
                    }
                }
                //sau khi c?? k???t qu??? th?? th??ng b??o l??n giao di???n list t??m d
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },400);
    }

    public void cancelTimer(){
        if(timer != null)
            timer.cancel();
    }




}
