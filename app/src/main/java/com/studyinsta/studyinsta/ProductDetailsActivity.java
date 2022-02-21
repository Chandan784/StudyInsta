package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studyinsta.studyinsta.classes.CartItemModel;
import com.studyinsta.studyinsta.classes.CommonPurchasedListAdapter;
import com.studyinsta.studyinsta.classes.CommonPurchasedListModel;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.ProductDetailsAdapter;
import com.studyinsta.studyinsta.classes.ProductImagesAdapter;
import com.studyinsta.studyinsta.classes.ProductSpecificationTeachersModel;
import com.studyinsta.studyinsta.classes.TestSetsAdapter;
import com.studyinsta.studyinsta.fragments.SignInFragment;
import com.studyinsta.studyinsta.fragments.SignUpFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.Html.fromHtml;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static boolean continueClickedFromProdDetailsActivity = false;


    public static MenuItem cartItem;
    private TextView badgeCount;

    public static long PRODUCT_TYPE = 0;

    private ViewPager productImagesViewPager;
    private TextView productTitle;
    private TextView productPrice;
    private TextView cuttedPrice;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TabLayout viewPagerIndicator;

    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;

    private Button buyNowBtn;
//    private LinearLayout addToCartBtn;
    private Dialog signInDialog;
    public static Dialog loadingDialog;
    private FirebaseUser currentUser;
    public static String productId;
    public static int actual;
    private RecyclerView demoRv;
    private ConstraintLayout prodDemoLayout;

    ////////////Product Description

    private ConstraintLayout productDetailsOnlyContainer;
    private TextView productOnlyDescriptionBody;
    private   List<ProductSpecificationTeachersModel> productSpecificationTeachersModelList = new ArrayList<>();
    private ConstraintLayout productDetailsTabsContainer;
    private   String productDescription;
    private   String productOtherDetails;

    ////////////End Product Description

    //////Ratings Layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsProgressBarContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsNumberContainer;
    private TextView averageRating;
    //////End Ratings Layout

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buyNowBtn = findViewById(R.id.buy_now_button);
//        addToCartBtn = findViewById(R.id.add_to_cart_button);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);

        productDetailsViewPager = findViewById(R.id.productDetails_viewPager);
        productDetailsTabLayout = findViewById(R.id.productDetails_tabLayout);
        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productDetailsTabsContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_basic_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNumberContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressBar_container);
        averageRating = findViewById(R.id.average_rating);
        demoRv = findViewById(R.id.demoRv);
        prodDemoLayout = findViewById(R.id.productDemoMainLayout);
        initialRating = -1;

        ///loading Dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////end loading dialog

        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();


        onNewIntent(getIntent());
      productId = getIntent().getStringExtra("PRODUCT_ID");

        try {
            firebaseFirestore.collection("PRODUCTS").document(productId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        documentSnapshot = task.getResult();
                        List<String> demoResourceList = new ArrayList<>();
                        List<String> demoItemNameList = new ArrayList<>();
                        List<CommonPurchasedListModel> courseList = new ArrayList<>();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        demoRv.setLayoutManager(layoutManager);

                        PRODUCT_TYPE = (long) documentSnapshot.get("product_type");
                        productTitle.setText(documentSnapshot.get("product_title").toString());

                        List<String> productImageList = (List<String>) documentSnapshot.get("product_images");


                        for (int x = 0; x <productImageList.size(); x++) {
                            productImages.add(productImageList.get(x).toString());
                        }

                        if (PRODUCT_TYPE == 1){
                            //todo: product is course
//                            try {
//
//                                for (long x = 1; x < (long) documentSnapshot.get("no_of_demo") + 1; x++) {
//                                    courseList.add(new CommonPurchasedListModel(1,
//                                            documentSnapshot.get("demo_item_title_" + x).toString(),
//                                            documentSnapshot.get("demo_resource_" + x).toString()));
//                                }
//                                if (courseList.size() != 0) {
//                                    prodDemoLayout.setVisibility(View.VISIBLE);
//
//                                    CommonPurchasedListAdapter cadapter = new CommonPurchasedListAdapter(courseList);
//                                    demoRv.setAdapter(cadapter);
//                                    cadapter.notifyDataSetChanged();
//
//                                }
//                            }catch (NullPointerException e){
//                                e.printStackTrace();
//                            }

                        }else if (PRODUCT_TYPE == 2){
                            //todo: product is test
//                            try {
//
//                                for (long x = 1; x < (long) documentSnapshot.get("no_of_demo") + 1; x++) {
//                                    demoItemNameList.add(documentSnapshot.get("demo_item_title_" + x).toString());
//                                    demoResourceList.add(documentSnapshot.get("demo_resource_" + x).toString());
//                                }
//                                if (demoItemNameList.size() != 0) {
//                                    prodDemoLayout.setVisibility(View.VISIBLE);
//
//                                    TestSetsAdapter tadapter = new TestSetsAdapter(productId, productTitle.getText() + " Demo", demoItemNameList, true, demoResourceList);
//                                    demoRv.setAdapter(tadapter);
//                                    tadapter.notifyDataSetChanged();
//                                }
//                            }catch (NullPointerException e){
//                                e.printStackTrace();
//                            }
                        }
                        ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                        productImagesViewPager.setAdapter(productImagesAdapter);


                        averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                        totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ") ratings");
                        cuttedPrice.setText("Rs. " + documentSnapshot.get("cutted_price").toString() + "/-");
                        String priceStr = documentSnapshot.get("product_price").toString();
                        if (Integer.parseInt(priceStr) == 0){
                            productPrice.setText("FREE");
                            buyNowBtn.setText("Get FREE");
                        }else {
                            productPrice.setText("Rs. " + priceStr + "/-");
                        }


//                        if ((boolean) documentSnapshot.get("use_tab_layout")) {

//                          This is a filler if statement, the original statement is above this
                            if (3>5) {
                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                            productDetailsOnlyContainer.setVisibility(View.GONE);
                            productDescription = documentSnapshot.get("product_description").toString();
                            productOtherDetails = documentSnapshot.get("product_other_details").toString();

                            for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                for (long y = 1; y < (long) documentSnapshot.get("total_fields_under_spec_title_" + x) + 1; y++) {
                                    productSpecificationTeachersModelList.add(new ProductSpecificationTeachersModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(), documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));

                                }
                            }
                        } else {
                            productDetailsTabsContainer.setVisibility(View.GONE);
                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                productOnlyDescriptionBody.setText(fromHtml(documentSnapshot.get("product_description").toString(), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                productOnlyDescriptionBody.setText(fromHtml(documentSnapshot.get("product_description").toString()));
                            }
                        }
                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

                        for (int x = 0; x < 5; x++) {
                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(x);
                            rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                            ////Setting ProgressBar
                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                            progressBar.setMax(maxProgress);
                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                        }
                        totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                        averageRating.setText(documentSnapshot.get("average_rating").toString());
                        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationTeachersModelList));

                        if (currentUser != null) {
                            if (DBqueries.myRating.size() == 0) {
                                DBqueries.loadRatingList(ProductDetailsActivity.this);
                            }
        //                        if (DBqueries.cartList.size() == 0){
        //                            DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
        //                        }
                        }
                        loadingDialog.dismiss();


                        if (DBqueries.myRatedIds.contains(productId)) {
                            int index = DBqueries.myRatedIds.indexOf(productId);
                            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) - 1;
                            setRating(initialRating);
                        }

        //                    if (DBqueries.cartList.contains(productId)){
        //                        ALREADY_ADDED_TO_CART = true;
        //                    }else {
        //                        ALREADY_ADDED_TO_CART = false;
        //                    }
                    } else {
                        loadingDialog.dismiss();
                        String error = task.getException().getMessage();
                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Log.d("Firestore Error:", "" + e.getMessage());
            finish();
            Toast.makeText(this, "Oops! Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ////Rating Layout
        rateNowContainer = findViewById(R.id.rate_now_container);

        for (int x =0; x<rateNowContainer.getChildCount();x++){
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null){
                        signInDialog.show();
                    }else {
                        if(starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starPosition);

                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueries.myRatedIds.contains(productId)) {

                                    TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition - initialRating, true));
                                } else {
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", calculateAverageRating((long) starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                firebaseFirestore.collection("PRODUCTS").document(productId)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();

                                            if (DBqueries.myRatedIds.contains(productId)) {
                                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productId), (long) starPosition + 1);
                                            } else {
                                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productId);
                                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                            }


                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myRatedIds.contains(productId)) {
                                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productId), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                                            TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));


                                                        } else {

                                                            DBqueries.myRatedIds.add(productId);
                                                            DBqueries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(5 - (starPosition + 1));
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ") ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank You for your rating! You are awesome :)", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingFigures = (TextView) ratingsNumberContainer.getChildAt(x);
                                                            ////Setting ProgressBar
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText(calculateAverageRating((long) 0, true));
                                                        averageRatingMiniView.setText(calculateAverageRating((long) 0, true));


                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });

                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }

        ////End Rating Layout
            buyNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    continueClickedFromProdDetailsActivity = true;
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {

                        loadingDialog.show();
                        DeliveryActivity.cartItemModelList = new ArrayList<>();
                        DeliveryActivity.cartItemModelList.add(0, new CartItemModel(CartItemModel.CART_ITEM,
                                productId,
                                ((List<String>)documentSnapshot.get("product_images")).get(0).toString(),
                                documentSnapshot.get("product_title").toString(),
                                documentSnapshot.get("product_price").toString(),
                                documentSnapshot.get("cutted_price").toString()));
                        DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        deliveryIntent.putExtra("product_title_name", productTitle.getText().toString());
                        deliveryIntent.putExtra("product_amount", productPrice.getText().toString());
                        deliveryIntent.putExtra("product_id", productId);
                        deliveryIntent.putExtra("product_type", PRODUCT_TYPE);
                        startActivity(deliveryIntent);

                    }
                }
            });
