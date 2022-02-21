package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studyinsta.studyinsta.classes.TestSetsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.studyinsta.studyinsta.classes.DBqueries.keyNameList;
import static com.studyinsta.studyinsta.classes.DBqueries.setTitleList;
import static com.studyinsta.studyinsta.classes.DBqueries.sortedList;


public class TestSetsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String productID, productTitle;
    private Long numberOfSets;
    public static Dialog loadingDialog;


    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference reference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sets);

        recyclerView = findViewById(R.id.setsRV);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Select Set");

        ///loading Dialog
        loadingDialog = new Dialog(TestSetsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        productID = getIntent().getStringExtra("product_ID");
        productTitle = getIntent().getStringExtra("title");


        retrieveTotalSets();


    }

    public void retrieveTotalSets(){

        try {

            setTitleList.clear();
            //problem solved

            reference.child("QuestionsData").child(productID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    for (DataSnapshot shot : snapshot.getChildren()) {
                        setTitleList.add(shot.child("setName").getValue(String.class));
                    }


                    numberOfSets = snapshot.getChildrenCount();
                    setRecyclerView();
                    loadingDialog.dismiss();




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TestSetsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }

            });
        }catch (Exception e){
            finish();
            Toast.makeText(this, "Something Went Wrong! Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    public void setRecyclerView(){


        if (numberOfSets != null){


            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(manager);

            List<String> toFillParameter = new ArrayList<>();

            TestSetsAdapter adapter= new TestSetsAdapter(productID, productTitle, setTitleList, false, toFillParameter);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }else {

            Toast.makeText(this, "There are currently no sets in this test! Please contact Jharkhand Warrior Team.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}