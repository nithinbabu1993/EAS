package com.example.eas;

import static android.Manifest.permission.CALL_PHONE;

import static java.lang.Double.parseDouble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.eas.databinding.ActivityChooseBinding;
import com.example.eas.model.Bookingmodel;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.settings.GPSTracker;
import com.example.eas.settings.LocationMonitoringService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends FragmentActivity implements OnMapReadyCallback {
    GPSTracker gps;
    private GoogleMap mMap;
    ActivityChooseBinding binding;
    List<Hospitalmodel> Hlist = new ArrayList();
    private int STORAGE_PERMISSION_CODE = 23;
    boolean somePermissionsForeverDenied = false;
    String latitude, longitude;
    Boolean b = false;
    ProgressDialog progressDoalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDoalog = new ProgressDialog(ChooseActivity.this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(true);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                        //Toast.makeText(ChooseActivity.this, latitude + longitude + "", Toast.LENGTH_SHORT).show();
                        if (latitude != null && longitude != null) {
                            Bookingmodel.latitude=latitude;
                            Bookingmodel.longitude=longitude;
//                            LatLng latLng = new LatLng(parseDouble(latitude), parseDouble(longitude));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
//                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .title("you are Here")
//                                    .icon(BitmapDescriptorFactory
//                                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))).showInfoWindow();
                            if (b == false) {
                                mMap.clear();
                                showData();
                                b = true;
                            }

                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));

        binding.endride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("utype", "");
                editor.putString("name", "");
                editor.putString("mobile", "");
                editor.putString("address", "");
                editor.putString("devId", "");
                editor.commit();
                Intent i = new Intent(ChooseActivity.this, HomeActivity.class);
                startActivity(i); // invoke the SecondActivity.
                finish();
            }
        });
        binding.address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this, UpdateAddress.class);
                startActivity(i); // invoke the SecondActivity.
                finish();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

//        navigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fab.close(true);
//                final Intent intent1 = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?"
//                                + "saddr=" + latitude + "," +longitude + "&daddr=" + sp.getString("dlat","") + "," + sp.getString("dlon","")));
//                intent1.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                startActivity(intent1);
//            }
//        });
//        call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isReadStorageAllowed()) {
//                    fab.close(true);
//                    Intent intent = new Intent(Intent.ACTION_DIAL);
//                    intent.setData(Uri.parse("tel:" + sp.getString("phone", "")));
//                    startActivity(intent);
//                }
//                else{
//                    requestStoragePermission();
//                }
//            }
//        });

    }

    //We are calling this method to check the permission status
    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, CALL_PHONE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CALL_PHONE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {

            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Log.e("denied", permission);
                    // requestStoragePermission();
                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. Please open settings, go to permissions and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }

        } else {
            switch (requestCode) {
                //act according to the request code used while requesting the permission(s).
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);

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
                        if (Hlist.isEmpty()) {

                        } else {
                            try {
                                for (i = 0; i < Hlist.size(); i++) {
                                    if (latitude != null && longitude != null) {
                                        float[] results = new float[1];
                                        Location.distanceBetween(parseDouble(latitude), parseDouble(longitude),
                                                parseDouble(Hlist.get(i).getHlatitude()), parseDouble(Hlist.get(i).getHlongitude()),
                                                results);
                                        float km = results[0] / 1000;
                                        LatLng latLng = new LatLng(Double.parseDouble(Hlist.get(i).getHlatitude()), Double.parseDouble(Hlist.get(i).getHlongitude()));
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                      //  mMap.clear();

                                        mMap.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .title("Hospital Name-\t" + Hlist.get(i).getName() + ":"+Hlist.get(i).getAddress() + "\t:\tDistance from you-" + km+"\t:\t\tHospital Phone-"+Hlist.get(i).getPhone()+"Hospital ID:"+Hlist.get(i).getDevId())
                                                .icon(BitmapDescriptorFactory
                                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();

                                    }
                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        public void onInfoWindowClick(Marker marker) {
                                            SharedPreferences sd=getSharedPreferences("hospital",Context.MODE_PRIVATE);
                                            SharedPreferences.Editor ed=sd.edit();
                                            ed.putString("hname",marker.getTitle());
                                            ed.commit();
                                            startActivity(new Intent(getApplicationContext(), ShowAmbulance.class));
                                            finish();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                            }

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