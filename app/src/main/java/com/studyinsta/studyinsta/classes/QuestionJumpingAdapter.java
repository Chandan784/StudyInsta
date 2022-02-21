package com.studyinsta.studyinsta.classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.studyinsta.studyinsta.QuestionDisplayActivity;
import com.studyinsta.studyinsta.R;

public class QuestionJumpingAdapter extends RecyclerView.Adapter<QuestionJumpingAdapter.ViewHolder> {

    private int x;

    public QuestionJumpingAdapter(int x) {
        this.x = x;
    }

    @NonNull
    @Override
    public QuestionJumpingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_number_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionJumpingAdapter.ViewHolder holder, int position) {

        int s = position +1;
        holder.setData(s);

    }

    @Override
    public int getItemCount() {
        return x;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv = itemView.findViewById(R.id.setsaTV);
        }

        private void setData(final int a){
            tv.setText("" + a);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuestionDisplayActivity.currentQuestion = (a - 1);
                    QuestionDisplayActivity.questDialog.dismiss();
                }
            });




        }
    }
}
