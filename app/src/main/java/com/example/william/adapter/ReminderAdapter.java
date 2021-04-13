package com.example.william.adapter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.william.R;
import com.example.william.entities.Reminders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderAdapter extends FirebaseRecyclerAdapter<Reminders,ReminderAdapter.ReminderViewHolder> {
    String date,time;
    private Calendar cdate = Calendar.getInstance();
    private Calendar ctime = Calendar.getInstance();

    public ReminderAdapter(@NonNull FirebaseRecyclerOptions<Reminders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReminderViewHolder holder, final int position, @NonNull final Reminders model) {

        holder.txtTitle.setText(model.getTitle());

        if(!model.getDescription().equalsIgnoreCase("")){
            holder.txtDesc.setText(model.getDescription());
            holder.txtDesc.setVisibility(View.VISIBLE);
        }


        if(model.getTime() != null || model.getDate() != null){
            String tempDATETIME;
            if(model.getTime() == null)
                tempDATETIME = model.getDate();
            else if (model.getDate() == null)
                tempDATETIME = model.getTime();
            else
                tempDATETIME = model.getTime() + ", " + model.getDate();
            holder.txtTime.setText(tempDATETIME.trim());
            holder.txtTime.setVisibility(View.VISIBLE);
        }

        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_edit_reminder);

                Window window = dialog.getWindow();
                if(window == null)
                    return;

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowAttributes = window.getAttributes();

                windowAttributes.gravity = Gravity.BOTTOM;
                window.setAttributes(windowAttributes);
                dialog.setCancelable(true);

                ImageView imgBackEditReminder = dialog.findViewById(R.id.imgBackEditReminder);
                ImageView imgSaveEditReminder = dialog.findViewById(R.id.imgSaveEditReminder);
                EditText edtEditTitleReminder = dialog.findViewById(R.id.edtTitleEditReminder);
                EditText edtEditDescriptionReminder = dialog.findViewById(R.id.edtDescriptionEditReminder);
                ConstraintLayout layoutShowDescReminder = dialog.findViewById(R.id.layout_addDescriptionReminder);
                ConstraintLayout layoutSetDateTime = dialog.findViewById(R.id.layout_editDateTimeReminder);
                TextView txtDateTimeReminderChanged = dialog.findViewById(R.id.txtDateTimeEditReminder);
                TextView txtMarkDone = dialog.findViewById(R.id.txtMarkDone);

                edtEditTitleReminder.setText(model.getTitle());
                if(model.getDescription().equalsIgnoreCase("")){
                    layoutShowDescReminder.setVisibility(View.VISIBLE);
                }else{
                    edtEditDescriptionReminder.setText(model.getDescription());
                    edtEditDescriptionReminder.setVisibility(View.VISIBLE);
                    layoutShowDescReminder.setVisibility(View.GONE);

                }


                layoutShowDescReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtEditDescriptionReminder.getVisibility() == View.GONE)
                            edtEditDescriptionReminder.setVisibility(View.VISIBLE);
                        else if(edtEditDescriptionReminder.getVisibility() == View.VISIBLE){
                            edtEditDescriptionReminder.setVisibility(View.GONE);
                            edtEditDescriptionReminder.setText(null);
                        }
                    }
                });

                if(model.getTime() != null || model.getDate() != null){
                    String tempDateTime2;
                    if(model.getTime() == null)
                        tempDateTime2 = model.getDate();
                    else if(model.getDate() == null)
                        tempDateTime2 = model.getTime();
                    else
                        tempDateTime2 = model.getTime() + " , " + model.getDate();
                    txtDateTimeReminderChanged.setText(tempDateTime2.trim());
                    txtDateTimeReminderChanged.setVisibility(View.VISIBLE);
                }

                dialog.show();

                imgBackEditReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgSaveEditReminder.performClick();
                    }
                });

                txtMarkDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FirebaseDatabase.getInstance().getReference().child("Reminder").
                                    child(getRef(position).getKey()).removeValue();
                        }catch (Exception e){
                            Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                layoutSetDateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogb = new Dialog(v.getContext());
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

                        layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int hour = ctime.get(ctime.HOUR);
                                int minute = ctime.get(ctime.MINUTE);

                                TimePickerDialog Timedialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
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
                                if(time != null || date != null){
                                    String temptimedate;
                                    if(time == null)
                                        temptimedate = date;
                                    else if(date == null)
                                        temptimedate = time;
                                    else
                                        temptimedate = time + ", " + date;
                                    txtDateTimeReminderChanged.setText(temptimedate.trim());
                                }
                                dialogb.dismiss();
                            }
                        });

                        dialogb.show();
                    }
                });

                imgSaveEditReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Reminders tempObject = new Reminders();
                        tempObject.setTitle(edtEditTitleReminder.getText().toString().trim());
                        tempObject.setDescription(edtEditDescriptionReminder.getText().toString().trim());
                        tempObject.setDate(date);
                        tempObject.setTime(time);
                        try {
                            FirebaseDatabase.getInstance().getReference().child("Reminder")
                                    .child(getRef(position).getKey()).setValue(tempObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                }
                            });
                        }catch (Exception e){
                            Toast.makeText(v.getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }

    //Use to swipe to delete
    public void deleteItem(int position){
        FirebaseDatabase.getInstance().getReference().child("Reminder").
                child(getRef(position).getKey()).removeValue();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder,parent,false);
        return new ReminderViewHolder(v);
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtDesc,txtTime;
        ConstraintLayout layout_item;
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.title_reminder);
            txtDesc = itemView.findViewById(R.id.desctiption_reminder);
            txtTime = itemView.findViewById(R.id.datetime_reminder);
            layout_item = itemView.findViewById(R.id.layout_item_reminder);
        }

    }

}
