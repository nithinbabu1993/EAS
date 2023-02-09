package com.example.eas;

import static android.Manifest.permission.CALL_PHONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eas.settings.GPSTracker;
import com.example.eas.settings.LocationMonitoringService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class ChooseActivity extends FragmentActivity implements OnMapReadyCallback {
GPSTracker gps;
    private GoogleMap mMap;

    private int STORAGE_PERMISSION_CODE = 23;
    boolean somePermissionsForeverDenied = false;
    String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent serviceIntent = new Intent(getApplicationContext(), LocationMonitoringService.class);
//            ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
//
//        } else {
//            Intent intent1 = new Intent(getApplicationContext(), LocationMonitoringService.class);
//            startService(intent1);
//        }
//    }

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
        gps=new GPSTracker(this);
        Toast.makeText(ChooseActivity.this, gps.getLatitude() + "\n"+gps.getLatitude() + "", Toast.LENGTH_SHORT).show();

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
        // Add a marker in Sydney and move the camera
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);
                         Toast.makeText(ChooseActivity.this, latitude + longitude + "", Toast.LENGTH_SHORT).show();
                        if (latitude != null && longitude != null) {
                            LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

//
//                            if (latitude != null && longitude != null) {
//                                float[] results = new float[1];
//                                Location.distanceBetween(Double.parseDouble(latitude), Double.parseDouble(longitude),
//                                        Double.parseDouble(response.body().getLatitude()), Double.parseDouble(response.body().getLongitude()),
//                                        results);
//                                float km = results[0] / 1000;
//                                vkm.setText("Rider is\t" +km + "\tkm Far away");
//
//                            }
//                            LatLng latLng = new LatLng(Double.parseDouble(response.body().getLatitude()), Double.parseDouble(response.body().getLongitude()));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
//                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions()
//                                    .position(latLng)
//                                    .title("Rider is Here")
//                                    .icon(BitmapDescriptorFactory
//                                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
//                        }
//                    }


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
        switch (id){
            case R.id.item1:
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
                return true;
           default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        finish();
    }
}