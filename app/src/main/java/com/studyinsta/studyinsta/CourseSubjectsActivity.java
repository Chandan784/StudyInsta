package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.ViewGroup;

import com.studyinsta.studyinsta.classes.DBqueries;


public class CourseSubjectsActivity extends AppCompatActivity {

    private RecyclerView courseSubjectsRecyclerView;
    public static Dialog loadingDialog;

    public static int LECTURE = 1;
    public static int NOTES = 2;
    public static int TEST = 3;

    public static String productId;
    public static String courseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_subjects);

        Toolbar toolbar = findViewById(R.id.courseSubjectToolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        productId = intent.getStringExtra("product_ID");
        courseTitle = intent.getStringExtra("product_TITLE");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(courseTitle);

        ///loading Dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        courseSubjectsRecyclerView = findViewById(R.id.course_subjects_recyclerview);

        int noOfcolumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfcolumns);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        courseSubjectsRecyclerView.setLayoutManager(layoutManager);

        DBqueries.loadSubjects(courseSubjectsRecyclerView, CourseSubjectsActivity.this, loadingDialog, productId);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}