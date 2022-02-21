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

public class MyTestsFragment extends Fragment {

    public MyTestsFragment() {
        // Required empty public constructor
    }
    private RecyclerView myTestsRecyclerView;
    public static MyCoursesAdapter myTestsCoursesAdapter;
    public static Dialog loadingDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_tests, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        myTestsRecyclerView = view.findViewById(R.id.my_tests_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myTestsRecyclerView.setLayoutManager(layoutManager);

        if (DBqueries.myTestsCoursesModelList.size() == 0 ){
            DBqueries.purchasedTestsList.clear();
            DBqueries.loadMyTests(getContext(), loadingDialog, true);
        }else {
            loadingDialog.dismiss();
        }

        myTestsCoursesAdapter = new MyCoursesAdapter(DBqueries.myTestsCoursesModelList);
        myTestsRecyclerView.setAdapter(myTestsCoursesAdapter);
        myTestsCoursesAdapter.notifyDataSetChanged();

        return view;
    }

}