package com.example.eas.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eas.HospitalList;
import com.example.eas.databinding.LayoutHospitalBinding;
import com.example.eas.model.Hospitalmodel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HlistAdapter extends RecyclerView.Adapter<HlistAdapter.MyviewHolder> {
    public List<Hospitalmodel> HospList;
    LayoutHospitalBinding binding;

    @NonNull
    @Override
    public HlistAdapter.MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = LayoutHospitalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new HlistAdapter.MyviewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull HlistAdapter.MyviewHolder holder, int position) {
        Hospitalmodel dm = HospList.get(position);
        holder.hname.setText(dm.getName());
        holder.haddress.setText(dm.getAddress());
        holder.hphone.setText(dm.getPhone());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
                alertbox.setMessage("Do you really wants to Delete this Hospital?");
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
            }
        });
    }

    @Override
    public int getItemCount() {
        return HospList.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView hname, haddress, hphone;
        ConstraintLayout root;
        ImageView ddelete;

        public MyviewHolder(@NonNull LayoutHospitalBinding binding) {
            super(binding.getRoot());
            hname = binding.tvPatientName;
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
        db.collection("User").document(doc_name).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(adapterPosition);
                        Intent i = new Intent(view.getRootView().getContext(), HospitalList.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        view.getRootView().getContext().startActivity(i);
                        Toast.makeText(view.getRootView().getContext(), " Hospital removed successfully", Toast.LENGTH_SHORT).show();
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


