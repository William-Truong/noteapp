package com.example.william.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.william.R;
import com.example.william.database.NoteDatabase;
import com.example.william.entities.Notes;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomPicker.Builder;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class AddNoteScreen extends AppCompatActivity {

    private ImageView imgSave,imgClear,imgBack;
    private EditText edtTitle,edtSubtitle,edtContent;
    private TextView txtDateTime;

    //Color note
    private View vSubtitleColor;
    private String noteColor;

    //Select Photo
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private ImageView imgNote;
    private String imagePath;

    //URL
    private TextView txtURL;
    private LinearLayout layoutURL;
    private AlertDialog dialogURL;

    //View and update
    private Notes noteSelected;

    //delete note
    private AlertDialog dialogDeleteNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        addControls();
        addEvents();
    }

    private void addControls() {
        imgBack = findViewById(R.id.imgBack);
        imgClear = findViewById(R.id.imgClear);
        imgSave = findViewById(R.id.imgDone);
        edtTitle = findViewById(R.id.edtTitle);
        txtDateTime = findViewById(R.id.txtDateTime);
        edtSubtitle = findViewById(R.id.edtsubtitle);
        edtContent = findViewById(R.id.edtContent);
        vSubtitleColor = findViewById(R.id.subtilte_color);
        imgNote = findViewById(R.id.imageNote);
        layoutURL = findViewById(R.id.layoutweb);
        txtURL = findViewById(R.id.txtURL);
    }

    private void addEvents() {
        //Get Time
        txtDateTime.setText( new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));
        //N??t Clear
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtTitle.setText(null);
                edtSubtitle.setText(null);
                edtContent.setText(null);
                imgNote.setImageBitmap(null);
                txtURL.setText(null);
            }
        });
        //N??t L??u
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //n???u Ti??u ????? t???ng
                if(edtTitle.getText().toString().trim().isEmpty())
                    edtTitle.setError("Title can't not Empty!");
                else//n???u ko r???ng
                    saveNote();
            }
        });
        //N??t back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtTitle.getText().toString().isEmpty())
                    onBackPressed();
                else
                    saveNote();
            }
        });

        //color note
        noteColor = "#333333";
        imagePath = "";

        //view and update
        if(getIntent().getBooleanExtra("isViewUpdate",false)){
            noteSelected = (Notes) getIntent().getSerializableExtra("data");
            viewandupdate();
        }

        toolbar();
        setSubtitleColor();

        //delete Photo and url
        findViewById(R.id.imgDeleteURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtURL.setText(null);
                layoutURL.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.imgRemovePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgNote.setImageBitmap(null);//x??a ???nh
                imgNote.setVisibility(View.GONE);//???n view ???nh v???a x??a
                findViewById(R.id.imgRemovePhoto).setVisibility(View.GONE);//???n n??t x??a ???nh
                imagePath = "";//x??a ???????ng d???n
            }
        });
    }

    private void saveNote() {
        //T???o note m???i
        final Notes temp = new Notes();
        temp.setTitle(edtTitle.getText().toString().trim());
        temp.setDatetime(txtDateTime.getText().toString());
        temp.setSubtilte(edtSubtitle.getText().toString());
        temp.setContent(edtContent.getText().toString());
        temp.setColorr(noteColor);
        temp.setImgphoto(imagePath);

        if(layoutURL.getVisibility() == View.VISIBLE)
            temp.setWeblinkk(txtURL.getText().toString());
        if(noteSelected != null)
            temp.setId(noteSelected.getId());

        //l??u v??o DATABASE
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getDatabase(getApplicationContext()).noteDB().addNote(temp);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) { //tr??? v??? m??n h??nh list note
                super.onPostExecute(aVoid);
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    private void setSubtitleColor() {
        GradientDrawable temp = (GradientDrawable) vSubtitleColor.getBackground();
        temp.setColor(Color.parseColor(noteColor));
    }

    private void toolbar() {
        final LinearLayout ToolColor = findViewById(R.id.layoutTools);
        final BottomSheetBehavior<LinearLayout> bottomsheet = BottomSheetBehavior.from(ToolColor);

        ToolColor.findViewById(R.id.txtSelectColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomsheet.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    bottomsheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final ImageView imgcolor1 = findViewById(R.id.imgColor1);
        final ImageView imgcolor2 = findViewById(R.id.imgColor2);
        final ImageView imgcolor3 = findViewById(R.id.imgColor3);
        final ImageView imgcolor4 = findViewById(R.id.imgColor4);
        final ImageView imgcolor5 = findViewById(R.id.imgColor5);

        ToolColor.findViewById(R.id.vcolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteColor = "#333333";
                imgcolor1.setImageResource(R.drawable.ic_done);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setSubtitleColor();
            }
        });
        ToolColor.findViewById(R.id.vcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteColor = "#FDBE38";
                imgcolor1.setImageResource(0);
                imgcolor2.setImageResource(R.drawable.ic_done);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setSubtitleColor();
            }
        });
        ToolColor.findViewById(R.id.vcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteColor = "#FF4842";
                imgcolor1.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(R.drawable.ic_done);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setSubtitleColor();
            }
        });
        ToolColor.findViewById(R.id.vcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteColor = "#3A52Fc";
                imgcolor1.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(R.drawable.ic_done);
                imgcolor5.setImageResource(0);
                setSubtitleColor();
            }
        });
        ToolColor.findViewById(R.id.vcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteColor = "#000000";
                imgcolor1.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(R.drawable.ic_done);
                setSubtitleColor();
            }
        });

        //th??m ???nh
        ToolColor.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddNoteScreen.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_PERMISSION);
                }else
                    selectImage();
            }
        });

        //th??m link web
        ToolColor.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showURLdialog();
            }
        });

        //update color
        if(noteSelected != null && noteSelected.getColorr() != null && !noteSelected.getColorr().trim().isEmpty()){
            switch (noteSelected.getColorr()){
                case "#FDBE38":
                    ToolColor.findViewById(R.id.vcolor2).performClick();
                    break;
                case "#FF4842":
                    ToolColor.findViewById(R.id.vcolor3).performClick();
                    break;
                case "#3A52Fc":
                    ToolColor.findViewById(R.id.vcolor4).performClick();
                    break;
                case "#000000":
                    ToolColor.findViewById(R.id.vcolor5).performClick();
                    break;
            }
        }

        //M???c ?????nh khi th??m note s??? ???n n??t x??a note ??i
        //n???u ??ang s???a note th?? s??? hi???n th??? n??t x??a
        if(noteSelected != null){
            ToolColor.findViewById(R.id.layoutDelete).setVisibility(View.VISIBLE);
            ToolColor.findViewById(R.id.layoutDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomsheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showdialogDelete();
                }
            });
        }


    }

    //select photo start
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
            else
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null)
            startActivityForResult(i,REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK){
            if(data != null){
                Uri ImageUri = data.getData();
                if(ImageUri != null)
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(ImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgNote.setImageBitmap(bitmap);//set ???nh
                        imgNote.setVisibility(View.VISIBLE);//show ???nh
                        findViewById(R.id.imgRemovePhoto).setVisibility(View.VISIBLE); // show n??t x??a
                        imagePath = getPathImage(ImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    private String getPathImage(Uri temp){
        String filePath;
        Cursor c = getContentResolver().query(temp,null,null,null,null);
        if(c == null)
            filePath = temp.getPath();
        else {
            c.moveToFirst();
            int i = c.getColumnIndex("_data");
            filePath = c.getString(i);
            c.close();
        }
        return filePath;
    }
    //end select photo

    //add URL
    private void showURLdialog(){
        if(dialogURL == null){
            AlertDialog.Builder b = new AlertDialog.Builder(AddNoteScreen.this);
            View v = LayoutInflater.from(this).inflate(R.layout.web_dialog, this.<ViewGroup>findViewById(R.id.layout_url));
            b.setView(v);

            dialogURL = b.create();
            if(dialogURL.getWindow() != null)
                dialogURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            final  EditText edtURL = v.findViewById(R.id.edtAddurl);
            edtURL.requestFocus();

            v.findViewById(R.id.txtAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edtURL.getText().toString().trim().isEmpty())
                        Toast.makeText(AddNoteScreen.this,"Enter URL",Toast.LENGTH_SHORT).show();
                    else {
                        txtURL.setText(edtURL.getText().toString());
                        layoutURL.setVisibility(View.VISIBLE);
                        dialogURL.dismiss();
                    }
                }
            });

            v.findViewById(R.id.txtCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogURL.dismiss();
                }
            });
        }
        dialogURL.show();
    }

    //view and update
    private void viewandupdate(){
        edtTitle.setText(noteSelected.getTitle());
        edtSubtitle.setText(noteSelected.getSubtilte());
        edtContent.setText(noteSelected.getContent());
        txtDateTime.setText(noteSelected.getDatetime());

        if(noteSelected.getImgphoto() != null && !noteSelected.getImgphoto().trim().isEmpty()){
            imgNote.setImageBitmap(BitmapFactory.decodeFile(noteSelected.getImgphoto()));
            imgNote.setVisibility(View.VISIBLE);

            findViewById(R.id.imgRemovePhoto).setVisibility(View.VISIBLE);
            imagePath = noteSelected.getImgphoto();
        }
        if(noteSelected.getWeblinkk() != null && !noteSelected.getWeblinkk().trim().isEmpty()){
            txtURL.setText(noteSelected.getWeblinkk());
            layoutURL.setVisibility(View.VISIBLE);
        }

    }

    //Delete Note
    public void showdialogDelete(){
        if(dialogDeleteNote == null){
            //n??i dialog hi???n th???
            AlertDialog.Builder b = new AlertDialog.Builder(AddNoteScreen.this);
            //t???o custom dialog
            View v = LayoutInflater.from(this).inflate(R.layout.delete_note, this.<ViewGroup>findViewById(R.id.layout_deleteNote));
            //n???p custom view v??o dialog
            b.setView(v);

            dialogDeleteNote = b.create();
            if(dialogDeleteNote.getWindow() != null)
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            //nh???n cancel
            v.findViewById(R.id.txtDCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
            //nh???n yes
            v.findViewById(R.id.txtyes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            //truy v???n database v?? x??a note ??ang m???
                            NoteDatabase.getDatabase(getApplicationContext()).noteDB().deleteNote(noteSelected);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            //quay l???i m??n h??nh list note v?? ki???m tra xem c?? x??a hay ch??a
                            Intent i = new Intent();
                            i.putExtra("isRemoveNote",true);
                            setResult(RESULT_OK,i);
                            finish();
                        }
                    }
                    //kh???i ch???y task
                    new DeleteNoteTask().execute();
                }
            });

            //hi???n th??? dialog x??a note
            dialogDeleteNote.show();
        }
    }
}