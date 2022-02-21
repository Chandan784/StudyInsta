package com.studyinsta.studyinsta.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.studyinsta.studyinsta.MainActivity;
import com.studyinsta.studyinsta.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    private EditText email;
    private EditText phone;
    private EditText fullName;
    private EditText password;
    private EditText confirmPass;

    private ImageButton closeBtn;
    private Button signUpBtn;

    private ProgressBar progressBar;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        parentFrameLayout = getActivity().findViewById(R.id.register_frameLayout);

        alreadyHaveAnAccount = view.findViewById(R.id.sign_up_sign_in_button);

        email = view.findViewById(R.id.sign_up_email);
        fullName = view.findViewById(R.id.sign_up_full_name);
        phone = view.findViewById(R.id.sign_up_phone);
        password = view.findViewById(R.id.sign_up_password);
        confirmPass = view.findViewById(R.id.sign_up_confirm_password);

        closeBtn = view.findViewById(R.id.sign_up_close_button);
        signUpBtn = view.findViewById(R.id.sign_up_register_button);

        progressBar = view.findViewById(R.id.progressBarSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (disableCloseButton){
            closeBtn.setVisibility(View.GONE);
        } else {
            closeBtn.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkEmailAndPassword();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(fullName.getText())){
            if(!TextUtils.isEmpty(email.getText())){
                if (!TextUtils.isEmpty(phone.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >=6){
                        if (!TextUtils.isEmpty(confirmPass.getText())){
                                signUpBtn.setEnabled(true);
                            }else {
                                confirmPass.setError("Please Confirm Your Password!");
                                signUpBtn.setEnabled(false);
                            }

                        }else{
                            password.setError("Please Enter a Password!");
                            signUpBtn.setEnabled(false);
                        }
                }else{
                    phone.setError("Please Enter Your Phone Number");
                    signUpBtn.setEnabled(false);
                }
            }else {
                email.setError("Please Enter Your Email Address!");
                signUpBtn.setEnabled(false);
            }
        } else {
            email.setError("Please Enter Your Name!");
            signUpBtn.setEnabled(false);

        }
    }

    private void checkEmailAndPassword(){
        if (email.getText().toString().matches(emailPattern)){
            if (confirmPass.getText().toString().equals(password.getText().toString())){

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);
signUpBtn.setText("");
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Map<Object,String> userdata = new HashMap<>();
                                    userdata.put("fullName",fullName.getText().toString());
                                    userdata.put("email",email.getText().toString());
                                    userdata.put("phone",phone.getText().toString());
                                    userdata.put("initialPassword",password.getText().toString());

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                                        // Maps
                                                        Map<String,Object> ratingsMap = new HashMap<>();
                                                        ratingsMap.put("list_size",(long) 0);

                                                        Map<String,Object> cartMap = new HashMap<>();
                                                        cartMap.put("list_size",(long) 0);

                                                        Map<String,Object> coursesMap = new HashMap<>();
                                                        coursesMap.put("list_size",(long) 0);

                                                        Map<String,Object> testsMap = new HashMap<>();
                                                        testsMap.put("list_size",(long) 0);

                                                        Map<String,Object> notesMap = new HashMap<>();
                                                        notesMap.put("list_size",(long) 0);

                                                        Map<String,Object> ebookMap = new HashMap<>();
                                                        ebookMap.put("list_size",(long) 0);

                                                        // End Maps

                                                        //ho gaya problem solve... ab signup kar ke dekho

                                                        final List<String> documentNames = new ArrayList<>();
                                                        documentNames.add("MY_RATINGS");
                                                        documentNames.add("MY_CART");
                                                        documentNames.add("MY_COURSES");
                                                        documentNames.add("MY_TESTS");
                                                        documentNames.add("MY_NOTES");
                                                        documentNames.add("MY_DOWNLOAD");

                                                        final List<Map<String, Object>> documentFields = new ArrayList<>();
                                                        documentFields.add(ratingsMap);
                                                        documentFields.add(cartMap);
                                                        documentFields.add(coursesMap);
                                                        documentFields.add(testsMap);
                                                        documentFields.add(notesMap);
                                                        documentFields.add(ebookMap);

                                                        //everything is correct here.. let me sign up and try

                                                        for (int x = 0; x < documentNames.size() ; x++){
                                                            final int finalX = x;
                                                            userDataReference.document(documentNames.get(x))
                                                                    .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        if (finalX == documentNames.size() -1) {
                                                                            mainIntent();
                                                                        }

                                                                    } else {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        signUpBtn.setEnabled(true);
                                                                        signUpBtn.setText("Sign Up");
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpBtn.setEnabled(true);
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                confirmPass.setError("Confirm Password does not matches with Password!");

            }
        }else {
            email.setError("Please Enter a Valid Email Address!");

        }

    }

    private void mainIntent(){
        if (disableCloseButton){
            disableCloseButton = false;
        }else {
            Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }
}
