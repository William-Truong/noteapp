package com.example.william.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.william.R;
import com.example.william.entities.Reminders;
import com.example.william.listener.NoteListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReminderAdapter extends FirebaseRecyclerAdapter<Reminders,ReminderAdapter.ReminderViewHolder> {

    public ReminderAdapter(@NonNull FirebaseRecyclerOptions<Reminders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReminderViewHolder holder, final int position, @NonNull final Reminders model) {
        holder.txtTitle.setText(model.getTitle());
        if(model.getDescription() != null && model.getDescription() != ""){
            holder.txtDesc.setText(model.getDescription());
            holder.txtDesc.setVisibility(View.VISIBLE);
        }
        if(holder.ckFinish.isChecked()){
            holder.ckFinish.setChecked(false);
        }else{
            holder.ckFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("Reminder").
                                child(getRef(position).getKey()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(),"Done!",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(v.getContext(),"Error!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }catch (Exception e){
                        Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        if(model.getTime() != null || model.getDate() != null){
            String tempDATETIME;
            if(model.getTime() == null)
                tempDATETIME = model.getDate();
            else if (model.getDate() == null)
                tempDATETIME = model.getTime();
            else
                tempDATETIME = model.getTime() + " on " + model.getDate();
            holder.txtTime.setText(tempDATETIME.trim());
            holder.txtTime.setVisibility(View.VISIBLE);
        }

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),getRef(position).getKey(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder,parent,false);
        return new ReminderViewHolder(v);
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        CheckBox ckFinish;
        TextView txtTitle,txtDesc,txtTime;
        ConstraintLayout layout_item;
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            ckFinish = itemView.findViewById(R.id.ckFinish);
            txtTitle = itemView.findViewById(R.id.title_reminder);
            txtDesc = itemView.findViewById(R.id.desctiption_reminder);
            txtTime = itemView.findViewById(R.id.datetime_reminder);
            layout_item = itemView.findViewById(R.id.layout_item_reminder);
        }
    }
}
