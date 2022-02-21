package com.studyinsta.studyinsta.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.MyOrderAdapter;
import com.studyinsta.studyinsta.classes.MyOrdersItemModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {


    public MyOrdersFragment() {
        // Required empty public constructor
    }

    private RecyclerView myOrdersRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        myOrdersRecyclerView = view.findViewById(R.id.my_orders_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        myOrdersRecyclerView.setLayoutManager(layoutManager);


        /// In rating, 2 means 3 stars and 4 means 5 stars like this

        List<MyOrdersItemModel> myOrdersItemModelList = new ArrayList<>();
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.forgot_pass_image,2,"JSSC CGL 202","Your order is succesful"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.dustbin_icon,1,"JSSC CGL 202","Cancelled"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.forgot_pass_image,2,"JSSC CGL 202","Your order is succesful"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.dustbin_icon,3,"JSSC CGL 202","Cancelled"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.forgot_pass_image,2,"JSSC CGL 202","Your order is succesful"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.dustbin_icon,2,"JSSC CGL 202","Your order is succesful"));
        myOrdersItemModelList.add(new MyOrdersItemModel(R.drawable.dustbin_icon,4,"JSSC CGL 202","Your order is succesful"));

        MyOrderAdapter myOrderAdapter = new MyOrderAdapter(myOrdersItemModelList);
        myOrdersRecyclerView.setAdapter(myOrderAdapter);
        myOrderAdapter.notifyDataSetChanged();

        return view;
    }

}