//        addToCartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentUser == null){
//                    signInDialog.show();
//                }else {
//                    if (!running_cart_query){
//                        running_cart_query = true;
//                        if (ALREADY_ADDED_TO_CART){
//                            running_cart_query = false;
//                            Toast.makeText(ProductDetailsActivity.this, "Already Added To Cart!", Toast.LENGTH_SHORT).show();
//                        }else {
//                            loadingDialog.show();
//                            Map<String, Object> addProduct = new HashMap<>();
//                            addProduct.put("product_ID_" + DBqueries.cartList.size(), productId);
//                            addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));
//
//                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
//                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        if (DBqueries.cartItemModelList.size() != 0){
//                                            DBqueries.cartItemModelList.add(0, new CartItemModel(CartItemModel.CART_ITEM,
//                                                    productId,
//                                                    documentSnapshot.get("prduct_image_1").toString(),
//                                                    documentSnapshot.get("product_title").toString(),
//                                                    documentSnapshot.get("product_price").toString(),
//                                                    documentSnapshot.get("cutted_price").toString()));
//                                        }
//                                        DBqueries.cartList.add(productId);
//                                        ALREADY_ADDED_TO_CART = true;
//                                        Toast.makeText(ProductDetailsActivity.this, "Added To Cart Successfully!", Toast.LENGTH_SHORT).show();
//                                        invalidateOptionsMenu();
//                                        running_cart_query = false;
//                                    }else {
//                                        running_cart_query = false;
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(ProductDetailsActivity.this,error, Toast.LENGTH_SHORT).show();
//                                    }
//                                    loadingDialog.dismiss();
//                                }
//                            });
//                        }
//                    }
//                }
//            }
//        });

        //sign in dialog
        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_button);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseButton = true;
                SignUpFragment.disableCloseButton = true;
                signInDialog.dismiss();
                Intent signInIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);
                startActivity(signInIntent);
            }
        });

        //end sign in dialog

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            if (DBqueries.myRating.size() ==0){
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }

        }else {
            loadingDialog.dismiss();
        }
        if (DBqueries.myRatedIds.contains(productId)){
            int index = DBqueries.myRatedIds.indexOf(productId);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index)))-1;
            setRating(initialRating);
        }
