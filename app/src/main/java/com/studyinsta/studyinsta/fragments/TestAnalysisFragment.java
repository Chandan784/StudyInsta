package com.studyinsta.studyinsta.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studyinsta.studyinsta.QuestionDisplayActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.DBqueries;
import com.studyinsta.studyinsta.classes.QuestionAnalysisAdapter;
import com.studyinsta.studyinsta.classes.QuestionsModel;

import java.util.ArrayList;
import java.util.List;

public class TestAnalysisFragment extends Fragment {

    private List<Integer> selectedResponseList;
    private RecyclerView recyclerView;
    private QuestionAnalysisAdapter adapter;
    private boolean english, languageSwitchable, englishLoaded, hindiLoaded;
    private String productID = "productId";
    private int setNo = 1;
    private List<Long> selectedResponseDownloadedList;
    public static Dialog loadingDialog;


    private List<QuestionsModel> englishQuestionData = new ArrayList<>();
    private List<QuestionsModel> hindiQuestionData = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://study-insta-2c548-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private DatabaseReference reference = database.getReference();

    public TestAnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis_questions_analyse, container, false);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        recyclerView = view.findViewById(R.id.questRv);

        selectedResponseList = new ArrayList<>();
        selectedResponseDownloadedList = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        if (QuestionDisplayActivity.FROM_TEST_SUBMISSION){
            languageSwitchable = getActivity().getIntent().getBooleanExtra("switchable_lang", false);
            english = getActivity().getIntent().getBooleanExtra("default_lang",false);
            selectedResponseList = getActivity().getIntent().getIntegerArrayListExtra("resoponce");

            setView();

        }else {

            ///loading Dialog
            loadingDialog = new Dialog(getContext());
            loadingDialog.setContentView(R.layout.loading_progress_dialog);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingDialog.show();
            /////end loading dialog


            productID = getActivity().getIntent().getStringExtra("prodId");
            setNo = getActivity().getIntent().getIntExtra("setNo", 1);

            selectedResponseList = YourScoreFragment.selectedResponseList;

            loadEnglishData();
            loadHindiData();

        }

        return view;
    }

    public void isLanguageSwitchable(){
        if(englishQuestionData.size() >= hindiQuestionData.size()){
            //question in english is greater or equal to hindi
            english = true;
            if (hindiQuestionData.size() == englishQuestionData.size()){
                //questions in english = hindi
                languageSwitchable = true;
            }else {
                //question in english is not equal to hindi
                languageSwitchable = false;
            }
        }else{
            //questions in hindi > english
            languageSwitchable = false;
            english = false;
        }
    }

    public void  setView(){
        if (english) {
            adapter = new QuestionAnalysisAdapter(DBqueries.englishListToTransfer, selectedResponseList);
        }else {
            adapter = new QuestionAnalysisAdapter(DBqueries.hindiListToTransfer, selectedResponseList);
        }
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void loadEnglishData(){
        reference.child("QuestionsData").child(productID).child("set" + setNo).child("english")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            englishQuestionData.add(snapshot.getValue(QuestionsModel.class));
                        }

                        englishLoaded = true;

                        if (englishLoaded && hindiLoaded){
                           performWhenQuestionsLoaded();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        englishLoaded =false;
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void loadHindiData(){
        reference.child("QuestionsData").child(productID).child("set" + setNo).child("hindi")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            hindiQuestionData.add(snapshot.getValue(QuestionsModel.class));
                        }
                        hindiLoaded = true;

                        if (englishLoaded && hindiLoaded){
                           performWhenQuestionsLoaded();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hindiLoaded = false;
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void performWhenQuestionsLoaded(){
        DBqueries.englishListToTransfer.clear();
        DBqueries.hindiListToTransfer.clear();
        if (englishQuestionData.size() == hindiQuestionData.size()) {
            for (int i = 0; i < YourScoreFragment.selectedResponseList.size(); i++) {
                DBqueries.englishListToTransfer.add(englishQuestionData.get(i));
                DBqueries.hindiListToTransfer.add(hindiQuestionData.get(i));
            }
        }else if (englishQuestionData.size() > hindiQuestionData.size()){
            for (int i = 0; i < YourScoreFragment.selectedResponseList.size(); i++) {
                DBqueries.englishListToTransfer.add(englishQuestionData.get(i));
            }
        }else if (englishQuestionData.size() < hindiQuestionData.size()){
            for (int i = 0; i < YourScoreFragment.selectedResponseList.size(); i++) {
                DBqueries.hindiListToTransfer.add(hindiQuestionData.get(i));
            }
        }

        isLanguageSwitchable();
        loadingDialog.dismiss();
        setView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.test_analysis_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.langMenuSwitch){
            if(languageSwitchable){
                if (english){
                    english = false;
                }else {
                    english = true;
                }
                setView();
            }else {
                Toast.makeText(getContext(), "Analysis is not available in any other language!", Toast.LENGTH_SHORT).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }
}