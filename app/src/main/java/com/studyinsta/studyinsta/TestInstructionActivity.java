package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studyinsta.studyinsta.classes.TestsMetaModel;

import java.util.List;

public class TestInstructionActivity extends AppCompatActivity {

    private Button startTestBtn;
    private String maxmMarks, title, prodId;
    private long maxmTime;
    private int setNo=1;
    private long nofTimesAttempted;
    public static Dialog loadingDialog;
    private boolean english = true, demo;
    private TestsMetaModel testsMetaModel;

    private List<String> stringL;

    private String sliderText;

    private String instructionHtmlText;


    private TextView titleTV, marksTV, timeTV, instructionTV;


    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference reference = database.getReference();

    private boolean maxTimeFetched = false, maxMarksFetched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_instruction);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Instruction");

        startTestBtn = findViewById(R.id.startTestBtn);
        marksTV = findViewById(R.id.maxMarksTV);
        instructionTV = findViewById(R.id.basicInstruction);
        timeTV = findViewById(R.id.timeLimitTV);
        titleTV = findViewById(R.id.testTitleTV);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        testsMetaModel = new TestsMetaModel();


        ///loading Dialog
        loadingDialog = new Dialog(TestInstructionActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        title = getIntent().getStringExtra("title");
        prodId =getIntent().getStringExtra("prodId");
        setNo = getIntent().getIntExtra("setNo",1);
        demo = getIntent().getBooleanExtra("isDemo",false);
        nofTimesAttempted = getIntent().getLongExtra("noOfTimesAttempted",0);

        Log.d("mylog", "onCreate: " + setNo + "   " + prodId);
        Toast.makeText(this, "" + setNo + "   " + prodId, Toast.LENGTH_SHORT).show();

        loadTestMeta();

        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intentFunction();

            }
        });

    }

    public void intentFunction(){
        if (maxmMarks != null) {
            Intent intent = new Intent(TestInstructionActivity.this, QuestionDisplayActivity.class);
            intent.putExtra("maxm_marks", maxmMarks);
            intent.putExtra("maxm_time", maxmTime);
            intent.putExtra("productId", prodId);
            intent.putExtra("setNo", setNo);
            intent.putExtra("timesAttempted", nofTimesAttempted);
            if (demo){
                intent.putExtra("isDemo", true);
            }
            startActivity(intent);
        }else {
            Toast.makeText(this, "Something Went Wrong! Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    public void loadTestMeta(){

        try {
            reference.child("QuestionsData").child("prodId3").child("set" + setNo).child("maxTime").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        maxmTime = snapshot.getValue(Long.class);

                    }
                    maxTimeFetched = true;
                    if (maxMarksFetched && maxTimeFetched) {
                        onDismissLoading();
                        loadingDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TestInstructionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    maxTimeFetched = false;
                }
            });

            reference.child("QuestionsData").child(prodId).child("set" + setNo).child("maxMarks").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        maxmMarks = String.valueOf(snapshot.getValue(Long.class));

                    }
                    maxMarksFetched = true;
                    if (maxMarksFetched && maxTimeFetched) {
                        onDismissLoading();
                        loadingDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TestInstructionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    maxTimeFetched = false;
                }
            });
        }catch (Exception e){
            Log.d("Database Query Eror:", e.getMessage());
            Toast.makeText(this, "Something Went Wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    public void onDismissLoading(){

        String englishInstruction = "<ul><li>You have to finish the test in " + maxmTime + " minutes.</li>" +
                "<li>This test can be attempted only twice.</li>" +
                "<li>Each question contains 4 option out of which only one is correct.</li>" +
                "<li>Negative marking in each question will be displayed at the top in red color.</li>" +
                "<li>Positive marking in each question will be displayed at the top in green color.</li>" +
                "<li>There is no negative marking for the question which you have not attempted.</li>" +
                "<li>After time is over, the test gets submitted automatically.</li>" +
                "<li>When you click on start test button, you agree to the instruction provided above.</li></ul>";

        String hindiinstruction = "<ul><li>परीक्षा समाप्त करने के लिए आपके पास "+maxmTime +" मिनट हैं।</li>" +
                "<li>इस परीक्षा में केवल दो बार प्रयास किया जा सकता है।</li>" +
                "<li>प्रत्येक प्रश्न में 4 विकल्प होते हैं जिनमें से केवल एक सही है।</li>" +
                "<li>प्रत्येक प्रश्न में नकारात्मक अंकन लाल रंग में ऊपर पर प्रदर्शित किया जाएगा।</li>" +
                "<li>हरेक प्रश्न में सकारात्मक अंकन को हरे रंग में सबसे ऊपर प्रदर्शित किया जाएगा।</li>" +
                "<li>उस प्रश्न के लिए कोई नकारात्मक अंकन नहीं है जिसे आपने टिक नहीं किया है।</li>" +
                "<li>समय समाप्त होने के बाद, परीक्षण स्वचालित रूप से सबमिट हो जाता है।</li>" +
                "<li>जब आप स्टार्ट टेस्ट बटन पर क्लिक करते हैं, तो आप ऊपर दिए गए निर्देश से सहमत होते हैं।</li></ul>";

        try {

            titleTV.setText(title+ " (" + setNo + ")");
            if (english) {
                startTestBtn.setText("START TEST");
                marksTV.setText("Max Marks: " + maxmMarks);
                timeTV.setText("Time Limit: " + maxmTime + " minutes");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    instructionTV.setText(Html.fromHtml(englishInstruction , Html.FROM_HTML_MODE_COMPACT));
                } else {
                    instructionTV.setText(Html.fromHtml(englishInstruction));
                }
            } else {
                startTestBtn.setText("स्टार्ट टेस्ट");
                marksTV.setText("अधिकतम अंक: " + maxmMarks);
                timeTV.setText("समय सीमा: " + maxmTime + " मिनट");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    instructionTV.setText(Html.fromHtml(hindiinstruction, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    instructionTV.setText(Html.fromHtml(hindiinstruction));
                }
            }
        }catch (Exception e){
            finish();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_analysis_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
            return true;
        } else if (id == R.id.langMenuSwitch){
            if (english){
                english =false;
                onDismissLoading();
            }else {
                english =true;
                onDismissLoading();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}