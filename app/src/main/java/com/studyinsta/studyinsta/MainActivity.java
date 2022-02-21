package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.fragments.EBookFragment;
import com.studyinsta.studyinsta.fragments.HomeFragment;
import com.studyinsta.studyinsta.fragments.MyAccountFragment;
import com.studyinsta.studyinsta.fragments.MyCoursesFragment;
import com.studyinsta.studyinsta.fragments.MyNotesFragment;
import com.studyinsta.studyinsta.fragments.MyTestsFragment;
import com.studyinsta.studyinsta.fragments.NotificationsFragment;
import com.studyinsta.studyinsta.fragments.SignInFragment;
import com.studyinsta.studyinsta.fragments.SignUpFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    public static DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private androidx.appcompat.widget.Toolbar toolbar;

    public static FirebaseDatabase database;
    public static DatabaseReference reference;

    private TextView titleJhWarrior;
    private Dialog signInDialog;
    private FirebaseUser currentUser;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int MY_COURSES_FRAGMENT = 2;
    private static final int NOTIFICATIONS_FRAGMENT = 3;
    private static final int ACCOUNT_FRAGMENT = 4;
    private static final int ORDERS_FRAGMENT = 5;
    private static final int MY_TEST_FRAGMENT = 6;
    private static final int MY_NOTES_FRAGMENT = 7;
    private static final int MY_EBOOK_FRAGMENT = 8;

    //    public static Boolean activityFreshStart = false;
    public static int defaultFragment = -1;
    public static int currentFragment = -1;

    private long newVersion;
    private long currentVersion;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        activityFreshStart = true;

        database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = database.getReference();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);


        //Assigning Our Custom Toolbar && Hamburger Icon

        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleJhWarrior = findViewById(R.id.main_activity_action_bar_title);
        //Done!! Assigning Toolbar && Hamburger Icon

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.White));
        toggle.syncState();

        checkFragment();

        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_button);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseButton = true;
                SignUpFragment.disableCloseButton = true;
                signInDialog.dismiss();
                Intent signInIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(signInIntent);
            }
        });


        //Force Update Setup

        getVersionCode();


//        todo: testing required for the below code96

//            NewUpdateDatabase db = NewUpdateDatabase.getDbInstance(this);
//            List<NewUpdateModel> updateInfoList = db.newUpdateDao().getUpdateList();
//            try {
//                if (updateInfoList.size() != 0) {
//
//                    newVersion = updateInfoList.get(0).version;
//                    if (newVersion > currentVersion){
//                        showDialog(String.valueOf(newVersion));
//                    }else if (newVersion == currentVersion){
//                        //todo : when query ran
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.HOUR, 24);
//                        long more24 = calendar.getTimeInMillis();
//
//                        Calendar calendar1 = Calendar.getInstance();
//                        long currentTime = calendar1.getTimeInMillis();
//                        if ( currentTime > updateInfoList.get(0).queryRanTime){
//                            //todo: run firebase query again
//
//                            FirebaseFirestore.getInstance().collection("VERSION").document("LATEST_VERSION")
//                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                    if (task.isSuccessful()){
//                                        newVersion = (long) task.getResult().get("value");
//                                        if (newVersion > currentVersion){
//                                            showDialog(String.valueOf(newVersion));
//                                        }
//                                        NewUpdateDatabase upDb = NewUpdateDatabase.getDbInstance(getApplicationContext());
//                                        NewUpdateModel model = new NewUpdateModel();
//                                        model.version = newVersion;
//                                        upDb.newUpdateDao().insertUpdateInfo(model);
//                                    }
//                                }
//                            });
//
//                        }else {
//                            //Do nothing
//                        }
//                    }
//                } else {
//                    FirebaseFirestore.getInstance().collection("VERSION").document("LATEST_VERSION")
//                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()){
//                                newVersion = (long) task.getResult().get("value");
//                                if (newVersion > currentVersion){
//                                    showDialog(String.valueOf(newVersion));
//                                }
//                                NewUpdateDatabase upDb = NewUpdateDatabase.getDbInstance(getApplicationContext());
//                                NewUpdateModel model = new NewUpdateModel();
//                                model.version = newVersion;
//                                upDb.newUpdateDao().insertUpdateInfo(model);
//                            }
//                        }
//                    });
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }

        FirebaseMessaging.getInstance().subscribeToTopic("all");

