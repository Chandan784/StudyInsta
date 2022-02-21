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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studyinsta.studyinsta.DeliveryActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.CartAdapter;
import com.studyinsta.studyinsta.classes.CartItemModel;
import com.studyinsta.studyinsta.classes.DBqueries;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    private Button continueBtn;
    public static Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    private TextView totalAmount;
    public static boolean continueClickedFromMyCartFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        ///loading Dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recyclerview);
        continueBtn = view.findViewById(R.id.cart_continue_button);
        totalAmount = view.findViewById(R.id.delivery_total_amount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cartItemsRecyclerView.setLayoutManager(layoutManager);

        if (DBqueries.cartItemModelList.size() == 0 ){
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalAmount);
        }else {
            if (DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size() -1).getType()  == CartItemModel.TOTAL_AMOUNT){
                LinearLayout parent = (LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

        cartAdapter = new CartAdapter(DBqueries.cartItemModelList, totalAmount, true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClickedFromMyCartFragment = true;
                loadingDialog.show();
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                for (int x = 0; x < DBqueries.cartItemModelList.size(); x++){
                    CartItemModel cartItemModel = DBqueries.cartItemModelList.get(x);
                    DeliveryActivity.cartItemModelList.add(cartItemModel);
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                getContext().startActivity(deliveryIntent);
            }
        });
        return view;
    }

}
