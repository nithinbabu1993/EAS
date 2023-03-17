package com.example.eas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eas.databinding.ActivityNewHospitalBinding;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.model.UserModel;
import com.example.eas.settings.Validation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NewHospital extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ActivityNewHospitalBinding binding;
    FirebaseFirestore db;
    Geocoder geo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewHospitalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = FirebaseFirestore.getInstance();
        binding.btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(binding.hname.getText().toString().isEmpty()){
                   binding.hname.setError("Enter hospital name");
               }
                else if(binding.haddress.getText().toString().isEmpty()){
                    binding.haddress.setError("Enter hospital address");
                }
                else if(binding.hlat.getText().toString().isEmpty()){
                    binding.hlat.setError("Enter hospital latitude");
                }
                else if(binding.hlon.getText().toString().isEmpty()){
                    binding.hlon.setError("Enter hospital longitude");
                }
               else if(binding.hphone.getText().toString().isEmpty()
               || !binding.hphone.getText().toString().matches(Validation.mobile)){
                    binding.hphone.setError("Enter hospital phone");
                }
                else if(binding.hpin.getText().toString().isEmpty()||binding.hpin.getText().toString().length()<4){
                    binding.hpin.setError("Enter 4 digit  login pin");
                }else{
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
                whereEqualTo("devId", binding.hpin.getText().toString()).
                whereEqualTo("phone", binding.hphone.getText().toString()).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            addHospital();
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(NewHospital.this, "This Login pin/Phone number Already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(NewHospital.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void addHospital() {
        Hospitalmodel obj = new Hospitalmodel(binding.hpin.getText().toString(), binding.hname.getText().toString(), binding.hphone.getText().toString(), binding.haddress.getText().toString(), "Hospital",binding.hlat.getText().toString(),binding.hlon.getText().toString());
        db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewHospital.this, "Hospital Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),NewHospital.class));finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewHospital.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        if (mMap != null) {
            geo = new Geocoder(NewHospital.this, Locale.getDefault());

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    final ProgressDialog progressDoalog = new ProgressDialog(NewHospital.this);
                    progressDoalog.setMessage("Checking....");
                    progressDoalog.setTitle("Please wait");
                    progressDoalog.setCancelable(false);
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDoalog.show();
                    mMap.clear();
                    try {
                        if (geo == null)
                            geo = new Geocoder(NewHospital.this, Locale.getDefault());
                        binding.hlat.setText(latLng.latitude+"");
                        binding.hlon.setText(latLng.longitude+"");
                        List<Address> address = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        if (address.size() > 0) {
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Name:" + address.get(0).getCountryName()
                                    + ". Address:" + address.get(0).getAddressLine(0)));
                            binding.haddress.setText("Country Name:" + address.get(0).getCountryName()
                                    + "\n. Address:" + address.get(0).getAddressLine(0));

                        }
                        progressDoalog.dismiss();
                    } catch (IOException ex) {
                        progressDoalog.dismiss();
                        if (ex != null)
                            Toast.makeText(NewHospital.this, "Error:" + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    binding.hlat.setText(marker.getPosition().latitude+"");
                    binding.hlon.setText(marker.getPosition().longitude+"");
                //    txtMarkers.setText(marker.getTitle().toString() + " Lat:" + marker.getPosition().latitude + " Long:" + marker.getPosition().longitude);
                    return false;
                }
            });
        }



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NewHospital.this, AdminHome.class));
        finish();
    }
}