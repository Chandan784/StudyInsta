package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.studyinsta.studyinsta.classes.CartAdapter;
import com.studyinsta.studyinsta.classes.CartItemModel;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.MyCoursesModel;
import com.studyinsta.studyinsta.classes.notificaion.LocalDatabase;
import com.studyinsta.studyinsta.classes.notificaion.NotificationModel;
import com.studyinsta.studyinsta.fragments.MyCartFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.studyinsta.studyinsta.ProductDetailsActivity.productId;

public class DeliveryActivity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView deliveryRecyclerView;
    private TextView totalAmount;
    public static List<CartItemModel> cartItemModelList;
    private Button continueButton_PayButton;
    private Dialog loadingDialog;
    private ConstraintLayout orderConfirmationLayout;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference ref;
    private Button goToMaterialButton;
    private TextView orderIdTv, infoText;
    private String customOrderId;
    public static String productTitle;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String CHANNEL_ID = "Channel001";

    private boolean successfulResponse = false;
    public static long COURSE = 1;
    public static long TEST_SERIES = 2;
    public static long PDFNOTES = 3;
    public static long DOWNLOADABLE_MATERIAL = 4;
    public static long productType = 0;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Checkout.preload(getApplicationContext());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Purchase");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseFirestore = FirebaseFirestore.getInstance();

        totalAmount = findViewById(R.id.delivery_total_amount);
        continueButton_PayButton = findViewById(R.id.cart_continue_button);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        goToMaterialButton = findViewById(R.id.go_to_material_button);
        orderIdTv = findViewById(R.id.order_id);
        infoText = findViewById(R.id.textView19);

        ///loading Dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        /////end loading dialog

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        createNotificationChannel();

        Intent intent = getIntent();
        productTitle = intent.getStringExtra("product_title_name");
        productType = intent.getLongExtra("product_type", 0);

        deliveryRecyclerView = findViewById(R.id.delivery_activity_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        deliveryRecyclerView.setLayoutManager(layoutManager);

        CartAdapter cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        if (MyCartFragment.continueClickedFromMyCartFragment){
            MyCartFragment.loadingDialog.dismiss();
            MyCartFragment.continueClickedFromMyCartFragment = false;
        }
        if (ProductDetailsActivity.continueClickedFromProdDetailsActivity){
            ProductDetailsActivity.loadingDialog.dismiss();
            ProductDetailsActivity.continueClickedFromProdDetailsActivity = false;
        }

        customOrderId = UUID.randomUUID().toString().substring(0,10);

        continueButton_PayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                checkPrice();
            }
        });
    }
    public void checkPrice(){
        String checkPrice = totalAmount.getText().toString().substring(4, totalAmount.getText().toString().length() - 2);
//        Toast.makeText(this, "'"+checkPrice+"'", Toast.LENGTH_SHORT).show();
        int p = Integer.valueOf(checkPrice);

        if (p == 0){
//            Toast.makeText(this, "Price was found 0", Toast.LENGTH_SHORT).show();
            giveAccessToMaterials();
        }else {
            startPayment();
        }
    }

    public void startPayment() {

        String customer_Id = FirebaseAuth.getInstance().getUid();

        String finalProductPrice = totalAmount.getText().toString().substring(3, totalAmount.getText().toString().length() - 2) + "00";
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Study Insta");
            options.put("description", productTitle);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");

            //Order id is not being passed that => capture payments manually
//            options.put("order_id", orderId);
            options.put("theme.color", "#0f94ff");
            options.put("currency", "INR");
            options.put("notes.customer_id", customer_Id);
            options.put("notes.JHWR_order_id", customOrderId);
            options.put("amount", finalProductPrice);//pass amount in currency subunits
            options.put("prefill.email", DBqueries.email);
            options.put("prefill.contact",DBqueries.phone);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
            String error = e.getMessage();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    public void giveAccessToMaterials() {

        successfulResponse = true;
        loadingDialog.dismiss();
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        orderIdTv.setText("Your Order ID: " + customOrderId);

        FirebaseMessaging.getInstance().subscribeToTopic(productId);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 365);
        final Timestamp validTime = new Timestamp(c.getTime());

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                reference.child("AdditionalUserData").child(FirebaseAuth.getInstance().getUid())
                        .child(productId).setValue(validTime);

            }
        });

        th.start();

        firebaseFirestore.collection("PRODUCTS").document(productId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            documentSnapshot = task.getResult();

                            productType = (long) documentSnapshot.get("product_type");

                            if (productType == COURSE){
                                MainActivity.defaultFragment = 2;

                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_COURSES")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){

                                            DocumentSnapshot shot = task.getResult();
                                            long size = shot.getLong("list_size");

                                            //Add the product to the purchased courses list in DB
                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put("product_ID_" + size, productId);
                                            addProduct.put("list_size", (long) (size + 1));

                                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_COURSES")
                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myCoursesModelList.size() != 0) {
                                                            DBqueries.myCoursesModelList.add(new MyCoursesModel(
                                                                    productId,
                                                                    documentSnapshot.get("prduct_image_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    documentSnapshot.get("product_subtitle").toString()));
                                                        }
                                                        DBqueries.purchasedCoursesList.add(productId);
                                                        Toast.makeText(DeliveryActivity.this, "Product Purchased Successfully!", Toast.LENGTH_SHORT).show();

                                                        //trigger  purchase success notification
                                                        String title1 = "Enroll Success! to: " + productTitle;
                                                        String body1 = "To View Your Course, Just Go to My Courses";
                                                        triggerNotification(DeliveryActivity.this, CHANNEL_ID, title1, body1, 2);
                                                        captureNotification(title1,body1);
                                                        //end trigger  purchase success notification

                                                        //Statistics Tracking

                                                        //End Statistics Tracking

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                            // End Add the product to the purchased courses list in DB




                                        }else {
                                            triggerNotification(DeliveryActivity.this, CHANNEL_ID, "Course Not Purchased!", "Please contact Study Insta Team.", 7);
                                        }
                                    }
                                });


                                goToMaterialButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent courseIntent = new Intent(DeliveryActivity.this, MainActivity.class);
