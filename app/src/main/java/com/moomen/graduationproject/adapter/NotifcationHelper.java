package com.moomen.graduationproject.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.moomen.graduationproject.R;
import com.moomen.graduationproject.ui.fragment.company.NotificationCompanyFragment;

public class NotifcationHelper extends ContextWrapper {

    public static final String notifcationID = "notifcationID";
    public static final String notifcationName = "notifcationName";

    public NotificationManager manager ;

    public NotifcationHelper(Context base) {
        super(base);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotifcation();
        }
    }

    private void createNotifcation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notifcationID,notifcationName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(R.color.design_default_color_primary);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(channel);
        }


    }

    public NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
    public NotificationCompat.Builder getCannel1Notification(String title , String message){
       /* Intent intent = new Intent(this, NotificationCompanyFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);*/
      return new NotificationCompat.Builder(getApplicationContext(),notifcationID)
              .setContentTitle(title)
              .setContentText(message)
              .setSmallIcon(R.drawable.ic_baseline_add_circle_24)
              .setAutoCancel(true)
/*
              .setContentIntent(pendingIntent)
*/
              .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);



    }
}
