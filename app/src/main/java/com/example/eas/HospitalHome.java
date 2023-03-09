package com.example.eas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.eas.databinding.ActivityHospitalHomeBinding;

public class HospitalHome extends AppCompatActivity {
    ActivityHospitalHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddAmbulance.class));
                finish();
            }
        });
        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HospitalBookings.class));
                finish();
            }
        });
    }

    public void onBackPressed() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(HospitalHome.this);
        alertbox.setMessage("Do you really wants to logout from this app?");
        alertbox.setTitle("Logout!!");

        alertbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("utype", "");
                editor.commit();
                startActivity(new Intent(HospitalHome.this, HomeActivity.class));
                finish();

            }
        });
        alertbox.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertbox.show();

    }
}