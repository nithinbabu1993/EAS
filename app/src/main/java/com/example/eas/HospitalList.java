package com.example.eas;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.eas.Adapter.HlistAdapter;
import com.example.eas.databinding.ActivityHospitalListBinding;
import com.example.eas.model.Hospitalmodel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HospitalList extends AppCompatActivity {
ActivityHospitalListBinding binding;
    HlistAdapter adapter = new HlistAdapter();
    List<Hospitalmodel> Hlist = new ArrayList();
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHospitalListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rvHlist.setLayoutManager(new LinearLayoutManager(this));
        progressDoalog = new ProgressDialog(HospitalList.this);
        progressDoalog.setMessage("Adding Data....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        showData();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(HospitalList.this, AdminHome.class));
        finish();
    }

    private void showData() {

        //Log.d("@", "showData: Called")
        Hlist.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User").whereEqualTo("utype", "Hospital")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
if(queryDocumentSnapshots.getDocuments().size()>0) {
    int i;
    for (i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

        Hlist.add(new Hospitalmodel(
                queryDocumentSnapshots.getDocuments().get(i).getId(),
                queryDocumentSnapshots.getDocuments().get(i).getString("name"),
                queryDocumentSnapshots.getDocuments().get(i).getString("phone"),
                queryDocumentSnapshots.getDocuments().get(i).getString("address"),
                queryDocumentSnapshots.getDocuments().get(i).getString("utype"),
                queryDocumentSnapshots.getDocuments().get(i).getString("hlatitude"),
                queryDocumentSnapshots.getDocuments().get(i).getString("hlongitude")));
    }
    adapter.HospList = Hlist;
    binding.rvHlist.setAdapter(adapter);
    adapter.notifyDataSetChanged();
}else{
    Toast.makeText(getApplicationContext(), "No hospitals Available", Toast.LENGTH_SHORT).show();

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