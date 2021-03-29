package com.example.william.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.william.R;

public class AddRemiderScreen extends AppCompatActivity {
    private ImageView imgSave,imgBack,imgTimeLine,imgDelete;
    private EditText edtTitle,edtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addremider);

        anhXa();
        addEvents();
    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void anhXa() {
        imgSave = findViewById(R.id.imgSaveR);
        imgBack = findViewById(R.id.imgBackR);
        imgTimeLine = findViewById(R.id.imgTimeR);
        imgDelete = findViewById(R.id.rimgDelete);
    }
}