//                                        courseIntent.putExtra("defaultFragment", "2");
                                        startActivity(courseIntent);
                                        finish();
                                    }
                                });
                            }

                            else if (productType == TEST_SERIES){
                                MainActivity.defaultFragment = 6;




                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_TESTS")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){


                                            DocumentSnapshot shot = task.getResult();
                                            long size = shot.getLong("list_size");


                                            //Add the product to the purchased tests list in DB
                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put("product_ID_" + size, productId);
                                            addProduct.put("list_size", (long) (size + 1));

                                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_TESTS")
                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myTestsCoursesModelList.size() != 0) {
                                                            DBqueries.myTestsCoursesModelList.add(new MyCoursesModel(
                                                                    productId,
                                                                    documentSnapshot.get("prduct_image_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    documentSnapshot.get("product_subtitle").toString()));
                                                        }
                                                        DBqueries.purchasedTestsList.add(productId);
                                                        Toast.makeText(DeliveryActivity.this, "Product Purchased Successfully!", Toast.LENGTH_SHORT).show();

                                                        //trigger  purchase success notification
                                                        String title2 = "Purchase Success! : " + productTitle;
                                                        String body2 = "To Give The Test, Just Go to My Tests";
                                                        triggerNotification(DeliveryActivity.this, CHANNEL_ID, title2, body2, 6);
                                                        captureNotification(title2, body2);
                                                        //end trigger  purchase success notification


                                                        //Statistics Tracking

                                                        //End Statistics Tracking

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                            // End Add the product to the purchased tests list in DB


                                        }else {
                                            triggerNotification(DeliveryActivity.this, CHANNEL_ID, "Test Not Purchased!", "Please contact Study Insta Team.", 8);

                                        }
                                    }
                                });



                                goToMaterialButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent courseIntent = new Intent(DeliveryActivity.this, MainActivity.class);
