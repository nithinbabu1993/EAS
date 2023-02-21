package com.example.eas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.eas.databinding.ActivityShowAmbulanceBinding;

public class ShowAmbulance extends AppCompatActivity {
ActivityShowAmbulanceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityShowAmbulanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sd=getSharedPreferences("hospital", Context.MODE_PRIVATE);
        Toast.makeText(this,sd.getString("hname",""), Toast.LENGTH_SHORT).show();
        binding.address.setText(sd.getString("hname",""));
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShowAmbulance.this, ChooseActivity.class));
        finish();
    }
}