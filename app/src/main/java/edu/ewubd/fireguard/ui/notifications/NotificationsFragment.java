package edu.ewubd.fireguard.ui.notifications;

import static edu.ewubd.fireguard.ui.NotificationDatabaseHelper.TABLE_NOTIFICATIONS;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import edu.ewubd.fireguard.R;
import edu.ewubd.fireguard.databinding.FragmentNotificationsBinding;
import edu.ewubd.fireguard.ui.NotificationDatabaseHelper;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private NotificationDatabaseHelper dbHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbHelper = new NotificationDatabaseHelper(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize notification list
        notificationList = new ArrayList<>();
        fetchNotificationsFromDatabase();

        // Initialize adapter
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        // Hide the action bar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        return root;
    }

    private void fetchNotificationsFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification(cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_MESSAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_TIMESTAMP)));
//                notification.setId(cursor.getInt(cursor.getColumnIndexOrThrow(NotificationDatabaseHelper.COLUMN_ID)));

                Log.d("databse_status: ",notification.getTitle());
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}