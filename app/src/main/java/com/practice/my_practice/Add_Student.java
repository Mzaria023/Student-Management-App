package com.practice.my_practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Add_Student extends AppCompatActivity {
    EditText name, degree, email, level, url;
    Button add, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        name = findViewById(R.id.Name_et);
        degree = findViewById(R.id.Degree_et);
        email = findViewById(R.id.Email_et);
        level = findViewById(R.id.level_et);
        url = findViewById(R.id.url_et);
        add = findViewById(R.id.add_btn);
        back = findViewById(R.id.back_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                finish(); // Finish current activity to go back to previous one
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveData();
                    clearTxt();
                }
            }
        });
    }

    private boolean validateInputs() {
        String studentName = name.getText().toString().trim();
        String studentDegree = degree.getText().toString().trim();
        String studentEmail = email.getText().toString().trim();
        String studentLevel = level.getText().toString().trim();
        String studentUrl = url.getText().toString().trim();

        if (studentName.isEmpty() || studentDegree.isEmpty() || studentEmail.isEmpty() ||
                studentLevel.isEmpty() || studentUrl.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void saveData() {
        String studentName = name.getText().toString();
        String studentDegree = degree.getText().toString();
        String studentEmail = email.getText().toString();
        String studentLevel = level.getText().toString();
        String studentUrl = url.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("Degree", studentDegree);
        map.put("Email", studentEmail);
        map.put("Name", studentName);
        map.put("Level", studentLevel);
        map.put("url", studentUrl);

        FirebaseDatabase.getInstance().getReference().child("Students").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Add_Student.this, "Student Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Student.this, "Error Occurred, Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void clearTxt() {
        name.setText("");
        degree.setText("");
        email.setText("");
        level.setText("");
        url.setText("");
    }
}
