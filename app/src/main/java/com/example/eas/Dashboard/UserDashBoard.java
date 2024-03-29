package com.example.eas.Dashboard;

import static android.Manifest.permission.CALL_PHONE;
import static java.lang.Double.parseDouble;

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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.eas.Adapter.BookingAdapter;
import com.example.eas.ChooseActivity;
import com.example.eas.HomeActivity;
import com.example.eas.R;
import com.example.eas.ShowAmbulance;
import com.example.eas.UpdateAddress;
import com.example.eas.databinding.ActivityChooseBinding;
import com.example.eas.databinding.ActivityUserDashBoardBinding;
import com.example.eas.model.Bookingmodel;
import com.example.eas.model.Hospitalmodel;
import com.example.eas.settings.GPSTracker;
import com.example.eas.settings.LocationMonitoringService;
import com.github.clans.fab.FloatingActionButton;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserDashBoard extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
FloatingActionButton address,endride;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUserDashBoardBinding binding;
    GPSTracker gps;
    private GoogleMap mMap;
    List<Bookingmodel> BookingList = new ArrayList();
    private int STORAGE_PERMISSION_CODE = 23;
    boolean somePermissionsForeverDenied = false;
    String latitude, longitude;
    Boolean b = false;
    List<Hospitalmodel> Hlist = new ArrayList();
    RecyclerView rv;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarUserDashBoard.toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDoalog = new ProgressDialog(UserDashBoard.this);
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
        endride=findViewById(R.id.endride);
        rv=findViewById(R.id.rv_bookings);
        LinearLayoutManager ll=new LinearLayoutManager(this);
        rv.setLayoutManager(ll);
        endride.setOnClickListener(new View.OnClickListener() {
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
                Intent i = new Intent(UserDashBoard.this, HomeActivity.class);
                startActivity(i); // invoke the SecondActivity.
                finish();
            }
        });
       address=findViewById(R.id.address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserDashBoard.this, UpdateAddress.class);
                startActivity(i); // invoke the SecondActivity.
                finish();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.name);
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        nav_user.setText(sp.getString("name",""));
        navigationView.setNavigationItemSelectedListener(this);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//
        if (id == R.id.nav_gallery) {
            startActivity(new Intent(getApplicationContext(), UpdateAddress.class));
            finish();
        }
        if (id == R.id.nav_hosp) {
            startActivity(new Intent(getApplicationContext(), UserAllHospital.class));
            finish();
        }
        else if (id == R.id.nav_slideshow) {
            SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("utype", "");
            editor.putString("name", "");
            editor.putString("mobile", "");
            editor.putString("address", "");
            editor.putString("devId", "");
            editor.commit();
            Intent i = new Intent(UserDashBoard.this, HomeActivity.class);
            startActivity(i); // invoke the SecondActivity.
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyBookings();
    }

    private void MyBookings() {
        SharedPreferences sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);

        Log.d("##", sp.getString("docId",""));
        BookingList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Booking").whereEqualTo("uid", sp.getString("docId",""))
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
                            Toast.makeText(UserDashBoard.this, "No Bookings Found", Toast.LENGTH_SHORT).show();
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
                                                .title("Hospital Name:\t" + Hlist.get(i).getName() + ":" + Hlist.get(i).getAddress() + "\t:\t\tHospital Phone-" + Hlist.get(i).getPhone() + "Distance from you:" + km + "Hospital ID:" + Hlist.get(i).getDevId())
                                                .icon(BitmapDescriptorFactory
                                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();


                                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                            public void onInfoWindowClick(Marker marker) {
                                                SharedPreferences sd = getSharedPreferences("hospital", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor ed = sd.edit();
                                                ed.putString("hname", marker.getTitle());
                                                ed.putString("from","home");
                                                ed.commit();
                                                startActivity(new Intent(getApplicationContext(), ShowAmbulance.class));
                                                finish();
                                            }
                                        });
                                    }
                                }
                                } catch(Exception e){
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