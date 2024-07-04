package edu.ewubd.fireguard.ui.dashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import edu.ewubd.fireguard.R;

public class NotificationService extends Service {

    public MediaPlayer mp;
    int request_code;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {

        mp = MediaPlayer.create(NotificationService.this, R.raw.gas_leak);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        playSound();

        sentNotification();


        stopSelf();


        return START_STICKY;
    }
    // Add a listener to the MediaPlayer to stop the service when the music finishes

    @Override
    public void onDestroy() {

        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;

        }
        super.onDestroy();

    }

    private void playSound() {
        if (!mp.isPlaying()) {
            mp.start();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Music has finished playing, stop the service
                stopSelf();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String deccription = "description";
            NotificationChannel channel = new NotificationChannel("1234@ArifinAndroidProject", "sfireguardnotificationchanell", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(deccription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sentNotification() {
        // Intent intent = new Intent(NotificationService.this, SalahTimeTable.class);
//        intent.putExtra("request_code",request_code);
        // PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1234@ArifinAndroidProject")
                    .setSmallIcon(R.drawable.gasleak)//our logo will be here
                    .setContentTitle("!!!! gas leaking!!!!")
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    //.setContentIntent(pendingIntent)
                    .setColor(getResources().getColor(R.color.purple_500))
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());



            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }


}
