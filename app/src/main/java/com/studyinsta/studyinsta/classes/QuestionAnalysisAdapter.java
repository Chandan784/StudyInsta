package com.studyinsta.studyinsta.classes;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.studyinsta.studyinsta.R;

import java.util.List;

public class QuestionAnalysisAdapter extends RecyclerView.Adapter<QuestionAnalysisAdapter.ViewHolder>{


    private List<QuestionsModel> questionList;
    private List<Integer> indexList;

    public QuestionAnalysisAdapter(List<QuestionsModel> questionList, List<Integer> indexList) {
        this.questionList = questionList;
        this.indexList = indexList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_analysis_layout,parent,false);
        return new QuestionAnalysisAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String question = questionList.get(position).q;
        String correctAns = questionList.get(position).correct;
        String selectedAns;
        if (indexList.get(position) == 0){
            selectedAns = questionList.get(position).a;
        }else if (indexList.get(position) == 1){
            selectedAns = questionList.get(position).b;

        }else if (indexList.get(position) == 2){
            selectedAns = questionList.get(position).c;

        }else if (indexList.get(position) == 3){
            selectedAns = questionList.get(position).d;

        }else {
            selectedAns = "--";
        }

        holder.setData(question, selectedAns,correctAns, position);

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView questTv, correctOption, selectedOption, questionNo;
        private ImageView questIv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            questTv = itemView.findViewById(R.id.analQuest);
            correctOption = itemView.findViewById(R.id.analCorrect);
            selectedOption = itemView.findViewById(R.id.analSelected);
            questionNo = itemView.findViewById(R.id.analNo);
            questIv = itemView.findViewById(R.id.analQuestImage);

        }
        private void setData(String quest, String select, String correct, int posit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (quest.contains(" ")){
                    questIv.setVisibility(View.GONE);
                    questTv.setVisibility(View.VISIBLE);

                    questTv.setText(Html.fromHtml(quest, Html.FROM_HTML_MODE_COMPACT));
                }else {
                    questIv.setVisibility(View.VISIBLE);
                    questTv.setVisibility(View.INVISIBLE);
                    questTv.setHeight(((ImageView) questIv).getHeight());
                    Glide.with(itemView.getContext()).load(quest).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questIv);
                }
                correctOption.setText(Html.fromHtml(correct, Html.FROM_HTML_MODE_COMPACT));
                selectedOption.setText(Html.fromHtml(select, Html.FROM_HTML_MODE_COMPACT));
            }else {

                if (quest.contains(" ")){
                    questIv.setVisibility(View.GONE);
                    questTv.setVisibility(View.VISIBLE);

                    questTv.setText(Html.fromHtml(quest));
                }else {
                    questIv.setVisibility(View.VISIBLE);
                    questTv.setVisibility(View.INVISIBLE);
                    questTv.setHeight(((ImageView) questIv).getHeight());

                    Glide.with(itemView.getContext()).load(quest).transition(GenericTransitionOptions.with(R.anim.fade_in)).apply(new RequestOptions().placeholder(R.drawable.placeholder_image_short)).into(questIv);
                }
                correctOption.setText(Html.fromHtml(correct));
                selectedOption.setText(Html.fromHtml(select));
            }
            questionNo.setText("Q. " + (posit + 1));

            if (select.equals(correct)){
                selectedOption.setTextColor(itemView.getResources().getColor(R.color.successGreen));
            } else if (select.equals("--")){
                selectedOption.setTextColor(ColorStateList.valueOf(Color.parseColor("#595959")));
            }else {
                selectedOption.setTextColor(itemView.getResources().getColor(R.color.btnRed));
            }
        }
    }
}
