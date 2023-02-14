package com.example.eas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.eas.databinding.ActivityHomeBinding;
import com.example.eas.model.UserModel;
import com.example.eas.settings.LocationMonitoringService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    String mId = "0";
    FirebaseFirestore db;
    String People[] = {"Choose a Category", "User", "Admin", "Hospital", "Ambulance"};
    String item = "0";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                                if(queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("User")) {
                                    Toast.makeText(HomeActivity.this, "Login successfull as user", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(HomeActivity.this, ChooseActivity.class));
                                    finish();
                                }
                                else if(queryDocumentSnapshots.getDocuments().get(0).getString("utype").equals("Admin")) {
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
        sp = getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        if (sp.getString("utype", "").equals("User")) {
            startActivity(new Intent(HomeActivity.this, ChooseActivity.class));
            finish();
        }

    }

    private void userRegistration(String number) {
        UserModel obj = new UserModel(number, "", "", "", "User");
        db = FirebaseFirestore.getInstance();
        db.collection("User").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(HomeActivity.this, "Device Registered successfully", Toast.LENGTH_SHORT).show();
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
