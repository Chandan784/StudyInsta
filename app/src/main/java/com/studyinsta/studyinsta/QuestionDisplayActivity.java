package com.studyinsta.studyinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.OnSwipeTouchListener;
import com.studyinsta.studyinsta.classes.QuestionJumpingAdapter;
import com.studyinsta.studyinsta.classes.QuestionsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionDisplayActivity extends AppCompatActivity {


    private TextView timer, questionNo, positiveMarks, negativeMarks, questionText, maxMarksTV;
    private ImageView questionImageIv;
    private Button allQuestions, languageSwitch, submitButton, optionA, optionB,
            optionC, optionD, previousBtn, clrSelctionBtn, nextBtn;
    private LinearLayout optionsContainer;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference reference = database.getReference();
    private List<QuestionsModel> englishQuestionData;
    private List<QuestionsModel> hindiQuestionData;
    public Boolean english, languageSwitchable;
    private int totalQuestions = 0, setNumbr = 1, totalEngQuestions, totalHindiQuestions;
    public static Dialog loadingDialog;
    private boolean metaLoaded, englishDataLoaded, hindiDataLoaded,
            maxTimeFetched, maxMarksFetched;
    private RecyclerView questionMatrixRecyclerView;
    public static Dialog questDialog;
    public static int currentQuestion = 0, yourRank, totalStudentsAttempted ;

    public static boolean FROM_TEST_SUBMISSION;
    public static final int ENGLISH = 1, HINDI = 2;
    private boolean isDemo;

    private ConstraintLayout rootLayout;

    private int totalCorrect=0, totalWrong=0, totalUnattempted=0;
    private long noOfTimesAttempted;
    private List<Integer> attemptDetailsUploadList;


    private String maxMarks, productID;
    private long maximumTime;
    private List<Integer> selectedResponsce;
    //below list will contain that weather the selected option was correct wrong or unattempted
    //(1 means correct and 0 means wrong and 2 means unattempted)
    private List<Integer> attemptStatus;
    private static int CORRECT = 1, WRONG = 0, UNATTEMPED = 5;
    private static int selectedA = 0, selectedB = 1, selectedC = 2, selectedD = 3;

    private Double scoredMarks = 0.00;

    private List<String> ranksUserIdList;
    private List<Double> rankScoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_display);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        timer = findViewById(R.id.timerTv);
        questionNo = findViewById(R.id.questionNumberTv);
        positiveMarks = findViewById(R.id.positiveMarks);
        negativeMarks = findViewById(R.id.negativeMarks);
        questionText = findViewById(R.id.questionText);
        allQuestions = findViewById(R.id.allQuestions);
        maxMarksTV = findViewById(R.id.maxMarkstv);
        languageSwitch = findViewById(R.id.lang_switch);
        submitButton= findViewById(R.id.submitBtn);
        optionA = findViewById(R.id.optA);
        optionB = findViewById(R.id.optB);
        optionC = findViewById(R.id.optC);
        optionD = findViewById(R.id.optD);
        previousBtn = findViewById(R.id.prevBtn);
        clrSelctionBtn = findViewById(R.id.clrBtn);
        nextBtn = findViewById(R.id.nextBtn);
        optionsContainer = findViewById(R.id.optionsContainer);
        rootLayout = findViewById(R.id.questionDisplayConstrLt);
        questionImageIv = findViewById(R.id.questionImage);

        englishQuestionData = new ArrayList<>();
        hindiQuestionData = new ArrayList<>();
        attemptStatus = new ArrayList<>();
        selectedResponsce  = new ArrayList<>();
        rankScoresList = new ArrayList<>();
        ranksUserIdList = new ArrayList<>();

        ///loading Dialog
        loadingDialog = new Dialog(QuestionDisplayActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        /////end loading dialog

        FROM_TEST_SUBMISSION = true;
        attemptDetailsUploadList = new ArrayList<>();

        maximumTime = getIntent().getLongExtra("maxm_time", 0);
        maxMarks = getIntent().getStringExtra("maxm_marks");
        productID = getIntent().getStringExtra("productId");
        setNumbr = getIntent().getIntExtra("setNo",1);
        noOfTimesAttempted = getIntent().getLongExtra("timesAttempted",0);
        isDemo = getIntent().getBooleanExtra("isDemo", false);

        loadEnglishData();
        loadHindiData();

        questionNo.setText("Q. " + currentQuestion);

        //implemented swipe feature
        rootLayout.setOnTouchListener(new OnSwipeTouchListener(QuestionDisplayActivity.this) {
            @Override
            public void onSwipeLeft() {
                //go to next button
                if (currentQuestion < totalQuestions- 1){
                    currentQuestion++;
                    loadQuestionOnRequest(currentQuestion, maxMarks);
                }
            }

            @Override
            public void onSwipeRight() {
                //go to previous question
                if (currentQuestion > 0) {
                    currentQuestion--;
                    loadQuestionOnRequest(currentQuestion, maxMarks);
                }

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSubmitDialog();

            }
        });

        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (languageSwitchable){
                    if (english){
                        english = false;
                        setQuestions(currentQuestion, maxMarks);
                    }else {
                        english = true;
                        setQuestions(currentQuestion, maxMarks);
                    }
                }else {
                    Toast.makeText(QuestionDisplayActivity.this, "Questions are not available in any other language.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        allQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                questDialog = new Dialog(QuestionDisplayActivity.this);
                questDialog.setContentView(R.layout.question_bar_dialog_layout);
                questDialog.setCancelable(true);
                questDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
                questDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                questDialog.show();

                questionMatrixRecyclerView = questDialog.findViewById(R.id.questionMatrixRV);

                int noOfColumns = 5;
                GridLayoutManager layoutManager = new GridLayoutManager(QuestionDisplayActivity.this,noOfColumns);
                questionMatrixRecyclerView.setLayoutManager(layoutManager);

                QuestionJumpingAdapter adapter = new QuestionJumpingAdapter(totalQuestions);
                questionMatrixRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                questDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadQuestionOnRequest(currentQuestion, maxMarks);
                    }
                });
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion < totalQuestions- 1){
                    currentQuestion++;
                    loadQuestionOnRequest(currentQuestion, maxMarks);
                }
            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion > 0) {
                    currentQuestion--;
                    loadQuestionOnRequest(currentQuestion, maxMarks);
                }
            }
        });
        clrSelctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetOptionsBehaviour();
                normaliseMarks(currentQuestion);
                selectedResponsce.set(currentQuestion, UNATTEMPED);
                attemptStatus.set(currentQuestion, UNATTEMPED);
            }
        });

