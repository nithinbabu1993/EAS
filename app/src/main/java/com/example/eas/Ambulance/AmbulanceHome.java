package com.example.eas.Ambulance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eas.Adapter.BookingAdapter;
import com.example.eas.HomeActivity;
import com.example.eas.HospitalBookings;
import com.example.eas.HospitalHome;
import com.example.eas.R;
import com.example.eas.databinding.ActivityAmbulanceHomeBinding;
import com.example.eas.databinding.ActivityHospitalBookingsBinding;
import com.example.eas.model.Bookingmodel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AmbulanceHome extends AppCompatActivity {
    private ActivityAmbulanceHomeBinding binding;
    private GoogleMap mMap;
    List<Bookingmodel> BookingList = new ArrayList();
    RecyclerView rv;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAmbulanceHomeBinding.inflate(getLayoutInflater());
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
                .whereEqualTo("ambulanceId",sp.getString("docId",""))
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
                                    queryDocumentSnapshots.getDocuments().get(i).getString("hname")
                            ));
                        }
                        if (BookingList.isEmpty()) {
                            Toast.makeText(AmbulanceHome.this, "No Bookings Found", Toast.LENGTH_SHORT).show();
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

    public void onBackPressed() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(AmbulanceHome.this);
        alertbox.setMessage("Do you really wants to logout from this app?");
        alertbox.setTitle("Logout!!");

        alertbox.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("utype", "");
                editor.commit();
                startActivity(new Intent(AmbulanceHome.this, HomeActivity.class));
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