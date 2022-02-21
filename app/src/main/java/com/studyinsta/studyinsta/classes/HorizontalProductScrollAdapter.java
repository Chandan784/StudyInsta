package com.studyinsta.studyinsta.classes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.R;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder viewHolder, int position) {

        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String productId = horizontalProductScrollModelList.get(position).getProductId();

        viewHolder.setData(productId, resource, title, description, price);

    }

    @Override
    public int getItemCount() {

        if (horizontalProductScrollModelList.size() > 10){
            return 10;
        } else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productDescription;
        private TextView productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.hz_scroll_product_image);
            productDescription = itemView.findViewById(R.id.hz_scroll_product_description);
            productTitle = itemView.findViewById(R.id.hz_scroll_product_title);
            productPrice = itemView.findViewById(R.id.hz_scroll_product_price);
        }

        private void setData(final String productId, String resource, String title, String description, String price) {
            Glide.with(itemView.getContext()).load(resource).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(productImage);
            if (!price.equals("")) {
                if (Integer.parseInt(price) == 0) {
                    productPrice.setText("FREE");
                } else {
                    productPrice.setText("Rs. " + price + "/-");
                }
            }else {
                productPrice.setText("");
            }
            productDescription.setText(description);
            productTitle.setText(title);

            if (!title.equals("") && !productId.equals("")) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", productId);
                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            }
        }

        }

    }

