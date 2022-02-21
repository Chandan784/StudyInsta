package com.studyinsta.studyinsta.classes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.studyinsta.studyinsta.CourseMaterialActivity;
import com.studyinsta.studyinsta.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

    private List<String> subjectsCollectionList;

    public SubjectsAdapter(List<String> subjectsCollectionList) {
        this.subjectsCollectionList = subjectsCollectionList;
    }

    @NonNull
    @Override
    public SubjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subjects_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectsAdapter.ViewHolder viewHolder, int position) {

        //generating a random color for layout background
        List<String> colorList = new ArrayList<>();
        colorList.add("#FFC000");colorList.add("#FFFC00");colorList.add("#00FFFF");
        colorList.add("#FF7722");colorList.add("#00FF00");colorList.add("#C8F526"); colorList.add("#00E9B7");
        colorList.add("#4DA5FF");

        Random randomizer = new Random();
        String bgColor = colorList.get(randomizer.nextInt(colorList.size()));
        //end generating a random color for layout background

        String subName = subjectsCollectionList.get(position);
        viewHolder.setData(subName, bgColor);
    }

    @Override
    public int getItemCount() {
        return subjectsCollectionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private ConstraintLayout subjectConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.subject_title_tv);
            subjectConstraintLayout = itemView.findViewById(R.id.subjectsConstraintLayout);
        }

        private void setData(final String title, final String subjectsLayoutBg){
            titleText.setText(title);
            subjectConstraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(subjectsLayoutBg)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent courseMaterialIntent = new Intent(itemView.getContext(), CourseMaterialActivity.class);
                    courseMaterialIntent.putExtra("subject_name", title);
                    itemView.getContext().startActivity(courseMaterialIntent);
                }
            });

        }
    }
}
