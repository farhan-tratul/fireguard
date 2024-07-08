package edu.ewubd.fireguard.ui.notifications;

import android.os.Bundle;
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

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize notification list
        notificationList = new ArrayList<>();
        // Add sample notifications
        notificationList.add(new Notification("Title 1", "Message 1", "Timestamp 1"));
        notificationList.add(new Notification("Title 2", "Message 2", "Timestamp 2"));
        notificationList.add(new Notification("Title 8", "Message 3", "Timestamp 3"));

        // Initialize adapter
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);


        // Hide the action bar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}