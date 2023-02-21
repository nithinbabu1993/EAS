package com.example.eas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class ShowAmbulance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ambulance);
        SharedPreferences sd=getSharedPreferences("hospital", Context.MODE_PRIVATE);
        Toast.makeText(this,sd.getString("hname",""), Toast.LENGTH_SHORT).show();
    }
}