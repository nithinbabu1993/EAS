package com.example.eas.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eas.Dashboard.UserDashBoard;
import com.example.eas.HospitalList;
import com.example.eas.R;
import com.example.eas.databinding.LayoutBookingBinding;
import com.example.eas.databinding.LayoutHospitalBinding;
import com.example.eas.model.Bookingmodel;
import com.example.eas.model.Hospitalmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyviewHolder> {
    public List<Bookingmodel> bookingList;
    LayoutBookingBinding binding;

    @NonNull
    @Override
    public BookingAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new BookingAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.MyviewHolder holder, int position) {
        Bookingmodel dm = bookingList.get(position);
        holder.hname.setText(dm.getAmbNo());
        holder.haddress.setText(dm.getDriverPhone());
        holder.hphone.setText(dm.getDriverName());
        holder.pname.setText(dm.getUname());
        holder.pphone.setText(dm.getUphone());
        holder.hospname.setText(dm.getHname());
        if(dm.getDlatitude()!="" && dm.getDlongitude()!=""){
            holder.dlatlang.setText("Ambulance own its Way");

        }else{
            holder.dlatlang.setText("Ambulance not Started");

        }


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = view.getRootView().getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
                if (sp.getString("utype", "").equals("User")) {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                    alertbox.setMessage("Do you really wants to Delete this Booking?");
                    alertbox.setTitle("Delete!!");

                    alertbox.setPositiveButton("cancel booking", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteDepartment(dm.getUid(), view, holder.getAdapterPosition());

                        }
                    });
                    alertbox.setNegativeButton("call driver", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + dm.getDriverPhone()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            view.getRootView().getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                    alertbox.setNeutralButton("Track Ambulance", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertbox.show();
                } else {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView hname, haddress, hphone, dlatlang,pname,pphone,hospname;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutBookingBinding binding) {
            super(binding.getRoot());
            hname = binding.tvPatientName;
            hospname = binding.tvHname;
            dlatlang = binding.tvloc;
            pname = binding.tvpname;
            pphone = binding.tvpPhone;
            root = binding.root;
            haddress = binding.tvPatientAddress;
            hphone = binding.tvPatientPhone;
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
        db.collection("Booking").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(adapterPosition);
                        Intent i = new Intent(view.getRootView().getContext(), UserDashBoard.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getRootView().getContext().startActivity(i);
                        Toast.makeText(view.getRootView().getContext(), " Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getRootView().getContext(), "Technical error occured", Toast.LENGTH_SHORT).show();

                    }
                });

        progressDoalog.dismiss();

    }

}


