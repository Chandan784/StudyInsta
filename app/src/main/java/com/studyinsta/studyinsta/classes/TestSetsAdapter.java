package com.studyinsta.studyinsta.classes;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studyinsta.studyinsta.QuestionDisplayActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.TestAnalysisActivity;
import com.studyinsta.studyinsta.TestInstructionActivity;

import java.util.List;

public class TestSetsAdapter extends RecyclerView.Adapter<TestSetsAdapter.ViewHolder> {


    private String idOrUrl, prodTitle;
    private List<String> setNames;
    public static int TOTAL_POSSIBLE_ATTEMPTS = 6;
    //ye number change karoge to attempt badh jayega
    private boolean demo;
    private List<String> setNumbersList;

    public TestSetsAdapter(String idOrUrl, String prodTitle, List<String> setNames, boolean demo, List<String> setNumbers) {

        this.idOrUrl = idOrUrl;
        this.prodTitle = prodTitle;
        this.setNames = setNames;
        this.demo = demo;
        this.setNumbersList = setNumbers;
    }

    @NonNull
    @Override
    public TestSetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sets_adapter_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestSetsAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        int s = position +1;


        String title = setNames.get(position);

        if (demo){
            holder.setDataForDemo(position, setNames.get(position));
        }else {
            holder.setData(s, title);
        }
    }

    @Override
    public int getItemCount() {
        return setNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView setNameTv, attemptLeftTv;
        private TextView analyse, reattempt;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            setNameTv = itemView.findViewById(R.id.setNameTv);
            attemptLeftTv = itemView.findViewById(R.id.attemptLeftTv);

        }
        private void setDataForDemo(final int posit, String name){
            setNameTv.setText("" + (posit +1) + ") " + name);
            attemptLeftTv.setText("Unlimited Attempt");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QuestionDisplayActivity.FROM_TEST_SUBMISSION = true;

                    Intent intent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                    intent.putExtra("setNo", Integer.parseInt(setNumbersList.get(posit)));
                    intent.putExtra("prodId", idOrUrl);
                    intent.putExtra("title", prodTitle);
                    intent.putExtra("isDemo", true);
                    intent.putExtra("noOfTimesAttempted", 0);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
        private void setData(final int a, String name){
            setNameTv.setText("" + a + ") " + name);

                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    DocumentReference docIdRef = rootRef.collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_TESTS")
                            .collection("ATTEMPTED")
                            .document(idOrUrl);
                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    if (document.get(String.valueOf(a) + "_attempt_done") == null){
                                        //This set is unattempted
                                        attemptLeftTv.setText("" + TOTAL_POSSIBLE_ATTEMPTS + " Attempt Left");

                                        itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                QuestionDisplayActivity.FROM_TEST_SUBMISSION = true;

                                                Intent intent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                                                intent.putExtra("setNo", a);
                                                intent.putExtra("prodId", idOrUrl);
                                                intent.putExtra("title", prodTitle);
                                                intent.putExtra("noOfTimesAttempted", 0);
                                                itemView.getContext().startActivity(intent);

                                            }
                                        });
                                    }else {
                                        //This set is attempted
                                        setNameTv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#dc2eff")));
                                        final long attemptNumber = (long) document.get(String.valueOf(a) + "_attempt_done");
                                        long displayAttemptText = TOTAL_POSSIBLE_ATTEMPTS - attemptNumber;
                                        attemptLeftTv.setText("" + displayAttemptText + " Attempt Left");

                                        final Dialog questDialog = new Dialog(itemView.getContext());
                                        questDialog.setContentView(R.layout.test_reattempt_dialog);
                                        questDialog.setCancelable(true);

                                        analyse = questDialog.findViewById(R.id.analyseBtn);
                                        reattempt = questDialog.findViewById(R.id.reattemptBtn);

                                        if (attemptNumber < 4) {
                                            itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    questDialog.show();

                                                    analyse.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            questDialog.dismiss();
                                                            QuestionDisplayActivity.FROM_TEST_SUBMISSION = false;

                                                            Intent analysisIntent = new Intent(itemView.getContext(), TestAnalysisActivity.class);
                                                            analysisIntent.putExtra("setNo", a);
                                                            analysisIntent.putExtra("prodId", idOrUrl);
                                                            itemView.getContext().startActivity(analysisIntent);

                                                        }
                                                    });
                                                    reattempt.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            questDialog.dismiss();
                                                            QuestionDisplayActivity.FROM_TEST_SUBMISSION = true;
                                                            Intent intent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                                                            intent.putExtra("setNo", a);
                                                            intent.putExtra("prodId", idOrUrl);
                                                            intent.putExtra("title", prodTitle);
                                                            intent.putExtra("noOfTimesAttempted", attemptNumber);
                                                            itemView.getContext().startActivity(intent);
                                                        }
                                                    });

                                                }
                                            });
                                        } else {
                                            itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    reattempt.setVisibility(View.GONE);

                                                    questDialog.show();

                                                    analyse.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            questDialog.dismiss();
                                                            QuestionDisplayActivity.FROM_TEST_SUBMISSION = false;

                                                            Intent analysisIntent = new Intent(itemView.getContext(), TestAnalysisActivity.class);
                                                            analysisIntent.putExtra("setNo", a);
                                                            analysisIntent.putExtra("prodId", idOrUrl);
                                                            itemView.getContext().startActivity(analysisIntent);

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }

                                } else {
                                    //This whole test series is not attempted

                                    itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent intent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                                            intent.putExtra("setNo", a);
                                            intent.putExtra("prodId", idOrUrl);
                                            intent.putExtra("title", prodTitle);
                                            intent.putExtra("noOfTimesAttempted", 0);
                                            itemView.getContext().startActivity(intent);

                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(itemView.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });






//                }



        }


    }
}