//        Action When Any Option Is Clicked
        for (int i = 0; i < 4; i++){
            final int index = i;
            optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String optionValue = ((Button)v).getText().toString().substring(3).trim();
                    ((Button)v).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7cc3fc")));
                    selectedResponsce.set(currentQuestion, index);
                    checkAnswer(optionValue, currentQuestion);
                }
            });

//         End Action When Any Option Is Clicked
        }
    }

    public void getAttemptNumbers(){
        for (int i = 0; i < totalQuestions; i++){
            if (attemptStatus.get(i) == CORRECT){
                totalCorrect++;
            }
            if (attemptStatus.get(i) == WRONG){
                totalWrong++;
            }
        }
        totalUnattempted = totalQuestions - (totalCorrect + totalWrong);

        attemptDetailsUploadList.add(totalCorrect);
        attemptDetailsUploadList.add(totalWrong);
        attemptDetailsUploadList.add(totalUnattempted);
        attemptDetailsUploadList.add(Integer.parseInt(maxMarks));
    }

    public void showSubmitDialog(){
        final AlertDialog warningDialog = new AlertDialog.Builder(QuestionDisplayActivity.this)
                .setTitle("Are You Sure You Want To Submit?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .show();
        warningDialog.setCancelable(true);
        Button positiveBtn = warningDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeBtn = warningDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeBtn.setTextColor(Color.parseColor("#0F94FF"));
        positiveBtn.setTextColor(Color.parseColor("#DF1400"));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                whenTestFinished();

            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
            }
        });
    }

    public void setThisTestAttempted(){
        Map<String, Object> docData = new HashMap<>();
        docData.put(String.valueOf(setNumbr),  selectedResponsce);
        docData.put(String.valueOf(setNumbr) + "_meta", attemptDetailsUploadList);
        docData.put(String.valueOf(setNumbr) +"_marks_scored", scoredMarks);
        docData.put(String.valueOf(setNumbr) +"_attempt_done", noOfTimesAttempted + 1);

        FirebaseFirestore.getInstance().collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_TESTS")
                .collection("ATTEMPTED")
                .document(productID)
                .update(docData).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                }else {
                    Map<String, Object> docData = new HashMap<>();
                    docData.put(String.valueOf(setNumbr),  selectedResponsce);
                    docData.put(String.valueOf(setNumbr) + "_meta", attemptDetailsUploadList);
                    docData.put(String.valueOf(setNumbr) +"_marks_scored", scoredMarks);
                    docData.put(String.valueOf(setNumbr) +"_attempt_done", noOfTimesAttempted + 1);

                    FirebaseFirestore.getInstance().collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_TESTS")
                            .collection("ATTEMPTED")
                            .document(productID)
                            .set(docData);
                }
            }
        });
    }

    public void openNextActivity(){
        Intent intent = new Intent(QuestionDisplayActivity.this, TestAnalysisActivity.class);
        //Sent to Test analysis Fragment
        intent.putExtra("default_lang", english);
        intent.putExtra("switchable_lang", languageSwitchable);
        //End Sent to Test analysis Fragment

        intent.putExtra("marks_scored",scoredMarks);

        intent.putExtra("attempted_total_students",totalStudentsAttempted);
        intent.putExtra("my_rank",yourRank);

        intent.putExtra("max_marks",maxMarks);
        intent.putExtra("total_questions",totalQuestions);
        intent.putIntegerArrayListExtra("resoponce", (ArrayList<Integer>) selectedResponsce);
        intent.putIntegerArrayListExtra("attempt_status", (ArrayList<Integer>) attemptStatus);
        //End Sent to Your Score Fragment
        startActivity(intent);
        finish();
    }

    public void loadQuestionOnRequest(int currentQuestion, String maxMarks){
        setQuestions(currentQuestion, maxMarks);
        resetOptionsBehaviour();
        retrieveAttemptDetails(currentQuestion);
    }

    public void postScoreOnServer(){
        final String user = FirebaseAuth.getInstance().getUid();

        reference.child("QuestionsData").child(productID).child("set" + setNumbr).child("scores")
                .child(user).setValue(scoredMarks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    getAttemptNumbers();
                    setThisTestAttempted();
                    retrieveRank(user);

                }else {
                    Toast.makeText(QuestionDisplayActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
            }
        });
    }

    public void whenTestFinished(){
        loadingDialog.show();

        if (isDemo){
            totalStudentsAttempted = 0;
            yourRank = 0;
            openNextActivity();

        }else {
            postScoreOnServer();
        }

    }

    public void retrieveRank(final String id){
        reference.child("QuestionsData").child(productID).child("set" + setNumbr).child("scores")
                .orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ranksUserIdList.add(dataSnapshot.getKey());
                    rankScoresList.add(dataSnapshot.getValue(Double.class));
                }

                totalStudentsAttempted = rankScoresList.size();
                int index = ranksUserIdList.indexOf(id);
                yourRank = totalStudentsAttempted - index;

                openNextActivity();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUnavailable(){
        languageSwitchable = false;
    }

    public void setQuestions(int questionIndex, String maxMarksValue){
        questionNo.setText("Q. " + (currentQuestion + 1) +"/" + totalQuestions);
        if (english) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if(englishQuestionData.get(questionIndex).q.contains(" ")){
                    questionImageIv.setVisibility(View.GONE);
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(Html.fromHtml(englishQuestionData.get(questionIndex).q, Html.FROM_HTML_MODE_COMPACT));

                }else {
                    questionText.setVisibility(View.GONE);
                    questionImageIv.setVisibility(View.VISIBLE);
                    Glide.with(this).load(englishQuestionData.get(questionIndex).q).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questionImageIv);

                }
                optionA.setText("1) " + Html.fromHtml(englishQuestionData.get(questionIndex).a, Html.FROM_HTML_MODE_COMPACT));
                optionB.setText("2) " + Html.fromHtml(englishQuestionData.get(questionIndex).b, Html.FROM_HTML_MODE_COMPACT));
                optionC.setText("3) " + Html.fromHtml(englishQuestionData.get(questionIndex).c, Html.FROM_HTML_MODE_COMPACT));
                optionD.setText("4) " + Html.fromHtml(englishQuestionData.get(questionIndex).d, Html.FROM_HTML_MODE_COMPACT));
                positiveMarks.setText("+" + englishQuestionData.get(questionIndex).positiveGrade);
                negativeMarks.setText("-" + Html.fromHtml(englishQuestionData.get(questionIndex).negativeGrade.toString(), Html.FROM_HTML_MODE_COMPACT));

                maxMarksTV.setText("Max. Marks: " + maxMarksValue);
            }else {

                if(englishQuestionData.get(questionIndex).q.contains(" ")){
                    questionImageIv.setVisibility(View.GONE);
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(Html.fromHtml(englishQuestionData.get(questionIndex).q));

                }else {
                    questionText.setVisibility(View.GONE);
                    questionImageIv.setVisibility(View.VISIBLE);

                    Glide.with(this).load(englishQuestionData.get(questionIndex).q).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questionImageIv);

                }
                optionA.setText("1) " + Html.fromHtml(englishQuestionData.get(questionIndex).a));
                optionB.setText("2) " + Html.fromHtml(englishQuestionData.get(questionIndex).b));
                optionC.setText("3) " + Html.fromHtml(englishQuestionData.get(questionIndex).c));
                optionD.setText("4) " + Html.fromHtml(englishQuestionData.get(questionIndex).d));
                positiveMarks.setText("+" + englishQuestionData.get(questionIndex).positiveGrade);

                negativeMarks.setText("-" + Html.fromHtml(englishQuestionData.get(questionIndex).negativeGrade.toString()));

                maxMarksTV.setText("Max. Marks: " + maxMarksValue);
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                if(hindiQuestionData.get(questionIndex).q.contains(" ")){
                    questionImageIv.setVisibility(View.GONE);
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(Html.fromHtml(hindiQuestionData.get(questionIndex).q, Html.FROM_HTML_MODE_COMPACT));

                }else {
                    questionText.setVisibility(View.GONE);
                    questionImageIv.setVisibility(View.VISIBLE);

                    Glide.with(this).load(hindiQuestionData.get(questionIndex).q).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questionImageIv);

                }
                optionA.setText("1) " + Html.fromHtml(hindiQuestionData.get(questionIndex).a, Html.FROM_HTML_MODE_COMPACT));
                optionB.setText("2) " + Html.fromHtml(hindiQuestionData.get(questionIndex).b, Html.FROM_HTML_MODE_COMPACT));
                optionC.setText("3) " + Html.fromHtml(hindiQuestionData.get(questionIndex).c, Html.FROM_HTML_MODE_COMPACT));
                optionD.setText("4) " + Html.fromHtml(hindiQuestionData.get(questionIndex).d, Html.FROM_HTML_MODE_COMPACT));
                positiveMarks.setText("+" + hindiQuestionData.get(questionIndex).positiveGrade);
                negativeMarks.setText("-" + Html.fromHtml(hindiQuestionData.get(questionIndex).negativeGrade.toString(), Html.FROM_HTML_MODE_COMPACT));

            } else {

                if(hindiQuestionData.get(questionIndex).q.contains(" ")){
                    questionImageIv.setVisibility(View.GONE);
                    questionText.setVisibility(View.VISIBLE);
                    questionText.setText(Html.fromHtml(hindiQuestionData.get(questionIndex).q));


                }else {
                    questionText.setVisibility(View.GONE);
                    questionImageIv.setVisibility(View.VISIBLE);
                    Glide.with(this).load(hindiQuestionData.get(questionIndex).q).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questionImageIv);

                }
                optionA.setText("1) " + Html.fromHtml(hindiQuestionData.get(questionIndex).a));
                optionB.setText("2) " + Html.fromHtml(hindiQuestionData.get(questionIndex).b));
                optionC.setText("3) " + Html.fromHtml(hindiQuestionData.get(questionIndex).c));
                optionD.setText("4) " + Html.fromHtml(hindiQuestionData.get(questionIndex).d));
                positiveMarks.setText("+" + hindiQuestionData.get(questionIndex).positiveGrade);
                negativeMarks.setText("-" + Html.fromHtml(hindiQuestionData.get(questionIndex).negativeGrade.toString()));

            }

            maxMarksTV.setText("अधिकतम अंक: " + maxMarksValue);
        }

    }

    public void startTimer(){
        //todo:use maxTime Variable to set time in count down
        long givenTime = maximumTime*60*1000;
        new CountDownTimer(givenTime, 1000){
            public void onTick(long millisUntilFinished){
//                timer.setText(String.valueOf(millisUntilFinished/1000));
                String secondsStr, minutesStr;
                int seconds = (int) millisUntilFinished/1000;
                int showSeconds = seconds % 60;
                int minutes = seconds / 60;
                int showMinutes = minutes % 60;
                int hours = minutes / 60;
                if (showSeconds < 10) {
                    secondsStr = "0" + String.valueOf(showSeconds);
                }else {
                    secondsStr = String.valueOf(showSeconds);
                } if (minutes < 10){
                    minutesStr = "0" + String.valueOf(showMinutes);
                }else {
                    minutesStr = String.valueOf(showMinutes);
                }
                timer.setText("0"+hours + ":" + minutesStr + ":" + secondsStr);
            }
            public  void onFinish(){
                whenTestFinished();

            }
        }.start();
    }

    public void loadHindiData(){
        try {
            reference.child("QuestionsData").child(productID).child("set" + setNumbr).child("hindi")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                hindiQuestionData.add(snapshot.getValue(QuestionsModel.class));
                            }
                            totalHindiQuestions = hindiQuestionData.size();
                            hindiDataLoaded = true;
                            dismissLoading();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QuestionDisplayActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            hindiDataLoaded = false;
                            setUnavailable();
                        }

                    });
        }catch (Exception e){
            e.printStackTrace();
            setUnavailable();
        }
    }

    public void checkAnswer(String selectedAnswer, int questionIndex){

        isOptionClickable(false);
        if (english){
            String correctEngOption = englishQuestionData.get(currentQuestion).correct.trim();
            if (selectedAnswer.equals(correctEngOption)){
                attemptStatus.set(currentQuestion, CORRECT);
                scoredMarks = scoredMarks + Double.parseDouble(englishQuestionData.get(questionIndex).positiveGrade.toString());
                System.out.println("Scored: " + scoredMarks + " correct");

            }else {
                attemptStatus.set(currentQuestion, WRONG);
                scoredMarks = scoredMarks - englishQuestionData.get(questionIndex).negativeGrade.doubleValue();
                System.out.println("Scored: " + scoredMarks + " wrong");

            }
        }else {
            String correctHinOption = hindiQuestionData.get(currentQuestion).correct.trim();

            if (selectedAnswer.equals(correctHinOption)){
                attemptStatus.set(currentQuestion, CORRECT);
                scoredMarks = scoredMarks + Double.parseDouble(hindiQuestionData.get(questionIndex).positiveGrade.toString());
                System.out.println("Scored: " + scoredMarks + " correct");
            }else {
                attemptStatus.set(currentQuestion, WRONG);
                scoredMarks = scoredMarks - hindiQuestionData.get(questionIndex).negativeGrade.doubleValue();
                System.out.println("Scored: " + scoredMarks + " wrong");
            }
        }
    }

    public void isOptionClickable(boolean value){
        for (int i = 0; i < 4; i++){
            optionsContainer.getChildAt(i).setClickable(value);
        }
    }

    public void normaliseMarks(int questionIndex){
        if (!(Integer.parseInt(attemptStatus.get(questionIndex).toString()) == UNATTEMPED)) {
            if (english) {
                if (attemptStatus.get(questionIndex) == CORRECT) {
                    scoredMarks = scoredMarks - Double.parseDouble(englishQuestionData.get(questionIndex).positiveGrade.toString());
                } else if (attemptStatus.get(questionIndex) == WRONG) {
                    scoredMarks = scoredMarks + englishQuestionData.get(questionIndex).negativeGrade.doubleValue();
                }
            } else {
                if (attemptStatus.get(questionIndex) == CORRECT) {
                    scoredMarks = scoredMarks - Double.parseDouble(hindiQuestionData.get(questionIndex).positiveGrade.toString());
                } else if (attemptStatus.get(questionIndex) == WRONG) {
                    scoredMarks = scoredMarks + hindiQuestionData.get(questionIndex).negativeGrade.doubleValue();
                }
            }

        }
    }

    public void loadEnglishData(){
        try {

            reference.child("QuestionsData").child(productID).child("set" + setNumbr).child("english")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                englishQuestionData.add(snapshot.getValue(QuestionsModel.class));
                            }
                            totalEngQuestions = englishQuestionData.size();
                            englishDataLoaded = true;
                            dismissLoading();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(QuestionDisplayActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            englishDataLoaded = false;
                        }
                    });

        }catch (Exception e){
            setUnavailable();
            e.printStackTrace();
        }

    }

    public void resetOptionsBehaviour() {
        isOptionClickable(true);
        for (int i = 0; i < 4; i++) {
            optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    public void retrieveAttemptDetails(int questionIndex){
        if (!(Integer.parseInt(attemptStatus.get(questionIndex).toString()) == UNATTEMPED)){
            int x = selectedResponsce.get(questionIndex);
//            Toast.makeText(this, "" + x, Toast.LENGTH_SHORT).show();
            optionsContainer.getChildAt(x).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#7cc3fc")));
            isOptionClickable(false);
        }
    }


    public void dismissLoading(){
        if ( hindiDataLoaded && englishDataLoaded) {
            loadingDialog.dismiss();
            currentQuestion = 0;

            if(totalEngQuestions >= totalHindiQuestions){
                //question in english is greater or equal to hindi
                english = true;
                if (totalHindiQuestions == totalEngQuestions){
                    //questions in english = hindi
                    languageSwitchable = true;
                    totalQuestions = totalHindiQuestions;
                }else {
                    //question in english is not equal to hindi
                    languageSwitchable = false;
                    totalQuestions = totalEngQuestions;
                }
                setQuestions(currentQuestion, maxMarks);
            }else{
                //questions in hindi > english
                languageSwitchable = false;
                totalQuestions = totalHindiQuestions;
                english = false;
                setQuestions(currentQuestion, maxMarks);
            }

            startTimer();
            for (int i = 0; i < totalQuestions; i++) {
                attemptStatus.add(UNATTEMPED);
                selectedResponsce.add(UNATTEMPED);
            }
            DBqueries.englishListToTransfer.clear();
            DBqueries.hindiListToTransfer.clear();
            if (languageSwitchable){
                    for (int i = 0; i < totalQuestions; i++){
                    DBqueries.englishListToTransfer.add(englishQuestionData.get(i));
                    DBqueries.hindiListToTransfer.add(hindiQuestionData.get(i));
                    }
            }else{
                if (english){
                    for (int i = 0; i < totalQuestions; i++){
                        DBqueries.englishListToTransfer.add(englishQuestionData.get(i));
                    }

                }else {
                    for (int i = 0; i < totalQuestions; i++){
                        DBqueries.hindiListToTransfer.add(hindiQuestionData.get(i));
                    }
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are You Sure You Want To Exit?")
                .setPositiveButton("Yes", null)
                .setNegativeButton("No", null)
                .setCancelable(true)
                .show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dialog.setMessage(Html.fromHtml("If you will Exit then your test will be cancelled. <br><br> You can minimise this app and then come back again to the test.<br><br>Press <b>Yes</b> if you want to <b>Exit</b>", Html.FROM_HTML_MODE_COMPACT));
        }else {
            dialog.setMessage(Html.fromHtml("If you will Exit then your test will be cancelled. <br><br> You can minimise this app and then come back again to the test.<br><br>Press <b>Yes</b> if you want to <b>Exit</b>"));
        }

        Button positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeBtn.setTextColor(Color.parseColor("#0F94FF"));
        positiveBtn.setTextColor(Color.parseColor("#000000"));
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



//        super.onBackPressed();
    }

}