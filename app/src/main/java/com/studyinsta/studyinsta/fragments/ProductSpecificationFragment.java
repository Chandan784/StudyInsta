package com.studyinsta.studyinsta.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.ProductSpecificationTeachersAdapter;
import com.studyinsta.studyinsta.classes.ProductSpecificationTeachersModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSpecificationFragment extends Fragment {


    public ProductSpecificationFragment() {
        // Required empty public constructor
    }

    private RecyclerView productSpecificationRecyclerView;
    public List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecyclerView = view.findViewById(R.id.product_specification_teachers_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        productSpecificationRecyclerView.setLayoutManager(linearLayoutManager);

        //Dummy List


//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(0, "Teachers Details"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(0, "Others"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));
//        productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, "Algebra", "Ramdev Sir"));

        //End Dummy List

        ProductSpecificationTeachersAdapter productSpecificationTeachersAdapter = new ProductSpecificationTeachersAdapter(productSpecificationTeachersModelList);
        productSpecificationRecyclerView.setAdapter(productSpecificationTeachersAdapter);
        productSpecificationTeachersAdapter.notifyDataSetChanged();

        return view;
    }

}
