package com.example.eas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eas.Adapter.AmbulanceAdapter;
import com.example.eas.Adapter.HlistAdapter;
import com.example.eas.databinding.ActivityAddAmbulanceBinding;
import com.example.eas.model.AmbulanceModel;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.settings.Validation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddAmbulance extends AppCompatActivity {
    ActivityAddAmbulanceBinding binding;
    FirebaseFirestore db;
    SharedPreferences sp;
    AmbulanceAdapter adapter = new AmbulanceAdapter();
    List<AmbulanceModel> Hlist = new ArrayList();
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAmbulanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        binding.rvAmbulanceList.setLayoutManager(new LinearLayoutManager(this));
        progressDoalog = new ProgressDialog(AddAmbulance.this);
        progressDoalog.setMessage("Adding Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        showData();
        binding.btnambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.aname.getText().toString().isEmpty()) {
                    binding.aname.setError("Enter Ambulance name/number");
                } else if (binding.dname.getText().toString().isEmpty()) {
                    binding.aname.setError("Enter Ambulance driver name");
                } else if (binding.dphone.getText().toString().isEmpty()
                || !binding.dphone.getText().toString().matches(Validation.mobile)) {
                    binding.aname.setError("Enter Ambulance driver number");
                }
                else if (binding.logpin.getText().toString().isEmpty()) {
                    binding.aname.setError("Enter Ambulance loginpin");
                }
                else {
                    checkmforloginpinAvailability();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddAmbulance.this, HospitalHome.class));
        finish();
    }
    private void checkmforloginpinAvailability() {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").
                whereEqualTo("devId", binding.logpin.getText().toString()).
                whereEqualTo("phone", binding.dphone.getText().toString()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            addAmbulance();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(AddAmbulance.this, "This Phone number/pin Already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(AddAmbulance.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addAmbulance() {
        AmbulanceModel obj = new AmbulanceModel(binding.aname.getText().toString(), binding.dname.getText().toString(), binding.dphone.getText().toString(), sp.getString("docId", ""), "Ambulance",binding.logpin.getText().toString());
        db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddAmbulance.this, "Ambulance Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AddAmbulance.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddAmbulance.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showData() {

        //Log.d("@", "showData: Called")
        Hlist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").
                whereEqualTo("utype", "Ambulance").
                whereEqualTo("hospId", sp.getString("docId", ""))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.getDocuments().size()>0) {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

                                Hlist.add(new AmbulanceModel(
                                        queryDocumentSnapshots.getDocuments().get(i).getString("aname"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("name"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("phone"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("hospId"),
                                        queryDocumentSnapshots.getDocuments().get(i).getString("utype"),
                                        queryDocumentSnapshots.getDocuments().get(i).getId()));
                            }
                            adapter.HospList = Hlist;
                            binding.rvAmbulanceList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "No Ambulance Available", Toast.LENGTH_SHORT).show();

                        }
                        progressDoalog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}