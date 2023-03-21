package com.example.eas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.databinding.ActivityUpdateAddressBinding;
import com.example.eas.model.UserModel;
import com.example.eas.settings.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class UpdateAddress extends AppCompatActivity {
    ActivityUpdateAddressBinding binding;
    FirebaseFirestore db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        setUpView();
    }

    private void setUpView() {
        binding.btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.uname.getText().toString().isEmpty()) {
                    binding.uname.setError("Enter your name");
                } else if (binding.uphone.getText().toString().isEmpty() || !binding.uphone.getText().toString().matches(Validation.mobile)) {
                    binding.uphone.setError("Please enter a valid 10 digit uk phone number");
                } else if (binding.uaddress.getText().toString().isEmpty()) {
                    binding.uphone.setError("Please enter a valid address");
                } else {
                    checkmforloginpinAvailability();
                }
            }
        });
    }
    private void checkmforloginpinAvailability() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").
                whereEqualTo("phone", binding.uphone.getText().toString()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            updateAddress();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(UpdateAddress.this, "This Phone number Already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(UpdateAddress.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateAddress() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        UserModel obj = new UserModel(sp.getString("devId", "err"), binding.uname.getText().toString(), binding.uphone.getText().toString(),
                binding.uaddress.getText().toString(), "User");
        db.collection("User").document(sp.getString("docId", "err")).set(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("name",  binding.uname.getText().toString());
                        editor.putString("mobile", binding.uphone.getText().toString());
                        editor.putString("address", binding.uaddress.getText().toString());
                         editor.commit();
                        progressDoalog.dismiss();
                        Toast.makeText(UpdateAddress.this, "Address updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UpdateAddress.this, UserDashBoard.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(UpdateAddress.this, "updation failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(UpdateAddress.this, UserDashBoard.class));
        finish();

    }
}