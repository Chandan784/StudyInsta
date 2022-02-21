package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.studyinsta.studyinsta.classes.TabbedViewPagerAdapter;
import com.studyinsta.studyinsta.fragments.CourseLecturesFragment;
import com.studyinsta.studyinsta.fragments.CourseNotesFragment;
import com.studyinsta.studyinsta.fragments.CourseTestsFragment;

public class CourseMaterialActivity extends AppCompatActivity {

    public static Dialog loadingDialog;

    private TabbedViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_material);

        Toolbar toolbar = findViewById(R.id.courseMaterialToolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String titleSubName =intent.getStringExtra("subject_name");
        getSupportActionBar().setTitle(titleSubName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.courseMaterialTabLayout);
        viewPager = findViewById(R.id.courseMaterialViewPager);

        viewPagerAdapter = new TabbedViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new CourseLecturesFragment(), "Lectures");
        viewPagerAdapter.addFragment(new CourseNotesFragment(), "Notes");
        viewPagerAdapter.addFragment(new CourseTestsFragment(), "Tests");

        viewPager.setAdapter(viewPagerAdapter);
        //to stop reloading the fragments on swipe action (tell the view pager how many fragments do you have)
        //following is the code.
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

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