//        reference.child("AppMeta").child("CurrentLatestVersion").child("versionNumber").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    newVersion =  (long) snapshot.getValue(Long.class);
//                if (newVersion > currentVersion){
//
//                    showDialog(String.valueOf(newVersion));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        // End Force Update Setup

        navigationView.setItemIconTintList(null);

        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            MenuItem menuItem;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                menuItem = item;
                if (currentUser != null) {
                    drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(View drawerView) {
                            super.onDrawerClosed(drawerView);
                            switch (menuItem.getItemId()) {

                                case R.id.nav_homeFragment:
                                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                                    invalidateOptionsMenu();
                                    break;

                                case R.id.nav_myEbookFragment:
                                    gotoFragment(new EBookFragment(), MY_EBOOK_FRAGMENT, "My Download");
                                    break;

                                case R.id.nav_myCoursesFragment:
                                    gotoFragment(new MyCoursesFragment(), MY_COURSES_FRAGMENT, "My Courses");
                                    break;
//                                case R.id.testActivity:
//                                    Intent intent = new Intent(MainActivity.this, TestInstructionActivity.class);
//                                    intent.putExtra("prodId", "productId");
//                                    intent.putExtra("setNo",1);
//                                    intent.putExtra("title", "TestTitle");
//                                    startActivity(intent);
//                                    break;

                                case R.id.nav_myTestsFragment:
                                    gotoFragment(new MyTestsFragment(), MY_TEST_FRAGMENT, "My Tests");
                                    break;

                                case R.id.nav_myNotesFragment:
                                    gotoFragment(new MyNotesFragment(), MY_NOTES_FRAGMENT, "My Notes");

                                    break;

//                                case R.id.nav_myCart:
//                                    gotoFragment(new MyCartFragment(), CART_FRAGMENT, "My Cart");
//                                    break;
//                                case R.id.nav_myOrders:
//                                    gotoFragment(new MyOrdersFragment(), ORDERS_FRAGMENT, "My Orders");
//                                    break;

                                case R.id.nav_shareBtn:
                                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                                    myIntent.setType("text/plain");
                                    String body = "https://play.google.com/store/apps/details?id=" + getPackageName();
                                    String sub = "Abhi Download Karo PlayStore se!";
                                    myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                                    myIntent.putExtra(Intent.EXTRA_TEXT,body);
                                    startActivity(Intent.createChooser(myIntent, "Share Using"));
                                    break;

                                case R.id.nav_notificationsFragment:
                                    setFragment(new NotificationsFragment(), NOTIFICATIONS_FRAGMENT);
                                    break;

                                case R.id.nav_myAccountFragment:
                                    gotoFragment(new MyAccountFragment(), ACCOUNT_FRAGMENT, "My Account");
                                    break;

                                case R.id.nav_youtubeBtn:
                                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);
                                    youtubeIntent.setData(Uri.parse(getString(R.string.yt_channel_url)));
                                    youtubeIntent.setPackage("com.google.android.youtube");
                                    startActivity(youtubeIntent);
                                    break;

                                case R.id.nav_telegramBTn:

                                    String packageName = "org.telegram.messenger";
                                    try {
                                        PackageManager pm = getApplicationContext().getPackageManager();
                                        pm.getPackageInfo(packageName, 0);

                                        Intent teleINtent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=" + getString(R.string.telegram_channel_id)));
                                        startActivity(teleINtent);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this, "Telegram App is Not Installed! " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                                case R.id.nav_supportBtn:
                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                    dialIntent.setData(Uri.parse("tel:9583650902"));
                                    startActivity(dialIntent);
                                    break;

                                case R.id.nav_aboutBtn:

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_condition_url)));
                                    startActivity(browserIntent);
                                    break;

                                case R.id.nav_signout:
                                    FirebaseAuth.getInstance().signOut();
                                    DBqueries.clearData();
                                    Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
                                    Intent signOutIntent = new Intent(MainActivity.this, RegisterActivity.class);
                                    startActivity(signOutIntent);
                                    finish();
                                    break;


                            }
                        }
                    });
                    return true;
                }        else {
                    signInDialog.show();
                    return false;
                }
            }
        });

    }

    public void checkFragment(){
        if (defaultFragment == -1) {
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        } else if (defaultFragment == 2) {
            gotoFragment(new MyCoursesFragment(), MY_COURSES_FRAGMENT, "My Courses");
            defaultFragment = -1;
        } else if (defaultFragment == 6) {
            gotoFragment(new MyTestsFragment(), MY_TEST_FRAGMENT, "My Tests");
            defaultFragment = -1;
        } else if (defaultFragment == 7) {
            gotoFragment(new MyNotesFragment(), MY_NOTES_FRAGMENT, "My Notes");
            defaultFragment = -1;
        }else {
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }
    }

    private void showDialog(String versionFromDB) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Update Available!")
                .setMessage("Please Update StudyInsta App to latest version: " + versionFromDB + " to use this app.")
                .setPositiveButton("Update Now", null)
                .show();
        dialog.setCancelable(false);

        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveBtn.setTextColor(Color.parseColor("#0F94FF"));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }catch (android.content.ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
    }

    public void getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            currentVersion =  Objects.requireNonNull(packageInfo).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("PackageInfoError","NameNotFoundException: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        /// Enable Disable Signout Button in navigation drawer
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        } else {
            FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DBqueries.fullName = task.getResult().getString("fullName");
                        DBqueries.email = task.getResult().getString("email");
                        DBqueries.phone = task.getResult().getString("phone");
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        invalidateOptionsMenu();
    }

    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkFragment();

        Bundle extras = intent.getExtras() ;
        if (extras != null ) {
            if (extras.containsKey( "fragmentNo" )) {

                int recievedFragNo = extras.getInt( "fragmentNo" ) ;

                if (recievedFragNo == 2){
                    gotoFragment(new MyCoursesFragment(), MY_COURSES_FRAGMENT, "My Courses");
                }else if (recievedFragNo == 7){
                    gotoFragment(new MyNotesFragment(), MY_NOTES_FRAGMENT, "My Notes");
                }else if (recievedFragNo == 6){
                    gotoFragment(new MyTestsFragment(), MY_TEST_FRAGMENT, "My Tests");
                }else {
                    Toast.makeText(this, "Something went wrong in launching the material!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (currentFragment == HOME_FRAGMENT) {
            getMenuInflater().inflate(R.menu.toolbar_menu_main_activity, menu);

//            MenuItem cartItem = menu.findItem(R.id.product_details_activity_cart);
//                cartItem.setActionView(R.layout.badge_layout);
//                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
//                badgeIcon.setImageResource(R.drawable.cart_icon);
//                badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (currentUser != null) {
//                    if (DBqueries.cartList.size() == 0){
//                        DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false, badgeCount, new TextView(MainActivity.this));
//                    }else {
//                        badgeCount.setVisibility(View.VISIBLE);
//                        if (DBqueries.cartList.size() < 99) {
//                            badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
//                        }else {
//                            badgeCount.setText("99");
//                        }
//                    }

                if (DBqueries.purchasedCoursesList.size() == 0) {
                    DBqueries.loadMyCourses(MainActivity.this, new Dialog(MainActivity.this), false);
                }
                if (DBqueries.purchasedTestsList.size() == 0) {
                    DBqueries.loadMyTests(MainActivity.this, new Dialog(MainActivity.this), false);
                }
                if (DBqueries.purchasedNotesList.size() == 0) {
                    DBqueries.loadMyNotes(MainActivity.this, new Dialog(MainActivity.this), false);
                }
            }

//                cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (currentUser == null) {
//                            signInDialog.show();
//                        }else {
//                            gotoFragment(new MyCartFragment(),CART_FRAGMENT, "My Cart");
//                        }
//                    }
//                });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.product_details_activity_search) {
//            //todo search
//            return true;
//        }else if (id == R.id.product_details_activity_cart){
//            if (currentUser == null) {
//                signInDialog.show();
//            }else {
//                gotoFragment(new MyCartFragment(),CART_FRAGMENT, "My Cart");
//            }
//            return true;
//    }
        if (id == R.id.main_activity_notifications) {


            gotoFragment(new NotificationsFragment(), NOTIFICATIONS_FRAGMENT, "Notifications");
            return true;
        } else if (id == android.R.id.home) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(Fragment fragment, int fragmentNo, String title) {
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        titleJhWarrior.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
        if (fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            getSupportActionBar().setTitle("StudyInsta");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null).commit();
        }

    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            if (currentFragment == HOME_FRAGMENT){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // very important line just below it will reload the fragment after a user finishes main activity and the
                            //app is relaunched
                            currentFragment = -1;
                            finish();

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#0F94FF"));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                }
            });

            alertDialog.show();


            }else {
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                }
            }

        }

}
