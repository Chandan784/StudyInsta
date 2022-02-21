package com.studyinsta.studyinsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studyinsta.studyinsta.QuestionDisplayActivity;
import com.studyinsta.studyinsta.R;

import java.util.ArrayList;
import java.util.List;

public class YourScoreFragment extends Fragment {

    public YourScoreFragment() {
        // Required empty public constructor
    }

    private TextView marksScoredLarge, maxMarks,
            percentage, totalQTv, correct, incorrect, unattempted, rankTv, appearedTv;
    private Double scoredMarks;
    private String maxMarksValue;
    private int thiRank, totalStudentsApeared;
    private int totalQuestions, totalCorrect=0, totalWrong=0, totalUnattempted=0;
    private static int CORRECT = 1, WRONG = 0, UNATTEMPED = 5;
    private static int selectedA = 0, selectedB = 1, selectedC = 2, selectedD = 3;
    private List<Integer> attemptStatusList ;
    public static List<Integer> selectedResponseList;
    private List<Long> downloadedList, downloadedMeta, tempSeReList;
    public static final int INDEX_CORRECT = 0, INDEX_INCORRECT = 1, INDEX_UNATTAMPTED = 2, INDEX_MAXMARKS= 3 ;

    private List<String> ranksUserIdList;
    private List<Double> rankScoresList;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_analysis_score_board, container, false);
         marksScoredLarge = view.findViewById(R.id.marksScoredLarge);
         maxMarks = view.findViewById(R.id.maxMarks);
         totalQTv = view.findViewById(R.id.totalQTv);
         percentage = view.findViewById(R.id.percentage);
         correct = view.findViewById(R.id.correctNumber);
         incorrect = view.findViewById(R.id.incorrectNumber);
         unattempted = view.findViewById(R.id.unattemptedNumber);
         rankTv = view.findViewById(R.id.myRankTv);
         appearedTv = view.findViewById(R.id.totalAppearedTv);

         database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
         reference = database.getReference();

        selectedResponseList = new ArrayList<>();
        attemptStatusList = new ArrayList<>();
        downloadedMeta = new ArrayList<>();
        downloadedList = new ArrayList<>();
        ranksUserIdList = new ArrayList<>();
        rankScoresList = new ArrayList<>();

        if(QuestionDisplayActivity.FROM_TEST_SUBMISSION){

            scoredMarks = getActivity().getIntent().getDoubleExtra("marks_scored", 0);
            maxMarksValue = getActivity().getIntent().getStringExtra("max_marks");
            selectedResponseList = getActivity().getIntent().getIntegerArrayListExtra("resoponce");
            attemptStatusList = getActivity().getIntent().getIntegerArrayListExtra("attempt_status");
            totalQuestions = getActivity().getIntent().getIntExtra("total_questions",0);
            thiRank = getActivity().getIntent().getIntExtra("my_rank",0);
            totalStudentsApeared = getActivity().getIntent().getIntExtra("attempted_total_students",0);

            getAttemptNumbers();

            marksScoredLarge.setText(String.format("%.2f", scoredMarks));
            totalQTv.setText("" + totalQuestions);
            maxMarks.setText(maxMarksValue);
            double percent = (scoredMarks/Double.parseDouble(maxMarksValue))*100;
            percentage.setText(String.format("%.2f", percent) + "%");

            rankTv.setText("" + thiRank);
            appearedTv.setText("" + totalStudentsApeared);


//            correct.setText(selectedResponseList.get(4).toString());
//            incorrect.setText(attemptStatusList.get(4).toString());


        }else {

            final int setNo = getActivity().getIntent().getIntExtra("setNo",1);
            String prodID = getActivity().getIntent().getStringExtra("prodId");
            if (prodID!= null) {

                reference.child("QuestionsData").child(prodID).child("set" + setNo).child("scores")
                        .orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ranksUserIdList.add(dataSnapshot.getKey());
                            rankScoresList.add(dataSnapshot.getValue(Double.class));
                        }

                        try {
                            totalStudentsApeared = rankScoresList.size();
                            int index = ranksUserIdList.indexOf(FirebaseAuth.getInstance().getUid());
                            thiRank = totalStudentsApeared - index;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else {
                Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            DocumentReference docIdRef = rootRef.collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA")
                    .document("MY_TESTS")
                    .collection("ATTEMPTED")
                    .document(prodID);
                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){
                                    tempSeReList = (List<Long>) document.get(String.valueOf(setNo));
                                    downloadedMeta = (List<Long>) document.get(String.valueOf(setNo) + "_meta");
                                    scoredMarks = (Double) document.get(String.valueOf(setNo) +"_marks_scored");
                                    maxMarksValue = downloadedMeta.get(INDEX_MAXMARKS).toString();
                                    totalQuestions = selectedResponseList.size();

                                    totalUnattempted = downloadedMeta.get(INDEX_UNATTAMPTED).intValue();
                                    totalCorrect = downloadedMeta.get(INDEX_CORRECT).intValue();
                                    totalWrong = downloadedMeta.get(INDEX_INCORRECT).intValue();
                                    totalQuestions = totalCorrect + totalWrong + totalUnattempted;

                                    for (int x = 0; x < tempSeReList.size(); x++){
                                        selectedResponseList.add(tempSeReList.get(x).intValue());
                                    }

                                    try {
                                        correct.setText("" + totalCorrect);
                                        incorrect.setText("" + totalWrong);
                                        unattempted.setText("" + totalUnattempted);
                                        rankTv.setText("" + thiRank);
                                        appearedTv.setText("" + totalStudentsApeared);

                                        marksScoredLarge.setText(String.format("%.2f", scoredMarks));
                                        totalQTv.setText("" + totalQuestions);
                                        maxMarks.setText(maxMarksValue);
                                        double percent = (scoredMarks / Double.parseDouble(maxMarksValue)) * 100;
                                        percentage.setText(String.format("%.2f", percent) + "%");
                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }else {
                                    //document does not exist
                                }
                            }else {

                            }
                        }
                    });
        }

         return view;
    }
    public void getAttemptNumbers(){
        for (int i = 0; i < totalQuestions; i++){
            if (attemptStatusList.get(i) == CORRECT){
                totalCorrect++;
            }
            if (attemptStatusList.get(i) == WRONG){
                totalWrong++;
            }
        }
        totalUnattempted = totalQuestions - (totalCorrect + totalWrong);

        correct.setText("" + totalCorrect);
        incorrect.setText("" + totalWrong);
        unattempted.setText("" + totalUnattempted);

    }
}