package com.studyinsta.studyinsta.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.R;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteButton;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteButton) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteButton = showDeleteButton;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
                return new cartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_total_amount_layout, viewGroup, false);
                return new cartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productId = cartItemModelList.get(position).getProductId();
                String resource = cartItemModelList.get(position).getProductImage();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String title = cartItemModelList.get(position).getProductTitle();

                ((cartItemViewHolder)viewHolder).setItemDetails(productId, resource,title,productPrice,cuttedPrice, position);
                break;
            case CartItemModel.TOTAL_AMOUNT:

                int totalItems = 0;
                int totalItemPrice = 0;
                String taxPrice;
                int totalAmount;
                int savedAmount = 0;
                for (int x = 0; x < cartItemModelList.size(); x++ ){
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM){
                        totalItems++;
                        totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice());
                        savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - totalItemPrice);
                    }
                }
                taxPrice = "inclusive of taxes.";
                totalAmount = totalItemPrice;

                ((cartTotalAmountViewHolder)viewHolder).setTotalAmount(totalItems,totalItemPrice,taxPrice,totalAmount,savedAmount);
                break;
            default:
                return;
        }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }


    @Override
    public int getItemCount() {
        if (cartItemModelList!=null) {
            return cartItemModelList.size();
        }else {
            return 0;
        }
    }

    class cartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView cuttedPrice;
//        private LinearLayout deleteButton;

        public cartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productPrice = itemView.findViewById(R.id.product_price);
            productTitle = itemView.findViewById(R.id.product_title);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
//            deleteButton = itemView.findViewById(R.id.remove_item_btn);

        }

        private void setItemDetails(String productId, String resource, String title, String price, String cuttedPriceRate, final int position) {
            Glide.with(itemView.getContext()).load(resource).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(productImage);
            productTitle.setText(title);
            productPrice.setText("Rs. " + price + "/-");
            cuttedPrice.setText("Rs. " + cuttedPriceRate + "/-");

//            if (showDeleteButton){
//                deleteButton.setVisibility(View.VISIBLE);
//            }else {
//                deleteButton.setVisibility(View.GONE);
//            }
//            deleteButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!ProductDetailsActivity.running_cart_query){
//                        ProductDetailsActivity.running_cart_query = true;
//                        DBqueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
//                    }
//                }
//            });
        }
    }

    class cartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView taxPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public cartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            taxPrice = itemView.findViewById(R.id.tax_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);

        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String taxPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price (" + totalItemText + " items)");
            totalItemPrice.setText("Rs. " + totalItemPriceText + "/-");
            taxPrice.setText(taxPriceText);
            totalAmount.setText("Rs. " + totalAmountText +"/-");
            cartTotalAmount.setText("Rs. " + totalAmountText +"/-");
            savedAmount.setText("You Saved Rs. " + savedAmountText +"/-");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemPriceText == 0 && DBqueries.cartItemModelList.size() > 0){
                DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                parent.setVisibility(View.GONE);
            }else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }

}
