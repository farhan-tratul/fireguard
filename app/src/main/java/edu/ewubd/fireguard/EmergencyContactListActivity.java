package edu.ewubd.fireguard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyContactListActivity extends AppCompatActivity {
    private ListView contactListView;
    private String[] contactNames = {"Contact 1", "Contact 2", "Contact 3"};
    private String[] contactNumbers = {"01783814409", "0987654321", "1122334455"};
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_list);

        contactListView = findViewById(R.id.contactListView);
        ContactAdapter adapter = new ContactAdapter(this, contactNames, contactNumbers);
        contactListView.setAdapter(adapter);
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

            nameTextView.setText(names[position]);
            callButton.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + numbers[position]));
                context.startActivity(dialIntent);
            });

            sosButton.setOnClickListener(v -> {
                mainLayout = findViewById(R.id.mainContactListItem);
                changeBackgroundColor();
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

    private void changeBackgroundColor() {
        mainLayout.setBackgroundColor(Color.RED);
        // Revert background color after a delay of 3 seconds
        new Handler().postDelayed(this::resetBackgroundColor, 3000);
    }

    private void resetBackgroundColor() {
        mainLayout.setBackgroundResource(R.color.Original_Color); // Assuming this is your original background
    }
}
