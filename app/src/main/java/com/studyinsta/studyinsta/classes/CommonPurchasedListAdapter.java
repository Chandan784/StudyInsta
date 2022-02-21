package com.studyinsta.studyinsta.classes;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.studyinsta.studyinsta.CourseSubjectsActivity;
import com.studyinsta.studyinsta.LecturePlayActivity;
import com.studyinsta.studyinsta.NotesDisplayActivity;
import com.studyinsta.studyinsta.QuestionDisplayActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.TestAnalysisActivity;
import com.studyinsta.studyinsta.TestInstructionActivity;

import java.util.List;


public class CommonPurchasedListAdapter extends RecyclerView.Adapter<CommonPurchasedListAdapter.ViewHolder> {

    private List<CommonPurchasedListModel> commonPurchasedListModelList;

    public CommonPurchasedListAdapter(List<CommonPurchasedListModel> commonPurchasedListModelList) {
        this.commonPurchasedListModelList = commonPurchasedListModelList;
    }

    @NonNull
    @Override
    public CommonPurchasedListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_material_title_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonPurchasedListAdapter.ViewHolder viewHolder, int position) {

        String title = commonPurchasedListModelList.get(position).getTitle();
        int type = commonPurchasedListModelList.get(position).getMaterialType();
        String idOrLink = commonPurchasedListModelList.get(position).getIdOrUrl();
        viewHolder.setData(title, type, idOrLink);
    }

    @Override
    public int getItemCount() {
        return commonPurchasedListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleText;
        private ImageView icon, tickMark;
        private TextView analyse, reattempt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.purchased_material_title);
            icon = itemView.findViewById(R.id.purchased_material_icon);
            tickMark = itemView.findViewById(R.id.tick_icon);
        }

        private void setData(final String title, int type, final String idOrUrl) {
            titleText.setText(title);

            if (!title.equals("")) {
                //Setting Icon image && Click Listener
                if (type == CourseSubjectsActivity.LECTURE) {
                    icon.setImageResource(R.drawable.lecture_play_icon);
                    if(!idOrUrl.equals("")) {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent lectureIntent = new Intent(itemView.getContext(), LecturePlayActivity.class);
                                lectureIntent.putExtra("course_lecture_url", idOrUrl);
                                itemView.getContext().startActivity(lectureIntent);
                            }
                        });
                    }else {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(itemView.getContext(),"Video Not Available! Please Contact Jharkhand Warrior Team.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (type == CourseSubjectsActivity.NOTES) {
                    icon.setImageResource(R.drawable.notes_icon_blue);

                    if(!idOrUrl.equals("")) {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent notesIntent = new Intent(itemView.getContext(), NotesDisplayActivity.class);
                                notesIntent.putExtra("course_notes_url", idOrUrl);
                                itemView.getContext().startActivity(notesIntent);
                            }
                        });
                    }else {

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(itemView.getContext(), "This Notes is Not Available! Please Contact Jharkhand Warrior Team.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (type == CourseSubjectsActivity.TEST) {
                    icon.setImageResource(R.drawable.tests_icon_blue);
                    if(!idOrUrl.equals("")) {

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
                                        //Attempted
                                        tickMark.setVisibility(View.VISIBLE);

                                        itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final Dialog questDialog = new Dialog(itemView.getContext());
                                                questDialog.setContentView(R.layout.test_reattempt_dialog);
                                                questDialog.setCancelable(true);
                                                questDialog.show();

                                                analyse = questDialog.findViewById(R.id.analyseBtn);
                                                reattempt = questDialog.findViewById(R.id.reattemptBtn);

                                                analyse.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        questDialog.dismiss();
                                                        QuestionDisplayActivity.FROM_TEST_SUBMISSION = false;

                                                        Intent analysisIntent = new Intent(itemView.getContext(), TestAnalysisActivity.class);
                                                        analysisIntent.putExtra("setNo", 1);
                                                        analysisIntent.putExtra("prodId", idOrUrl);
                                                        itemView.getContext().startActivity(analysisIntent);

                                                    }
                                                });
                                                reattempt.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        questDialog.dismiss();
                                                        QuestionDisplayActivity.FROM_TEST_SUBMISSION = true;

                                                        Intent testIntent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                                                        testIntent.putExtra("prodId", idOrUrl);
                                                        testIntent.putExtra("setNo",1);
                                                        testIntent.putExtra("title", title);
                                                        itemView.getContext().startActivity(testIntent);
                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        //Not Attempted
                                        itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                QuestionDisplayActivity.FROM_TEST_SUBMISSION = true;
                                                Intent testIntent = new Intent(itemView.getContext(), TestInstructionActivity.class);
                                                testIntent.putExtra("prodId", idOrUrl);
                                                testIntent.putExtra("setNo",1);
                                                testIntent.putExtra("title", title);
                                                itemView.getContext().startActivity(testIntent);
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(itemView.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });






                    }else {
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(itemView.getContext(), "Test is Not Ready! Please Try Again Later...", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }

            }
        }
    }
}

