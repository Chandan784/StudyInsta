package com.studyinsta.studyinsta.fragments;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.MyCoursesAdapter;
import com.studyinsta.studyinsta.classes.MyCoursesModel;

import java.util.Comparator;


public class MyCoursesFragment extends Fragment {


    public MyCoursesFragment() {
        // Required empty public constructor
    }

    private RecyclerView mycoursesRecyclerView;
    public static MyCoursesAdapter myCoursesAdapter;
    public static Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_courses, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        mycoursesRecyclerView = view.findViewById(R.id.my_courses_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mycoursesRecyclerView.setLayoutManager(layoutManager);


        if (DBqueries.myCoursesModelList.size() == 0 ){
            DBqueries.purchasedCoursesList.clear();
            DBqueries.loadMyCourses(getContext(), loadingDialog, true);
        }else {
            loadingDialog.dismiss();
        }

        myCoursesAdapter = new MyCoursesAdapter(DBqueries.myCoursesModelList);
        mycoursesRecyclerView.setAdapter(myCoursesAdapter);
        myCoursesAdapter.notifyDataSetChanged();

        return  view;
    }

    public class SortbyProductId implements Comparator<MyCoursesModel>
    {

        @Override
        public int compare(MyCoursesModel a, MyCoursesModel b) {
            return a.getProductId().compareTo(b.getProductId());
        }
    }

}
