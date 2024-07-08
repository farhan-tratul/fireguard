package edu.ewubd.fireguard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmergencyContactListActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 123;
    private ListView contactListView;
    private String[] contactNames = {"Contact 1", "Contact 2", "Contact 3"};
    private String[] contactNumbers = {"01783814409", "0987654321", "1122334455"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_list);

        contactListView = findViewById(R.id.contactListView);
        ContactAdapter adapter = new ContactAdapter(this, contactNames, contactNumbers);
        contactListView.setAdapter(adapter);

        checkSmsPermission();
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    private class ContactAdapter extends BaseAdapter {
        private Context context;
        private String[] names;
        private String[] numbers;

        ContactAdapter(Context context, String[] names, String[] numbers) {
            this.context = context;
            this.names = names;
            this.numbers = numbers;
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
            }

            TextView nameTextView = convertView.findViewById(R.id.contactName);
            Button callButton = convertView.findViewById(R.id.callButton);
            Button sosButton = convertView.findViewById(R.id.sosButton);
            Button infoButton = convertView.findViewById(R.id.infoButton);
            RelativeLayout mainLayout = convertView.findViewById(R.id.mainContactListItem);

            nameTextView.setText(names[position]);
            callButton.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + numbers[position]));
                context.startActivity(dialIntent);
            });

            sosButton.setOnClickListener(v -> {
                sendSosMessage(numbers[position]);
                changeBackgroundColor(mainLayout);
            });

            infoButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Contact Information")
                        .setMessage("Name: " + names[position] + "\nPhone: " + numbers[position])
                        .setPositiveButton("OK", null)
                        .show();
            });

            return convertView;
        }
    }

    private void sendSosMessage(String phoneNumber) {
        String emergencyMessage = "Emergency! Need help immediately.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, emergencyMessage, null, null);
            Toast.makeText(this, "SOS message sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to send SOS message", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeBackgroundColor(RelativeLayout mainLayout) {
        mainLayout.setBackgroundColor(Color.RED);
        // Revert background color after a delay of 3 seconds
        new Handler().postDelayed(() -> resetBackgroundColor(mainLayout), 3000);
    }

    private void resetBackgroundColor(RelativeLayout mainLayout) {
        mainLayout.setBackgroundResource(R.color.Original_Color); // Assuming this is your original background
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
