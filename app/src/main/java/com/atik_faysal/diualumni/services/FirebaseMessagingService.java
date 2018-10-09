package com.atik_faysal.diualumni.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.atik_faysal.diualumni.R;
import com.atik_faysal.diualumni.main.JobPortal;
import com.google.firebase.messaging.RemoteMessage;
import android.content.Context;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
     @Override
     public void onMessageReceived(RemoteMessage remoteMessage) {
          showNotification(remoteMessage.getData().get("message"));
     }

     private void showNotification(String message) {
          int NOTIFICATION_ID = 234;

          NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

          String CHANNEL_ID = "my_channel_01";
          CharSequence name = "my_channel";
          String Description = "This is my channel";

          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

               int importance = NotificationManager.IMPORTANCE_HIGH;
               NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
               mChannel.setDescription(Description);
               mChannel.enableLights(true);
               mChannel.setLightColor(Color.RED);
               mChannel.enableVibration(true);
               mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
               mChannel.setShowBadge(false);
               notificationManager.createNotificationChannel(mChannel);
          }

          NotificationCompat.Builder builder = new NotificationCompat.Builder(FirebaseMessagingService.this,CHANNEL_ID)
               .setSmallIcon(R.drawable.icon_notification_on)
               .setContentTitle("Diu Alumni")
               .setContentText(message);

          Intent resultIntent = new Intent(this, JobPortal.class);
          TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
          stackBuilder.addParentStack(JobPortal.class);
          stackBuilder.addNextIntent(resultIntent);
          PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

          builder.setContentIntent(resultPendingIntent);

          notificationManager.notify(NOTIFICATION_ID, builder.build());
     }
}
