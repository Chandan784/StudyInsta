package com.studyinsta.studyinsta.classes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studyinsta.studyinsta.R;

import java.util.List;

public class ProductSpecificationTeachersAdapter extends RecyclerView.Adapter<ProductSpecificationTeachersAdapter.ViewHolder> {

    private List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList;

    public ProductSpecificationTeachersAdapter(List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList) {
        this.productSpecificationTeachersModelList = productSpecificationTeachersModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (productSpecificationTeachersModelList.get(position).getType()) {
            case 0:
                return ProductSpecificationTeachersModel.SPECIFICATION_TITLE;
            case 1:
                return ProductSpecificationTeachersModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public ProductSpecificationTeachersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case ProductSpecificationTeachersModel.SPECIFICATION_TITLE:
                TextView title = new TextView(viewGroup.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //Settings Margins for TextView
                layoutParams.setMargins(setDp(16, viewGroup.getContext())
                        , setDp(16, viewGroup.getContext())
                        , setDp(16, viewGroup.getContext())
                        , setDp(16, viewGroup.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);

            case ProductSpecificationTeachersModel.SPECIFICATION_BODY:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_specification_teachers_item_layout, viewGroup, false);
                return new ViewHolder(view);

            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationTeachersAdapter.ViewHolder viewHolder, int position) {

        switch (productSpecificationTeachersModelList.get(position).getType()) {
            case ProductSpecificationTeachersModel.SPECIFICATION_TITLE:
                viewHolder.setTitle(productSpecificationTeachersModelList.get(position).getTitle());
                break;
            case ProductSpecificationTeachersModel.SPECIFICATION_BODY:
                String featureTitle = productSpecificationTeachersModelList.get(position).getFeatureName();
                String featureDetails = productSpecificationTeachersModelList.get(position).getFeatureValue();
                viewHolder.setFeatures(featureTitle, featureDetails);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return productSpecificationTeachersModelList.size();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView featureName;
        private TextView featureValue;
        private TextView title;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setTitle(String titleText) {
            title = (TextView) itemView;
            title.setText(titleText);
        }

        private void setFeatures(String featureTitle, String featureDetails) {
            featureName = itemView.findViewById(R.id.feature_name);
            featureValue = itemView.findViewById(R.id.feature_value);

            featureName.setText(featureTitle);
            featureValue.setText(featureDetails);
        }
    }

    //Method to convert pixels into dp
    private int setDp(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
