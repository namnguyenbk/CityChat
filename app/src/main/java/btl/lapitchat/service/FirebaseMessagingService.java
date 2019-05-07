package btl.lapitchat.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import btl.lapitchat.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String clickAction  = remoteMessage.getNotification().getClickAction();
        String fromUserId = remoteMessage.getData().get("from_user_id");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "personal_notifications")
                .setSmallIcon(R.mipmap.citychat_icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent resultIntent =  new Intent(clickAction);
        resultIntent.putExtra("userId", fromUserId);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_NO_CREATE);
        builder.setContentIntent(pendingIntent);
        int mNotificationId = (int)System.currentTimeMillis();
        NotificationManager mNotifyManage = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyManage.notify(mNotificationId, builder.build());
    }
}
