package com.example.eas.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eas.AddAmbulance;
import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.databinding.LayoutHospitalBinding;
import com.example.eas.model.AmbulanceModel;
import com.example.eas.model.Bookingmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceAdapter.MyviewHolder> {
    public List<AmbulanceModel> HospList;
    Boolean b1 = false;
    LayoutHospitalBinding binding;
    public String uid = "";
    public String HospitalId = "";
    public String Hname = "";
    public String uname = "";
    public String uaddress = "";
    public String uphone = "";


    @NonNull
    @Override
    public AmbulanceAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutHospitalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AmbulanceAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull AmbulanceAdapter.MyviewHolder holder, int position) {
        AmbulanceModel dm = HospList.get(position);
        holder.aname.setText(dm.getAname());
        holder.dname.setText(dm.getName());
        holder.dphone.setText(dm.getPhone());
        holder.labelname.setText("Ambulance name :\t");
        holder.labelphone.setText("driver Phone \t\t\t\t\t:\t");
        holder.labeladdress.setText("driver name \t\t\t\t\t\t:\t");
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = view.getRootView().getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                if (sp.getString("utype", "").equals("Hospital")) {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("Do you really wants to Delete this Ambulance?");
                    alertbox.setTitle("Delete!!");

                    alertbox.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deleteDepartment(dm.getDevId(), view, holder.getAdapterPosition());

                        }
                    });
                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertbox.show();
                } else {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("Do you  wants to book this Ambulance?");
                    alertbox.setTitle("book!!");

                    alertbox.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                          CheckBookingAvailability(dm.getDevId(), view, holder.getAdapterPosition(),dm);


                        }
                    });
                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertbox.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return HospList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView aname, dname, dphone, labelname, labelphone, labeladdress;
        ConstraintLayout root;

        public MyviewHolder(@NonNull LayoutHospitalBinding binding) {
            super(binding.getRoot());
            aname = binding.tvPatientName;
            root = binding.root;
            dname = binding.tvPatientAddress;
            dphone = binding.tvPatientPhone;
            labelname = binding.labelName;
            labelphone = binding.labelPhone;
            labeladdress = binding.labelAddress;
        }
    }

    private void deleteDepartment(String doc_name, View view, int adapterPosition) {
        //Log.d("@", "showData: Called")

        final ProgressDialog progressDoalog = new ProgressDialog(view.getRootView().getContext());
        progressDoalog.setMessage("Loading....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(adapterPosition);
                        Intent i = new Intent(view.getRootView().getContext(), AddAmbulance.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getRootView().getContext().startActivity(i);
                        Toast.makeText(view.getRootView().getContext(), " Ambulance removed successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getRootView().getContext(), "Technical error occured", Toast.LENGTH_SHORT).show();

                    }
                });

        progressDoalog.dismiss();

    }

    private void bookAmbulance(String ambulanceID, View view, int adapterPosition, String aname, String dname, String dphone) {
        final ProgressDialog progressDoalog = new ProgressDialog(view.getRootView().getContext());
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Bookingmodel obj = new Bookingmodel(uid,
                ambulanceID,
                HospitalId,
                uname,
                uaddress,
                uphone,
                Bookingmodel.latitude, Bookingmodel.longitude,
                aname, dname, dphone, formattedDate,"","",Hname);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Booking").add(obj).
                addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(view.getRootView().getContext(), "Ambulance booked successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(view.getRootView().getContext(), UserDashBoard.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getRootView().getContext().startActivity(i);

                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getRootView().getContext(), "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
        progressDoalog.dismiss();
    }

    private void CheckBookingAvailability(String ambId, View view, int adapterPosition, AmbulanceModel dm) {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ProgressDialog progressDoalog = new ProgressDialog(view.getRootView().getContext());
        progressDoalog.setMessage("Checking....");
        progressDoalog.setTitle("Please wait");
        progressDoalog.setCancelable(false);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        db.collection("Booking").whereEqualTo("bdate", formattedDate)
                .whereEqualTo("ambulanceId", ambId)
                .whereEqualTo("uid", uid).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.getDocuments().isEmpty()) {
                            bookAmbulance(dm.getDevId(), view, adapterPosition, dm.getAname(), dm.getName(), dm.getPhone());
                            progressDoalog.dismiss();

                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(view.getRootView().getContext(), "This Ambulance Already booked for todays date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //userRegistration();
                        Toast.makeText(view.getRootView().getContext(), "Creation failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}



