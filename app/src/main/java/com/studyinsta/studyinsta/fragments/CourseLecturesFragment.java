package com.studyinsta.studyinsta.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.studyinsta.studyinsta.CourseSubjectsActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.DBqueries;


public class CourseLecturesFragment extends Fragment {


    public CourseLecturesFragment() {
        // Required empty public constructor
    }
    public static Dialog loadingDialog;
    public static String subjectNameCourse, prodId;
    public static RecyclerView lecturesRecyclerView;
    public static TextView moreTv, noMat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_lectures, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        Intent intent = getActivity().getIntent();
        subjectNameCourse = intent.getStringExtra("subject_name");

        lecturesRecyclerView = view.findViewById(R.id.course_lecture_recyclerview);
        moreTv = view.findViewById(R.id.moreLecturesTv);
        noMat = view.findViewById(R.id.noMatLecturesTv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lecturesRecyclerView.setLayoutManager(layoutManager);

        if (CourseSubjectsActivity.productId != null) {
            DBqueries.loadLectures(getContext(), loadingDialog, lecturesRecyclerView, CourseSubjectsActivity.productId, subjectNameCourse, moreTv, noMat);
        }else {
            Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
        return view;
    }
}