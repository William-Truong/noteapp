package com.example.william.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.william.R;
import com.example.william.adapter.ReminderAdapter;
import com.example.william.entities.Reminders;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ReminderFrament extends Fragment {
    private ImageView btnAddReminder;

    private Calendar c = Calendar.getInstance();
    private String date,timee;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private RecyclerView lvData;
    ReminderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_reminders,container,false);

        btnAddReminder = v.findViewById(R.id.btnAddReminder);
        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReminderDialog(Gravity.BOTTOM);
            }
        });

        lvData = v.findViewById(R.id.lvReminderData);
        lvData.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseRecyclerOptions<Reminders> options = new FirebaseRecyclerOptions.Builder<Reminders>()
                .setQuery(db.child("Reminder"),Reminders.class).build();
        adapter = new ReminderAdapter(options);

        adapter.startListening();
        lvData.setAdapter(adapter);

        db.keepSynced(true);
        return v;
    }

    private void showAddReminderDialog(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_reminder_dialog);

        Window window = dialog.getWindow();
        if(window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity)
            dialog.setCancelable(true);
        else
            dialog.setCancelable(false);
        EditText edtTitleR = dialog.findViewById(R.id.edtTilteR);
        EditText edtDescription = dialog.findViewById(R.id.edtDescriptionR);

        ImageView imgShowDescription = dialog.findViewById(R.id.imgShowDesctiptionEditext);
        ImageView imgDatetimePicker = dialog.findViewById(R.id.imgdatetimepicker);

        TextView txtDatetimeSaved = dialog.findViewById(R.id.txtDateTimeR);
        TextView txtSaveR = dialog.findViewById(R.id.txtSaveR);

        imgShowDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtDescription.getVisibility() == View.GONE)
                    edtDescription.setVisibility(View.VISIBLE);
                else if(edtDescription.getVisibility() == View.VISIBLE)
                    edtDescription.setVisibility(View.GONE);
            }
        });

        imgDatetimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTime(Gravity.BOTTOM);
                txtDatetimeSaved.setText(date);
                txtDatetimeSaved.setVisibility(View.VISIBLE);
            }
        });

        txtSaveR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTitleR.getText().toString().trim().isEmpty())
                    edtTitleR.setError("Title can not blank!");
                else{
                    Reminders temp = new Reminders();
                    temp.setTitle(edtTitleR.getText().toString());
                    temp.setDescription(edtDescription.getText().toString());
                    temp.setDate(date);
                    db.child("Reminder").push().setValue(temp);
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
    }

    private void showAddTime(int gravity){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_datetime_picker);
        Window window = dialog.getWindow();
        if(window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity)
            dialog.setCancelable(true);
        else
            dialog.setCancelable(false);

        CalendarView pkDate = dialog.findViewById(R.id.pkDatepicker);
        ConstraintLayout layout = dialog.findViewById(R.id.layout_setTime);
        TextView txtSetTime = dialog.findViewById(R.id.txtSetTime);
        TextView txtCancel = dialog.findViewById(R.id.CancelDateTime);
        TextView txtSave = dialog.findViewById(R.id.SaveDateTime);

        pkDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth+"/"+(month+1);
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = c.get(c.HOUR);
                int minute = c.get(c.MINUTE);
                TimePickerDialog Timedialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timee = hourOfDay+":"+minute;

                        txtSetTime.setText(timee);
                    }
                },hour,minute,true);
                Timedialog.show();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