//        if (DBqueries.cartList.contains(productId)){
//            ALREADY_ADDED_TO_CART = true;
//        }else {
//            ALREADY_ADDED_TO_CART = false;
//        }
        invalidateOptionsMenu();

    }

    public static void setRating(int starPosition) {
        if (starPosition > -1) {
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starButton = (ImageView) rateNowContainer.getChildAt(x);
                starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }

    private String calculateAverageRating(Long currentUserRating, boolean update){
        Double totalStars = Double.valueOf(0);
        for(int x = 1; x < 6; x++){
            TextView ratingNo = (TextView) ratingsNumberContainer.getChildAt(5-x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()))*x;
        }
        totalStars = totalStars + currentUserRating;
        if(update){
            return String.valueOf(totalStars/Long.parseLong(totalRatingsFigure.getText().toString())).substring(0,3);

        }else {
            return String.valueOf(totalStars/(Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0,3);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras() ;
        if (extras != null ) {
            if (extras.containsKey( "PRODUCT_ID" )) {
                String receivedProductId = extras.getString( "PRODUCT_ID" ) ;
                productId = receivedProductId;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

//        cartItem = menu.findItem(R.id.product_details_activity_cart);
//            cartItem.setActionView(R.layout.badge_layout);
//            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
//            badgeIcon.setImageResource(R.drawable.cart_icon);
//            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null){
//                if (DBqueries.cartList.size() == 0){
//                    DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
//                }else {
//                    badgeCount.setVisibility(View.VISIBLE);
//                    if (DBqueries.cartList.size() < 99) {
//                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
//                    }else {
//                        badgeCount.setText("99");
//                    }
//                }
            }
//            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (currentUser == null){
//                        signInDialog.show();
//                    }else {
//                        Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
//                        showCart = true;
//                        startActivity(cartIntent);
//                    }
//                }
//            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.product_details_activity_search) {
//            //todo search
//            return true;

//        }else if (id == R.id.product_details_activity_cart){
//            if (currentUser == null){
//                signInDialog.show();
//            }else {
//                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
//                showCart = true;
//                startActivity(cartIntent);
//                return true;
//            }
//
//    }
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
