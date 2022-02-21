package com.studyinsta.studyinsta.classes;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.studyinsta.studyinsta.CourseSubjectsActivity;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.fragments.EBookFragment;
import com.studyinsta.studyinsta.fragments.HomeFragment;
import com.studyinsta.studyinsta.fragments.MyCartFragment;
import com.studyinsta.studyinsta.fragments.MyCoursesFragment;
import com.studyinsta.studyinsta.fragments.MyNotesFragment;
import com.studyinsta.studyinsta.fragments.MyTestsFragment;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBqueries {

    public static List<String> setTitleList = new ArrayList<>(), sortedList = new ArrayList<>(), keyNameList = new ArrayList<>();

    public static List<QuestionsModel> englishListToTransfer = new ArrayList<>();
    public static List<QuestionsModel> hindiListToTransfer = new ArrayList<>();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email, fullName, phone;

    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<String> purchasedCoursesList = new ArrayList<>();
    public static List<MyCoursesModel> myCoursesModelList = new ArrayList<>();

    public static List<String> purchasedTestsList = new ArrayList<>();
    public static List<MyCoursesModel> myTestsCoursesModelList = new ArrayList<>();

    public static List<String> purchasedNotesList = new ArrayList<>();
    public static List<MyCoursesModel> myNotesCoursesModelList = new ArrayList<>();

    public static List<String> purchasedDownloadList = new ArrayList<>();
    public static List<MyCoursesModel> myDownloadCoursesModelList = new ArrayList<>();

    public static List<String> subjectNamesList = new ArrayList();

    public static List<CommonPurchasedListModel> lecturesList = new ArrayList<>();

    public static List<CommonPurchasedListModel> courseNotesList = new ArrayList<>();

    public static List<CommonPurchasedListModel> courseTestsList = new ArrayList<>();

    public static List<CommonPurchasedListModel> individualNotesList = new ArrayList<>();

////////////////// All database Queries

    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context) {

        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),
                                        documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadCourseNotes(final Context context, final Dialog loadingDialog, final RecyclerView recyclerView, String productId, String subjectName, final TextView moreTv, final TextView noMat) {
        courseNotesList.clear();
        if (productId != null && !productId.equals("")) {
            firebaseFirestore.collection("PURCHASEDCOURSES").document(productId)
                    .collection(subjectName.toUpperCase(Locale.ROOT)).document("notes")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
//                        long total_lectures = (long) task.getResult().get("total_notes");

                        List<String> titleList = (List<String>) task.getResult().get("title_list");
                        List<String> urlList = (List<String>) task.getResult().get("url_list");
                        for (int x = 0; x < titleList.size(); x++) {
                            courseNotesList.add(new CommonPurchasedListModel(CourseSubjectsActivity.NOTES,
                                    titleList.get(x).toString(),
                                    urlList.get(x).toString()));
                        }
                        CommonPurchasedListAdapter adapter = new CommonPurchasedListAdapter(DBqueries.courseNotesList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if (courseNotesList.size() == 0) {
                            moreTv.setVisibility(View.GONE);
                            noMat.setVisibility(View.VISIBLE);
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();

                }
            });
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadCourseTests(final Context context, final Dialog loadingDialog, final RecyclerView recyclerView, String productId, String subjectName, final TextView moreTv, final TextView noMat) {
        courseTestsList.clear();
        if (productId != null && !productId.equals("")) {
            firebaseFirestore.collection("PURCHASEDCOURSES").document(productId)
                    .collection(subjectName.toUpperCase(Locale.ROOT)).document("tests")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
//                        long total_lectures = (long) task.getResult().get("total_tests");
                        DocumentSnapshot shot = task.getResult();

                        List<String> titleList = (List<String>) shot.get("title_list");
                        List<String> urlList = (List<String>) shot.get("url_list");

                        for (int x = 0; x < titleList.size(); x++) {

                            courseTestsList.add(new CommonPurchasedListModel(CourseSubjectsActivity.TEST,
                                    titleList.get(x).toString(),
                                    urlList.get(x).toString()));
                        }
                        CommonPurchasedListAdapter adapter = new CommonPurchasedListAdapter(DBqueries.courseTestsList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if (courseTestsList.size() == 0) {
                            moreTv.setVisibility(View.GONE);
                            noMat.setVisibility(View.VISIBLE);
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadLectures(final Context context, final Dialog loadingDialog, final RecyclerView recyclerView, String productId, String subjectName, final TextView moreTv, final TextView noMat) {
        lecturesList.clear();
        if (productId != null && !productId.equals("")) {
            firebaseFirestore.collection("PURCHASEDCOURSES").document(productId)
                    .collection(subjectName.toUpperCase(Locale.ROOT)).document("lectures")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

//                        long total_lectures = (long) task.getResult().get("total_lectures");

                        List<String> urlList = (List<String>) task.getResult().get("url_list");
                        List<String> titleList = (List<String>) task.getResult().get("title_list");

                        for (int x = 0; x < titleList.size(); x++) {
                            lecturesList.add(new CommonPurchasedListModel(CourseSubjectsActivity.LECTURE,
                                    titleList.get(x).toString(),
                                    urlList.get(x).toString()));
                        }
                        CommonPurchasedListAdapter adapter = new CommonPurchasedListAdapter(DBqueries.lecturesList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        if (lecturesList.size() == 0) {
                            moreTv.setVisibility(View.GONE);
                            noMat.setVisibility(View.VISIBLE);
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();

                }
            });
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loadSubjects(final RecyclerView recyclerView, final Context context, final Dialog loadingDialog, String productID) {
        subjectNamesList.clear();
        firebaseFirestore.collection("PURCHASEDCOURSES").document(productID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

//                            long no_of_subjects = (long) task.getResult().get("no_of_subjects");

//                            for (long x = 0; x < no_of_subjects; x++) {
//                                subjectNamesList.add(task.getResult().get("subject_" + x).toString());
//                            }

                            subjectNamesList = (List<String>) task.getResult().get("subject_list");
                            SubjectsAdapter subjectsAdapter = new SubjectsAdapter(subjectNamesList);
                            recyclerView.setAdapter(subjectsAdapter);
                            subjectsAdapter.notifyDataSetChanged();

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    public static void loadMyCourses(final Context context, final Dialog loadingDialog, final boolean loadCourseData) {
        purchasedCoursesList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_COURSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().get("list_size") != null) {
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            purchasedCoursesList.add(task.getResult().get("product_ID_" + x).toString());

                            myCoursesModelList.clear();
                            if (loadCourseData) {
                                final String productId = task.getResult().get("product_ID_" + x).toString();

                                firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            myCoursesModelList.add(new MyCoursesModel(productId,
                                                    ((List<String>) task.getResult().get("product_images")).get(0).toString(),
                                                    task.getResult().get("product_title").toString(),
                                                    task.getResult().get("product_subtitle").toString()));
                                            if (purchasedCoursesList.size() == 0) {
                                                myCoursesModelList.clear();
                                            }
                                            MyCoursesFragment.myCoursesAdapter.notifyDataSetChanged();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please contact Jharkhand Warrior Team! list_size does not exist!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });

    }

    public static void loadMyNotes(final Context context, final Dialog loadingDialog, final boolean loadCourseData) {
        purchasedNotesList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        DocumentSnapshot shot = task.getResult();
                        final String productId = shot.get("product_ID_" + x).toString();

                        purchasedNotesList.add(productId);
                        individualNotesList.clear();
                        if (loadCourseData) {

                            firebaseFirestore.collection("PRODUCTS")
                                    .document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot shot = task.getResult();

                                        String title = (String) shot.get("notes_title");
                                        String url = (String) shot.get("notes_url");

                                        Log.d("mylog", "onComplete: " + title);

                                        individualNotesList.add(new CommonPurchasedListModel(CourseSubjectsActivity.NOTES, title, url));
                                        if (purchasedNotesList.size() == 0) {
                                            individualNotesList.clear();
                                        }
                                        MyNotesFragment.adapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });

    }

    public static void loadMyDownload(final Context context, final Dialog loadingDialog, final RecyclerView rv) {
        purchasedDownloadList.clear();
        myDownloadCoursesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_DOWNLOAD")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> downloadUrlList = new ArrayList<>();

                    long sizee = (long) task.getResult().get("list_size");
                    for (long x = 0; x <sizee ; x++) {
                        long a = x;
                        DocumentSnapshot shot = task.getResult();
                        final String productId = shot.get("product_ID_" + x).toString();

                        purchasedDownloadList.add(productId);


                            firebaseFirestore.collection("PRODUCTS")
                                    .document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot shot = task.getResult();

                                        String title = (String) shot.get("product_title");
                                        String url = (String) shot.get("download_url");
                                        String icon = (String) shot.get("product_image_1");
                                        String subtitle = (String) shot.get("product_subtitle");

                                        myDownloadCoursesModelList.add(new MyCoursesModel(productId, icon, title,subtitle));

                                        Log.d("mylog", "onComplete: " + title);

                                        downloadUrlList.add(url);

                                        if(a == sizee -1){
                                            MyCoursesAdapter adapter = new MyCoursesAdapter(DBqueries.myDownloadCoursesModelList, downloadUrlList );
                                            rv.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }



                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });

    }

    public static void loadMyTests(final Context context, final Dialog loadingDialog, final boolean loadTestsData) {
        purchasedTestsList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_TESTS")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        purchasedTestsList.add(task.getResult().get("product_ID_" + x).toString());

                        myTestsCoursesModelList.clear();
                        if (loadTestsData) {
                            final String productId = task.getResult().get("product_ID_" + x).toString();

                            firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        myTestsCoursesModelList.add(new MyCoursesModel(productId,
                                                ((List<String>) task.getResult().get("product_images")).get(0).toString(),
                                                task.getResult().get("product_title").toString(),
                                                task.getResult().get("product_subtitle").toString()));
                                        if (purchasedTestsList.size() == 0) {
                                            myTestsCoursesModelList.clear();
                                        }
                                        MyTestsFragment.myTestsCoursesAdapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });

    }

    public static void old_noteInUse_loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("PROMOTION")
                .orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x < no_of_banners + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(),
                                                "#ffffff"));
                                    }
                                    lists.get(index).add(new HomePageModel(0, sliderModelList));
                                } else if ((long) documentSnapshot.get("view_type") == 1) {

                                    List<ViewAllModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");

                                    for (String productId : productIds) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(
                                                productId
                                                , ""
                                                , ""
                                                , ""
                                                , ""));

                                        viewAllProductList.add(new ViewAllModel(
                                                productId
                                                , ""
                                                , ""
                                                , ""
                                                , ""
                                                , ""
                                                , ""
                                                , 0));
                                    }
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));
                                } else if ((long) documentSnapshot.get("view_type") == 2) {
                                    List<HorizontalProductScrollModel> newGridList = new ArrayList<>();
                                    ArrayList<String> productIds = (ArrayList<String>) documentSnapshot.get("products");

                                    for (String productId : productIds) {
                                        newGridList.add(new HorizontalProductScrollModel(
                                                productId
                                                , ""
                                                , ""
                                                , ""
                                                , ""));
                                    }
                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), newGridList));
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_RATINGS").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                                    myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                                    myRating.add((long) task.getResult().get("rating_" + x));

                                    if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productId)) {
                                        ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                        if (ProductDetailsActivity.rateNowContainer != null) {
                                            ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                        }
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }
                            ProductDetailsActivity.running_rating_query = false;
                        }
                    });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());
                        if (DBqueries.cartList.contains(ProductDetailsActivity.productId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int index = 0;
                                        if (cartList.size() >= 2) {
                                            index = cartList.size() - 2;
                                        }
                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                productId,
                                                ((List<String>) task.getResult().get("product_images")).get(0).toString(),
                                                task.getResult().get("product_title").toString(),
                                                task.getResult().get("product_price").toString(),
                                                task.getResult().get("cutted_price").toString()));

                                        if (!(cartItemModelList.get(cartItemModelList.size() - 1).getType() == CartItemModel.TOTAL_AMOUNT)) {
//                                           if (MainActivity.activityFreshStart){
//                                               cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
//                                               LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
//                                               parent.setVisibility(View.VISIBLE);
//                                           }
                                            if (cartList.size() == 1) {
                                                cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                parent.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        if (cartList.size() == 0) {
                                            cartItemModelList.clear();
                                        }

                                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE);
                    } else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }
                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();

                    }
                    Toast.makeText(context, "Removed Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index, removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });


    }

    public static void clearData() {
        myCoursesModelList.clear();
        myNotesCoursesModelList.clear();
        myTestsCoursesModelList.clear();
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        cartList.clear();
        cartItemModelList.clear();
    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("PROMOTION")
                .orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    List<String> bannerList = (List<String>) documentSnapshot.get("banner_list");
//                                    long no_of_banners = (long)documentSnapshot.get("no_of_banners");
                                    for (int x = 0; x < bannerList.size(); x++) {
                                        sliderModelList.add(new SliderModel(bannerList.get(x), "#ffffff"));
                                    }
                                    lists.get(index).add(new HomePageModel(0, sliderModelList));
                                } else if ((long) documentSnapshot.get("view_type") == 1) {

                                    List<ViewAllModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

                                    long no_of_products = (long) documentSnapshot.get("no_of_products");


                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(
                                                documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductList.add(new ViewAllModel(
                                                documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()
                                                , documentSnapshot.get("cuttedd_price_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("average_rating_" + x).toString()
                                                , 0));
                                    }
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));
                                } else if ((long) documentSnapshot.get("view_type") == 2) {
//                                    String[] prodID = {};
//                                    for (final String productId : prodID) {
//                                        firebaseFirestore.collection("PRODUCTS")
//                                                .document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                                                DocumentSnapshot shot = task.getResult();
//                                                List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
//                                                gridLayoutModelList.add(new HorizontalProductScrollModel(
//                                                        productId
//                                                        , shot.get("product_image_1").toString()
//                                                        , shot.get("product_title").toString()
//                                                        , shot.get("product_subtitle").toString()
//                                                        , shot.get("product_price").toString()));
//                                            }
//                                        });
//                                    }


                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_products = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(
                                                documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridLayoutModelList));
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
