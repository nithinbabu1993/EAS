package com.example.eas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.eas.Adapter.AmbulanceAdapter;
import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.databinding.ActivityShowAmbulanceBinding;
import com.example.eas.model.AmbulanceModel;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.settings.LocationMonitoringService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAmbulance extends AppCompatActivity {
    ActivityShowAmbulanceBinding binding;

    String latitude, longitude;
    FirebaseFirestore db;
    SharedPreferences sp;
    AmbulanceAdapter adapter = new AmbulanceAdapter();
    List<AmbulanceModel> Hlist = new ArrayList();
    ProgressDialog progressDoalog;
    String val[]=new String[100];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAmbulanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences sd = getSharedPreferences("hospital", Context.MODE_PRIVATE);
        binding.address.setText(sd.getString("hname", ""));
         val = sd.getString("hname", "").trim().split(":");
        binding.rvAmbulance.setLayoutManager(new LinearLayoutManager(this));
        progressDoalog = new ProgressDialog(ShowAmbulance.this);
        progressDoalog.setMessage("Adding Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        showData(val[6]);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShowAmbulance.this, UserDashBoard.class));
        finish();
    }

    private void showData(String s) {

        //Log.d("@", "showData: Called")
        Hlist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").
                whereEqualTo("utype", "Ambulance").
                whereEqualTo("hospId", s)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().size() > 0) {
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
                            binding.rvAmbulance.setAdapter(adapter);
                            sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                            adapter.uid = sp.getString("docId", "");
                            adapter.uname = sp.getString("name", "");
                            adapter.uaddress = sp.getString("address", "");
                            adapter.uphone = sp.getString("mobile", "");
                            adapter.HospitalId = val[6];

                            adapter.notifyDataSetChanged();
                        } else {
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