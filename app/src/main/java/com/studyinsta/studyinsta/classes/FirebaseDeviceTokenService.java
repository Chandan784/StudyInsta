package com.studyinsta.studyinsta.classes;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseDeviceTokenService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("DEVICE TOKEN", s);
        sendRegitrationTokenToServer(s);
    }

    private void sendRegitrationTokenToServer(final String s) {

    }
}
