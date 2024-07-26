package edu.ewubd.fireguard;

import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

        //top color match
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorToolbar));

        // Initialize the list of emergency contacts
        emergencyContacts = new ArrayList<>();
        emergencyContacts.add(new EmergencyContact("Fire Service\t(১৯৯)", "199"));
        emergencyContacts.add(new EmergencyContact("Ambulance\t(16263)", "16263"));
        emergencyContacts.add(new EmergencyContact("Police\t(৯৯৯)", "999"));
        emergencyContacts.add(new EmergencyContact("দুর্যোগের আগাম বার্তা\t(১০৯৪১)", "10941"));
        emergencyContacts.add(new EmergencyContact("তথ্য সেবা\t(৩৩৩)", "333"));

        adapter = new EmergencyContactAdapter(this, emergencyContacts);
        emergencyContactListView.setAdapter(adapter);
    }
}
