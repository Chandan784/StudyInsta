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
import com.studyinsta.studyinsta.classes.CommonPurchasedListAdapter;
import com.studyinsta.studyinsta.classes.DBqueries;

public class MyNotesFragment extends Fragment {


    public MyNotesFragment() {
        // Required empty public constructor
    }
    private RecyclerView myNotesRecyclerView;
//    public static MyCoursesAdapter myNotesCoursesAdapter;
    public static CommonPurchasedListAdapter adapter;
    public static Dialog loadingDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_notes, container, false);

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

        if (DBqueries.individualNotesList.size() == 0 ){
            DBqueries.purchasedNotesList.clear();
            DBqueries.loadMyNotes(getContext(), loadingDialog, true);
        }else {
            loadingDialog.dismiss();
        }

        adapter = new CommonPurchasedListAdapter(DBqueries.individualNotesList);
        myNotesRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
}