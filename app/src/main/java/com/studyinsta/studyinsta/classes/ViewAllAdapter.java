package com.studyinsta.studyinsta.classes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.R;

import java.util.List;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    private List<ViewAllModel> viewAllModelList;

    public ViewAllAdapter(List<ViewAllModel> viewAllModelList) {
        this.viewAllModelList = viewAllModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewall_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String productId = viewAllModelList.get(position).getProductId();
        String resource = viewAllModelList.get(position).getProductImage();
        String title = viewAllModelList.get(position).getProductTitle();
        String rating = viewAllModelList.get(position).getRating();
        long totalRatings = viewAllModelList.get(position).getTotalRatings();
        String productPrice = viewAllModelList.get(position).getProductPrice();
        String cuttedPrice = viewAllModelList.get(position).getCuttedPrice();
        String description = viewAllModelList.get(position).getDescription();

        viewHolder.setData(productId, resource, title, productPrice, cuttedPrice, rating, totalRatings, description );

    }

    @Override
    public int getItemCount() {
        if (viewAllModelList != null) {
            return viewAllModelList.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethodProductAvailability;
        private TextView ratings;
        private TextView totalRatings;
        private View priceCut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            paymentMethodProductAvailability = itemView.findViewById(R.id.payment_method_product_availability);
            ratings = itemView.findViewById(R.id.tv_product_rating_miniview);
            totalRatings = itemView.findViewById(R.id.total_ratings);
            priceCut = itemView.findViewById(R.id.price_cut);
        }
        private void setData(final String productId, String resource, String title, String price, String cuttedprice, String averageRate, long totalRatingsNo,String description){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.loading_icon)).into(productImage);
            productTitle.setText(title);
            cuttedPrice.setText("Rs. " + cuttedprice + "/-");
            ratings.setText(averageRate);
            totalRatings.setText("("+totalRatingsNo + ") ratings");
            paymentMethodProductAvailability.setText(description);
            if (!price.equals("")) {
                if (Integer.parseInt(price) == 0) {
                    productPrice.setText("FREE");
                } else {
                    productPrice.setText("Rs. " + price + "/-");
                }
            }else {
                productPrice.setText("Not Available!");
            }

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
