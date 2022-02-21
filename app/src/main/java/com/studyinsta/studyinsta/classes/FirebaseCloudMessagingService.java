package com.studyinsta.studyinsta.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.studyinsta.studyinsta.ProductDetailsActivity;
import com.studyinsta.studyinsta.R;
import com.studyinsta.studyinsta.classes.notificaion.LocalDatabase;
import com.studyinsta.studyinsta.classes.notificaion.NotificationModel;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {


    private String CHANNEL_ID = "Channel001", productId;
    private boolean redirectToProduct = false;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        createNotificationChannel();

        redirectToProduct = remoteMessage.getData().containsKey("id");

        if (redirectToProduct){
            productId = remoteMessage.getData().get("id");
        }else {
            productId = "";
        }


        triggerNotification(getApplicationContext(),
                CHANNEL_ID,
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                productId);

        captureNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void captureNotification(String title, String body){
        LocalDatabase db = LocalDatabase.getDbInstance(getApplicationContext());
        NotificationModel model = new NotificationModel();
        model.title = title;
        model.body = body;
        db.notificationDao().insertNotification(model);
    }

    private void triggerNotification(Context context, String channel_id, String title, String body, String productID){

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setChannelId(channel_id);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(false);

        if (!productID.equals("")) {
            Intent notificationIntent = new Intent(context, ProductDetailsActivity.class);
            notificationIntent.putExtra("PRODUCT_ID", productID);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent resultIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultIntent);
        }

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1220 , builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "General";
            String description = "General Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
