package com.muhibbin.memes.broadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.muhibbin.memes.MainActivity;
import com.muhibbin.memes.R;
import com.muhibbin.memes.SplashActivity;

import static com.muhibbin.memes.broadcastReceiver.App.CHANNEL_1_ID;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_icon_small)
                .setContentTitle("MEME DROP: W3MG")
                .setContentText("Check the latest memes")
                .setColor(context.getResources().getColor(R.color.black))
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{0, 100, 200, 300})
                .setAutoCancel(true);;
        notificationManager.notify(0, mNotifyBuilder.build());
    }
}
