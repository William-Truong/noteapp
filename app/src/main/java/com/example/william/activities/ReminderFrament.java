package com.example.william.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.william.R;
import com.example.william.adapter.ReminderAdapter;
import com.example.william.entities.Reminders;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderFrament extends Fragment{

    private ImageView btnAddReminder;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private RecyclerView lvData;
    ReminderAdapter adapter;

    private Calendar cdate = Calendar.getInstance();
    private Calendar ctime = Calendar.getInstance();
    public String date,time;
    private SwipeRefreshLayout refreshLayout;
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

        refreshLayout = v.findViewById(R.id.swipeRefreshReminder);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.startListening();
                refreshLayout.setRefreshing(false);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(lvData);

        db.keepSynced(true);
        return v;
    }

    public void showAddReminderDialog(int gravity) {
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
        TextView txtSaveR = dialog.findViewById(R.id.txtSaveR);

        imgShowDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtDescription.getVisibility() == View.GONE)
                    edtDescription.setVisibility(View.VISIBLE);
                else if(edtDescription.getVisibility() == View.VISIBLE){
                    edtDescription.setVisibility(View.GONE);
                    edtDescription.setText(null);
                }
            }
        });

        imgDatetimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogb = new Dialog(getActivity());
                dialogb.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogb.setContentView(R.layout.layout_datetime_picker);

                Window window = dialogb.getWindow();
                if(window == null)
                    return;

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.BOTTOM;
                window.setAttributes(windowAttributes);

                CalendarView pkDate = dialogb.findViewById(R.id.pkDatepicker);
                ConstraintLayout layout = dialogb.findViewById(R.id.layout_setTime);
                TextView txtSetTime = dialogb.findViewById(R.id.txtSetTime);
                TextView txtCancel = dialogb.findViewById(R.id.CancelDateTime);
                TextView txtSave = dialogb.findViewById(R.id.SaveDateTime);

                pkDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        cdate.set(year,month,dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                        date = dateFormat.format(cdate.getTime());
                    }
                });

                if(date == null){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                    date = dateFormat.format(cdate.getTime());
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = ctime.get(ctime.HOUR);
                        int minute = ctime.get(ctime.MINUTE);

                        TimePickerDialog Timedialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                ctime.set(0,0,0,hourOfDay,minute);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                time = dateFormat.format(ctime.getTime());
                                txtSetTime.setText(time);
                            }
                        }, hour, minute, true);
                        Timedialog.show();
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date = null;
                        time = null;
                        dialogb.dismiss();
                    }
                });

                txtSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogb.dismiss();
                    }
                });

                dialogb.show();

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
                    temp.setTime(time);
                    db.child("Reminder").push().setValue(temp);
                    date = null;
                    time = null;
                    dialog.dismiss();
                }
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
