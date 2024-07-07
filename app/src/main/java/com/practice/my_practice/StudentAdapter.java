package com.practice.my_practice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapter extends FirebaseRecyclerAdapter<StudentDetails,StudentAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StudentAdapter(@NonNull FirebaseRecyclerOptions<StudentDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull StudentDetails model) {
        // Bind student details to views
        holder.studName.setText(model.getName());
        holder.degreeName.setText(model.getDegree());
        holder.yearLvl.setText(model.getLevel());
        holder.studEmail.setText(model.getEmail());

        // Load student profile image using Glide
        Glide.with(holder.profile.getContext())
                .load(model.getUrl())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.profile);

        // Handle edit button click
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show update dialog using DialogPlus
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.profile.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_details))
                        .setExpanded(true, 1200)
                        .create();

                // Initialize views inside the dialog
                View view = dialogPlus.getHolderView();
                EditText name = view.findViewById(R.id.Name_et);
                EditText degree = view.findViewById(R.id.Degree_et);
                EditText email = view.findViewById(R.id.Email_et);
                EditText imageurl = view.findViewById(R.id.url_et);
                EditText year = view.findViewById(R.id.level_et);
                AppCompatButton updateBtn = view.findViewById(R.id.update_btn);

                // Set initial values from model to dialog fields
                name.setText(model.getName());
                degree.setText(model.getDegree());
                email.setText(model.getEmail());
                imageurl.setText(model.getUrl());
                year.setText(model.getLevel());

                dialogPlus.show();

                // Handle update button click inside the dialog
                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get updated values from dialog fields
                        String newName = name.getText().toString();
                        String newDegree = degree.getText().toString();
                        String newEmail = email.getText().toString();
                        String newImageUrl = imageurl.getText().toString();
                        String newYear = year.getText().toString();

                        // Update Firebase database
                        Map<String, Object> map = new HashMap<>();
                        map.put("Name", newName);
                        map.put("Degree", newDegree);
                        map.put("Email", newEmail);
                        map.put("url", newImageUrl);
                        map.put("Level", newYear);

                        // Get adapter position to update the correct student
                        int adapterPosition = holder.getAbsoluteAdapterPosition();
                        FirebaseDatabase.getInstance().getReference()
                                .child("Students")
                                .child(Objects.requireNonNull(getRef(adapterPosition).getKey()))
                                .updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.studName.getContext(), "Update Was Successful", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss(); // Dismiss dialog on success
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.studName.getContext(), "Update Was Unsuccessful", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss(); // Dismiss dialog on failure
                                    }
                                });
                    }
                });
            }
        });

        // Handle delete button click
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show delete confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.studName.getContext());
                builder.setTitle("Are you sure you want to delete");
                builder.setMessage("Deleted data cannot be restored");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get adapter position to delete the correct student
                        int adapterPosition = holder.getAbsoluteAdapterPosition();
                        String studentKey = getRef(adapterPosition).getKey();

                        if (studentKey != null) {
                            // Remove student from Firebase
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Students")
                                    .child(studentKey)
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(holder.studName.getContext(), "Student deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.studName.getContext(), "Failed to delete student", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(holder.studName.getContext(), "Failed to delete. Student key not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.studName.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                builder.show(); // Show the dialog
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
      CircleImageView profile;
      TextView studName,degreeName, yearLvl,studEmail;
      Button edit, delete;




      public myViewHolder(@NonNull View itemView) {
          super(itemView);
          profile = (CircleImageView)itemView.findViewById(R.id.profile_iv);
          studName = (TextView) itemView.findViewById(R.id.stud_Name);
          degreeName = (TextView) itemView.findViewById(R.id.degree_Name);
          yearLvl = (TextView) itemView.findViewById(R.id.level);
          studEmail = (TextView) itemView.findViewById(R.id.email_tv);
          edit = (Button) itemView.findViewById(R.id.edit_btn);
          delete = (Button) itemView.findViewById(R.id.delete_btn);

      }
  }
}
