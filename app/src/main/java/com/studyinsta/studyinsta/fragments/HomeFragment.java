package com.studyinsta.studyinsta.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.studyinsta.studyinsta.MainActivity;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.CategoryAdapter;
import com.studyinsta.studyinsta.classes.CategoryModel;
import com.studyinsta.studyinsta.classes.HomePageAdapter;
import com.studyinsta.studyinsta.classes.HomePageModel;
import com.studyinsta.studyinsta.classes.HorizontalProductScrollModel;
import com.studyinsta.studyinsta.classes.SliderModel;
import com.studyinsta.studyinsta.classes.SliderTextMetaModel;
import com.studyinsta.studyinsta.classes.ViewAllModel;

import java.util.ArrayList;
import java.util.List;

import static com.studyinsta.studyinsta.MainActivity.reference;
import static com.studyinsta.studyinsta.classes.DBqueries.categoryModelList;
import static com.studyinsta.studyinsta.classes.DBqueries.lists;
import static com.studyinsta.studyinsta.classes.DBqueries.loadCategories;
import static com.studyinsta.studyinsta.classes.DBqueries.loadFragmentData;
import static com.studyinsta.studyinsta.classes.DBqueries.loadedCategoriesNames;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private TextView slidingTextView;
    private List<SliderTextMetaModel> sliderTextMetaModelList = new ArrayList<>();

    private ImageView noInternetConnection;
    private Button retryButton;
    private RecyclerView categoryRecyclerView;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private HomePageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary));

        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        retryButton = view.findViewById(R.id.retry_btn);
        categoryRecyclerView = view.findViewById(R.id.category_recyclerview);
        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerView);
        slidingTextView = view.findViewById(R.id.sliderTV);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(RecyclerView.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        //Categories Fake List
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        categoryModelFakeList.add(new CategoryModel("",""));
        //End Categories Fake List

        //Home Page Fake List
        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));
        sliderModelFakeList.add(new SliderModel("null", "#dfdfdf"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#dfdfdf",horizontalProductScrollModelFakeList,new ArrayList<ViewAllModel>()));
        homePageModelFakeList.add(new HomePageModel(2,"","#dfdfdf",horizontalProductScrollModelFakeList));
        //Home Page Fake List


        categoryAdapter = new CategoryAdapter(categoryModelFakeList);
        adapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            getSliderText();

            if (categoryModelList.size() == 0){
                loadCategories(categoryRecyclerView, getContext());

            }else{
                categoryAdapter.notifyDataSetChanged();
                categoryAdapter = new CategoryAdapter(categoryModelList);
            }
            categoryRecyclerView.setAdapter(categoryAdapter);


            if (lists.size() == 0){
                loadedCategoriesNames.add("Home");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homePageRecyclerView,getContext(),0,"HOME");
            }else{
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);


        }else {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.no_internet_connection_image).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
        }

        //////////Swipe Refresh Layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        //////////End Swipe Refresh Layout

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return view;
    }

    private void getSliderText(){
        sliderTextMetaModelList.clear();

            reference.child("SliderText").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    sliderTextMetaModelList.add(dataSnapshot.getValue(SliderTextMetaModel.class));
                }
                if (!sliderTextMetaModelList.equals("")){

                    if (!sliderTextMetaModelList.get(0).text.equals("")){
                        //text is not empty
                        slidingTextView.setVisibility(View.VISIBLE);
                        slidingTextView.setBackgroundTintList(null);
                        slidingTextView.setSelected(true);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            slidingTextView.setText(Html.fromHtml(sliderTextMetaModelList.get(0).text, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            slidingTextView.setText(Html.fromHtml(sliderTextMetaModelList.get(0).text));
                        }

                        if (!sliderTextMetaModelList.get(0).prodId.equals("")){
                            //product ID is not empty
                            //open product details activity for the given product id
                            slidingTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent prodIntent = new Intent(getContext(), ProductDetailsActivity.class);
                                    prodIntent.putExtra("PRODUCT_ID", sliderTextMetaModelList.get(0).prodId);
                                    getActivity().startActivity(prodIntent);
                                }
                            });

                        }else {
                            //product ID is empty
                        }
                        if (!sliderTextMetaModelList.get(0).url.equals("")){
                            slidingTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String url = sliderTextMetaModelList.get(0).url;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

                        }else {
                            //url is empty
                        }
                    }

                    else {
                        //text is empty
                        slidingTextView.setVisibility(View.GONE);
                    }


                }else {

                    //list is empty
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void reloadPage(){
        networkInfo = connectivityManager.getActiveNetworkInfo();
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecyclerView.setVisibility(View.VISIBLE);
            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageRecyclerView.setAdapter(adapter);

            loadCategories(categoryRecyclerView, getContext());

            loadedCategoriesNames.add("Home");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecyclerView, getContext(),0,"HOME");
        } else {
            MainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "Sorry! No Internet Connection Detected!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_internet_connection_image).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}
