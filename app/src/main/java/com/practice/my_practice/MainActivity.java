package com.practice.my_practice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.function.IntFunction;

public class MainActivity extends AppCompatActivity {

    RecyclerView studRv;
    StudentAdapter studAdapter;
    FloatingActionButton floatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatBtn = findViewById(R.id.add_ftb);
        studRv = findViewById(R.id.student_rv);
        studRv.setLayoutManager(new LinearLayoutManager(this));

        if (floatBtn != null) {
            floatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Add_Student.class);
                    startActivity(intent);
                }
            });
        } else {
            // Handle case where FloatingActionButton is not found
            Log.e("MainActivity", "FloatingActionButton not found");
        }

        // Setup FirebaseRecyclerOptions
        FirebaseRecyclerOptions<StudentDetails> options =
                new FirebaseRecyclerOptions.Builder<StudentDetails>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Students"), StudentDetails.class)
                        .build();

        // Initialize StudentAdapter
        studAdapter = new StudentAdapter(options);
        studRv.setAdapter(studAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        studAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        studAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) item.getActionView();

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDatabase(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchDatabase(newText);
                return false;
            }
        });

        return true;
    }

    private void searchDatabase(String searchText) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Students")
                .orderByChild("Name")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<StudentDetails> options =
                new FirebaseRecyclerOptions.Builder<StudentDetails>()
                        .setQuery(query, StudentDetails.class)
                        .build();

        studAdapter.updateOptions(options);
    }
}
