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

import com.studyinsta.studyinsta.CourseSubjectsActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.DBqueries;


public class CourseNotesFragment extends Fragment {

    public CourseNotesFragment() {
        // Required empty public constructor
    }

    public static Dialog loadingDialog;
    public static String subjectNameNotes;
    public static RecyclerView courseNotesRecyclerview;
    public static TextView moreTv, noMat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_notes, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        Intent intent = getActivity().getIntent();
        subjectNameNotes = intent.getStringExtra("subject_name");

        courseNotesRecyclerview = view.findViewById(R.id.course_notes_recyclerview);
        moreTv = view.findViewById(R.id.moreNotesTv);
        noMat = view.findViewById(R.id.noMatNotesTv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        courseNotesRecyclerview.setLayoutManager(layoutManager);

        DBqueries.loadCourseNotes(getContext(), loadingDialog, courseNotesRecyclerview, CourseSubjectsActivity.productId, subjectNameNotes, moreTv, noMat);

        return view;
    }
}