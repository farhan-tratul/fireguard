package edu.ewubd.fireguard;


import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ewubd.fireguard.databinding.ActivityMainBinding;
import edu.ewubd.fireguard.ui.NotificationDatabaseHelper;
import edu.ewubd.fireguard.ui.dashboard.DashboardFragment;
import edu.ewubd.fireguard.ui.home.HomeFragment;
import edu.ewubd.fireguard.ui.notifications.Notification;
import edu.ewubd.fireguard.ui.notifications.NotificationAdapter;
import edu.ewubd.fireguard.ui.notifications.NotificationsFragment;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
    private NotificationAdapter adapter;
    private static final String CHANNEL_ID = "fireguard_notification_channel";
    BottomNavigationView bottomNavigationView;


    HomeFragment homeFragment = new HomeFragment();
    DashboardFragment DashboardFragment = new DashboardFragment();
    NotificationsFragment notificationFragment = new NotificationsFragment();

    private static final int NAVIGATION_HOME = R.id.navigation_home;
    private static final int NAVIGATION_DASHBOARD = R.id.navigation_dashboard;
    private static final int NAVIGATION_NOTIFICATION = R.id.navigation_notifications;
    public MediaPlayer mp;
    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NotificationDatabaseHelper dbHelper = new NotificationDatabaseHelper(this);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        mp = MediaPlayer.create(MainActivity.this, R.raw.voice);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case NAVIGATION_HOME:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case NAVIGATION_DASHBOARD:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, DashboardFragment).commit();
                        return true;
                    case NAVIGATION_NOTIFICATION:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                        return true;
                }

                return false;
            }
        });


        db = FirebaseFirestore.getInstance();
        db.collection("gas_notify_Alert")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot snapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        createNotificationChannel();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            switch (dc.getType()) {

                                case MODIFIED:
                                    Log.d(TAG, "Modified document: " + dc.getDocument().getData());


                                        showNotification("Alert!", "Gas Leaking");

                                       dbHelper.insertstatus("Gas", "Gas Leaking", timestamp);

                                        Log.d("database", "inserted");



                                    break;

                            }
                        }
                    }
                });
        db.collection("Fire_notify_Alert")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot snapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        createNotificationChannel();
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            switch (dc.getType()) {

                                case MODIFIED:
                                    Log.d(TAG, "Modified document: " + dc.getDocument().getData());


                                    showNotification("Alert!", "Fire occurring");
                                    playSound();
                                    dbHelper.insertstatus("Fire", "Fire is occouring", timestamp);
                                    Log.d("database", "inserted");



                                    break;

                            }
                        }
                    }
                });



    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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
    private void playSound() {
        if (!mp.isPlaying()) {
            mp.start();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Music has finished playing, stop the service

            }
        });
    }
}
