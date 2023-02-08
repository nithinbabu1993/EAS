package com.example.eas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.eas.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    String mId = "0";
    FirebaseFirestore db;
    String People[] = {"Choose a Category", "User", "Admin", "Hospital", "Ambulance"};
    String item="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, People);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner2.setAdapter(aa);
        db = FirebaseFirestore.getInstance();
        mId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("@@", mId);
        setupViews();
    }

    void setupViews() {
        binding.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 item = binding.spinner2.getSelectedItem().toString();
                if (item.equals("Admin") || item.equals("Hospital") || item.equals("Ambulance")) {
                    binding.loginpin.setVisibility(View.VISIBLE);
                    binding.regDevId.setVisibility(View.GONE);
                    binding.textView2.setVisibility(View.GONE);
                } else if (item.equals("Choose a Category")) {
                    Toast.makeText(HomeActivity.this, "please choose a category", Toast.LENGTH_SHORT).show();
                } else {
                    binding.textView2.setVisibility(View.VISIBLE);
                    binding.regDevId.setVisibility(View.VISIBLE);
                    binding.loginpin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(HomeActivity.this, "please select a category", Toast.LENGTH_SHORT).show();
            }
        });
        binding.regDevId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRegisterFun();
            }
        });
        binding.textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLoginFun();
            }
        });
    }


    private void callLoginFun() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").whereEqualTo("username", mId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                progressDoalog.dismiss();
                                Toast.makeText(HomeActivity.this, "DeviceId not Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences sp = getSharedPreferences("logDetails", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor = sp.edit();
//                                Log.d("##", queryDocumentSnapshots.getDocuments().get(0).getString("userId") + "");
//                                editor.putString("userType", queryDocumentSnapshots.getDocuments().get(0).getString("type"));
//                                editor.putString("name", queryDocumentSnapshots.getDocuments().get(0).getString("name"));
//                                editor.putString("mobile", queryDocumentSnapshots.getDocuments().get(0).getString("phone"));
//                                editor.putString("userId", queryDocumentSnapshots.getDocuments().get(0).getString("userId"));
//                                editor.putString("userDocID", queryDocumentSnapshots.getDocuments().get(0).getId());
                                editor.commit();
                                progressDoalog.dismiss();
                            }

                        } catch (Exception e) {
                            //progressDoalog.dismiss();
                            Log.d("exception: ", e.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDoalog.dismiss();
                        Toast.makeText(HomeActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void callRegisterFun() {

    }
}