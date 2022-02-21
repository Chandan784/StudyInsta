package com.studyinsta.studyinsta.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.RegisterActivity;
import com.studyinsta.studyinsta.UpdateUserInfoActivity;
import com.studyinsta.studyinsta.classes.DBqueries;

public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private Button signOutBtn;
    private TextView name, email, phone;
    private ImageButton settingsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_account, container, false);

        name = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.user_email);
        phone = view.findViewById(R.id.user_phone);
        signOutBtn = view.findViewById(R.id.sign_out_btn);
        settingsButton = view.findViewById(R.id.settings_button);

        phone.setText(DBqueries.phone);
        name.setText(DBqueries.fullName);
        email.setText(DBqueries.email);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userDataUpdateIntent = new Intent(getContext(), UpdateUserInfoActivity.class);
                getActivity().startActivity(userDataUpdateIntent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Toast.makeText(getContext(), "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
