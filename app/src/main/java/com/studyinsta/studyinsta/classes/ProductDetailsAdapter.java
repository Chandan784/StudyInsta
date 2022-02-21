package com.studyinsta.studyinsta.classes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.studyinsta.studyinsta.fragments.ProductDesicriptionFragment;
import com.studyinsta.studyinsta.fragments.ProductSpecificationFragment;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {

    private int totalTabs;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs, String productDescription, String productOtherDetails, List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList) {
        super(fm);
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecificationTeachersModelList = productSpecificationTeachersModelList;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ProductDesicriptionFragment productDesicriptionFragment1 = new ProductDesicriptionFragment();
                productDesicriptionFragment1.body = productDescription;
                return productDesicriptionFragment1;
            case 1:
                ProductSpecificationFragment productSpecificationFragment = new ProductSpecificationFragment();
                productSpecificationFragment.productSpecificationTeachersModelList = productSpecificationTeachersModelList;
                return productSpecificationFragment;

            case 2:
                ProductDesicriptionFragment productDesicriptionFragment2 = new ProductDesicriptionFragment();
                productDesicriptionFragment2.body = productOtherDetails;
                return productDesicriptionFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
