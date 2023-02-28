package com.example.eas;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.SEND_SMS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.databinding.ActivityHomeBinding;
import com.example.eas.model.UserModel;
import com.example.eas.settings.LocationMonitoringService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    String mId = "0";
    FirebaseFirestore db;
    String People[] = {"Choose a Category", "User", "Admin", "Hospital", "Ambulance"};
    String item = "0";
    SharedPreferences sp;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CALL_PHONE, SEND_SMS}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean callphone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean sms = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && callphone && sms)
                        Toast.makeText(this, "Permission Granted, Now you can access location data and sms,call phone.", Toast.LENGTH_SHORT).show();

                    else {
                        Toast.makeText(this, "Permission Denied, You cannot access location data and sms,call phone.", Toast.LENGTH_SHORT).show();
                        showSettingsDialog();

                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //  requestPermission();
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
                    binding.img.setVisibility(View.GONE);
                } else if (item.equals("Choose a Category")) {

                } else {
                    binding.textView2.setVisibility(View.VISIBLE);
                    binding.regDevId.setVisibility(View.VISIBLE);
                    binding.loginpin.setVisibility(View.GONE);
                    binding.img.setVisibility(View.VISIBLE);
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
                callLoginFun(mId);
            }
        });
        binding.loginpin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    callLoginFun(binding.loginpin.getText().toString());
                    binding.loginpin.setText("");
                }

            }
        });
    }


    private void callLoginFun(String DevId) {
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").whereEqualTo("devId", DevId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                                progressDoalog.dismiss();
                                if (item.equals("User"))
                                    Toast.makeText(HomeActivity.this, "Device Id not Registered", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(HomeActivity.this, "Login Pin not Registered", Toast.LENGTH_SHORT).show();
                            } else {

                                sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("utype", queryDocumentSnapshots.getDocuments().get(0).getString("utype"));
                                editor.putString("docId", queryDocumentSnapshots.getDocuments().get(0).getId());
                                editor.putString("name", queryDocumentSnapshots.getDocuments().get(0).getString("name"));
                                editor.putString("mobile", queryDocumentSnapshots.getDocuments().get(0).getString("phone"));
                                editor.putString("address", queryDocumentSnapshots.getDocuments().get(0).getString("address"));
                                editor.putString("devId", queryDocumentSnapshots.getDocuments().get(0).getString("devId"));
                                editor.commit();
                                progressDoalog.dismiss();
                                if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("User")) {
                                    Toast.makeText(HomeActivity.this, "Login successfull as user", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, UserDashBoard.class));
                                    finish();
                                } else if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Admin")) {
                                    Toast.makeText(HomeActivity.this, "Login successfull as admin", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, AdminHome.class));
                                    finish();
                                } else if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Hospital")) {
                                    Toast.makeText(HomeActivity.this, "Login successfull as admin", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, HospitalHome.class));
                                    finish();
                                } else if (queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Ambulance")) {
                                    Toast.makeText(HomeActivity.this, "Login successfull as admin", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, AdminHome.class));
                                    finish();
                                }
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
        final ProgressDialog progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("User").whereEqualTo("devId", mId).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            userRegistration(mId);
                            progressDoalog.dismiss();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(HomeActivity.this, "This Device Already registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(HomeActivity.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission()) {
            requestPermission();
        } else {
            // Toast.makeText(this, "Permission already granted.", Toast.LENGTH_SHORT).show();
        }
        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        if (sp.getString("utype", "").equals("User")) {
            startActivity(new Intent(HomeActivity.this, UserDashBoard.class));
            finish();
        }

    }

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void userRegistration(String number) {
        UserModel obj = new UserModel(number, "", "", "", "User");
        db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this, "Device Registered successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);

    }

}
