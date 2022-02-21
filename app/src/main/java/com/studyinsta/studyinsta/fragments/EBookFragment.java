package com.studyinsta.studyinsta.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.MyCoursesAdapter;


public class EBookFragment extends Fragment {


    private Dialog loadingDialog;
    private RecyclerView myNotesRecyclerView;
    private MyCoursesAdapter adapter;

    public EBookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e_book, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        myNotesRecyclerView = view.findViewById(R.id.my_notes_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myNotesRecyclerView.setLayoutManager(layoutManager);

        if (DBqueries.myDownloadCoursesModelList.size() == 0 ){
            DBqueries.purchasedDownloadList.clear();
            DBqueries.loadMyDownload(getContext(), loadingDialog, myNotesRecyclerView);
        }else {
            loadingDialog.dismiss();
        }



        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}