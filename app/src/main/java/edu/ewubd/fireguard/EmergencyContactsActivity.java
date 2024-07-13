package edu.ewubd.fireguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmergencyContactsActivity extends AppCompatActivity {

    private ListView emergencyContactListView;
    private ArrayList<EmergencyContact> emergencyContacts;
    private EmergencyContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        emergencyContactListView = findViewById(R.id.emergencyContactListView);

        // Initialize the list of emergency contacts
        emergencyContacts = new ArrayList<>();
        emergencyContacts.add(new EmergencyContact("Fire Service", "199"));
        emergencyContacts.add(new EmergencyContact("Ambulance", "16263"));
        emergencyContacts.add(new EmergencyContact("Police", "999"));
        emergencyContacts.add(new EmergencyContact("Emergency Medical", "199"));
        emergencyContacts.add(new EmergencyContact("Forewarning", "10941"));

        adapter = new EmergencyContactAdapter(this, emergencyContacts);
        emergencyContactListView.setAdapter(adapter);
    }
}
