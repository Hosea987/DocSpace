package com.example.mountaineer.DocSpace;

import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.mipmap.ic_stat_icons8_stethoscope_filled_50);
////                        .setContentTitle(notification_title)
////                        .setContentText(notification_message);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.ic_stat_icons8_stethoscope_filled_50);
            notification.setColor(getResources().getColor(R.color.orange));
        } else {
            notification.setSmallIcon(R.drawable.ic_stat_icons8_stethoscope_filled_50);
        }

    }

//    private int getNotificationIcon() {
//        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        //If the build version is higher than kitkat we need to create Silhouette icon.
//        return useWhiteIcon ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher;
//    }

}