//                                        courseIntent.putExtra("defaultFragment", "6");
                                        startActivity(courseIntent);
                                        finish();
                                    }
                                });
                            }

                            else if (productType == DOWNLOADABLE_MATERIAL){

                                goToMaterialButton.setText("Download Material");
                                infoText.setTextColor(getResources().getColor(R.color.btnRed));
                                // you can change the blow text...
                                // try to download now
                                infoText.setText("ध्यान दें ! यह PDF सिर्फ एक ही बार डाउनलोड किया जा सकता है| इसके बाद आप इसे डाउनलोड नहीं कर पाएंगे | ");

//                                storageReference = firebaseStorage.getInstance("gs://study-insta-2c548.appspot.com").getReference();
//                                ref = storageReference.child("downloadableMaterials/").child(productId + ".pdf");
//

                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_DOWNLOAD")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){


                                            DocumentSnapshot shot = task.getResult();
                                            long size = shot.getLong("list_size");
                                            
                                            //apne app me logout karo
                                            //and then signup karo abhi jo install hua usme


                                            //Add the product to the purchased tests list in DB
                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put("product_ID_" + size, productId);
                                            addProduct.put("list_size", (long) (size + 1));

                                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_DOWNLOAD")
                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myDownloadCoursesModelList.size() != 0) {
                                                            DBqueries.myDownloadCoursesModelList.add(new MyCoursesModel(
                                                                    productId,
                                                                    documentSnapshot.get("prduct_image_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    documentSnapshot.get("product_subtitle").toString()));
                                                        }
                                                        DBqueries.purchasedDownloadList.add(productId);
                                                        Toast.makeText(DeliveryActivity.this, "Product Purchased Successfully!", Toast.LENGTH_SHORT).show();



                                                        //Statistics Tracking

                                                        //End Statistics Tracking

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                            // End Add the product to the purchased tests list in DB


                                        }else {
                                            triggerNotification(DeliveryActivity.this, CHANNEL_ID, "Notes Not Purchased!", "Please contact Study Insta Team.", 8);

                                        }
                                    }
                                });
                                firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){

                                            DocumentSnapshot shot  = task.getResult();

                                            String downloadURL = (String) shot.get("download_url");
                                            Uri uri = Uri.parse(downloadURL);

                                            goToMaterialButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //todo: call downloader let user download material for once

                                                    try {
                                                        String filen = UUID.randomUUID().toString().substring(0, 5);

                                                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                                        DownloadManager.Request request = new DownloadManager.Request(uri);
                                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                        request.setDestinationInExternalFilesDir(DeliveryActivity.this, DIRECTORY_DOWNLOADS,  productTitle+ ".pdf");

                                                        downloadManager.enqueue(request);
                                                        Toast.makeText(DeliveryActivity.this, "Download Started! Check status bar!", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e){
                                                        Toast.makeText(DeliveryActivity.this, "Sorry Download Could Not Be Started! Please contact StudyInsta Team!", Toast.LENGTH_LONG).show();
                                                    } catch (Throwable e){
                                                        Toast.makeText(DeliveryActivity.this, "Sorry Download Could Not Be Started! Please contact StudyInsta Team!", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });

                                        }else{
                                            Toast.makeText(DeliveryActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                //trigger  purchase success notification
                                String title3 = "Purchase Success! : " + productTitle;
                                String body3 = "Just Click on Download Button!";

                                triggerNotification(DeliveryActivity.this, CHANNEL_ID, title3, body3, 6);
                                captureNotification(title3, body3);
                                captureNotification(title3, body3);
                                //end trigger  purchase success notification






                            }

                            else if (productType == PDFNOTES){
                                MainActivity.defaultFragment = 7;

                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTES")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){

                                            //Add the product to the purchased courses list in DB
                                            Map<String, Object> addProduct = new HashMap<>();
                                            addProduct.put("product_ID_" + DBqueries.purchasedNotesList.size(), productId);
                                            addProduct.put("list_size", (long) (DBqueries.purchasedNotesList.size() + 1));

//                                addProduct.put("product_ID_" + DBqueries.purchasedCoursesList.size(), productId);
//                                addProduct.put("list_size", (long) (DBqueries.purchasedCoursesList.size() + 1));

                                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTES")
                                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myNotesCoursesModelList.size() != 0) {
                                                            DBqueries.myNotesCoursesModelList.add(new MyCoursesModel(
                                                                    productId,
                                                                    documentSnapshot.get("prduct_image_1").toString(),
                                                                    documentSnapshot.get("product_title").toString(),
                                                                    documentSnapshot.get("product_subtitle").toString()));
                                                        }
                                                        DBqueries.purchasedNotesList.add(productId);
                                                        Toast.makeText(DeliveryActivity.this, "Product Purchased Successfully!", Toast.LENGTH_SHORT).show();

                                                        //trigger  purchase success notification
                                                        String title3 = "Purchase Success! : " + productTitle;
                                                        String body3 = "To Give The Test, Just Go to My Tests";
                                                        triggerNotification(DeliveryActivity.this, CHANNEL_ID, title3, body3, 7);
                                                        captureNotification(title3, body3);
                                                        //end trigger  purchase success notification

                                                        //Statistics Tracking

                                                        //End Statistics Tracking

                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    loadingDialog.dismiss();
                                                }
                                            });
                                            // End Add the product to the purchased courses list in DB


                                        }else {
                                            triggerNotification(DeliveryActivity.this, CHANNEL_ID, "Notes Not Purchased!", "Please contact Study Insta Team.", 10);

                                        }
                                    }
                                });


                                goToMaterialButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent courseIntent = new Intent(DeliveryActivity.this, MainActivity.class);
//                                        courseIntent.putExtra("defaultFragment", "7");
                                        startActivity(courseIntent);
                                        finish();
                                    }
                                });

                            }

                            else {
                                Toast.makeText(DeliveryActivity.this, "Product Type Not Recognized! Please Contact Study Insta Team!", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

    }

    private void triggerNotification(Context context, String channel_id, String title, String body, int number){
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent notificationIntent = new Intent(DeliveryActivity.this, MainActivity.class);
            notificationIntent.putExtra("fragmentNo", number);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent resultIntent = PendingIntent.getActivity(DeliveryActivity.this, 0, notificationIntent, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo);
        builder.setColor(0x008000);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setChannelId(channel_id);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(resultIntent) ;


        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1220 , builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "General";
            String description = "General Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void captureNotification(String title, String body){
        LocalDatabase db = LocalDatabase.getDbInstance(DeliveryActivity.this);
        NotificationModel model = new NotificationModel();
        model.title = title;
        model.body = body;
        db.notificationDao().insertNotification(model);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////RazorPay
    @Override
    public void onPaymentSuccess(String s) {
        giveAccessToMaterials();
    }

    @Override
    public void onPaymentError(int i, String s) {
        loadingDialog.dismiss();
        Toast.makeText(this, "Payment Failed! " + s , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        loadingDialog.dismiss();
        if (successfulResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }
}