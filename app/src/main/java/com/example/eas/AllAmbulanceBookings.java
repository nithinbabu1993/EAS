package com.example.eas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eas.Adapter.BookingAdapter;
import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.databinding.ActivityAllAmbulanceBookingsBinding;
import com.example.eas.databinding.ActivityUserDashBoardBinding;
import com.example.eas.model.Bookingmodel;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.settings.GPSTracker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllAmbulanceBookings extends AppCompatActivity {
    private ActivityAllAmbulanceBookingsBinding binding;
    private GoogleMap mMap;
    List<Bookingmodel> BookingList = new ArrayList();
    RecyclerView rv;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllAmbulanceBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rv=findViewById(R.id.rv_bookings);
        LinearLayoutManager ll=new LinearLayoutManager(this);
        rv.setLayoutManager(ll);
        progressDoalog=new ProgressDialog(this);
        MyBookings();
    }
    private void MyBookings() {
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        Log.d("##", sp.getString("docId",""));
        BookingList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Booking")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        int i;
                        for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

                            BookingList.add(new Bookingmodel(
                                    queryDocumentSnapshots.getDocuments().get(i).getId(),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("ambulanceId"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("hospitalId"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("uname"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("uaddress"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("uphone"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("ulatitude"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("ulongitude"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("ambNo"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("driverName"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("driverPhone"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("bdate"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("dlatitude"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("dlongitude"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("hname"),
                                    queryDocumentSnapshots.getDocuments().get(i).getString("bstatus")
                            ));
                        }
                        if (BookingList.isEmpty()) {
                            Toast.makeText(AllAmbulanceBookings.this, "No Bookings Found", Toast.LENGTH_SHORT).show();
                        } else {
                            BookingAdapter aa= new BookingAdapter();
                            aa.bookingList=BookingList;
                            rv.setAdapter(aa);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),AdminHome.class));finish();
    }
}