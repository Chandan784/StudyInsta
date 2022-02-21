package com.studyinsta.studyinsta.classes;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.R;

import java.util.List;

public class GridProductLayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @Override
    public int getCount() {
        return horizontalProductScrollModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!horizontalProductScrollModelList.get(position).getProductId().equals("")) {
                        Intent productDetailsIntent = new Intent(parent.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", horizontalProductScrollModelList.get(position).getProductId());
                        parent.getContext().startActivity(productDetailsIntent);
                    }
                }
            });

            ImageView productImage = view.findViewById(R.id.hz_scroll_product_image);
            TextView productTitle = view.findViewById(R.id.hz_scroll_product_title);
            TextView productDescription = view.findViewById(R.id.hz_scroll_product_description);
            TextView productPrice = view.findViewById(R.id.hz_scroll_product_price);

            Glide.with(parent.getContext()).load(horizontalProductScrollModelList.get(position).getProductImage()).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(productImage);

            productTitle.setText(horizontalProductScrollModelList.get(position).getProductTitle());
            productDescription.setText(horizontalProductScrollModelList.get(position).getProductDescription());
            if (!horizontalProductScrollModelList.get(position).getProductPrice().equals("")) {
                if (Integer.parseInt(horizontalProductScrollModelList.get(position).getProductPrice()) == 0) {
                    productPrice.setText("FREE");

                } else {
                    productPrice.setText("Rs. " + horizontalProductScrollModelList.get(position).getProductPrice() + "/-");

                }
            }else {
                productPrice.setText("");
            }
        }else {

            view = convertView;

        }
        return view;
    }
}
