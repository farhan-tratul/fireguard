package edu.ewubd.fireguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TrustedContactsActivity extends AppCompatActivity {

    private ListView contactListView;
    private FloatingActionButton addContactButton;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        contactListView = findViewById(R.id.contactListView);
        addContactButton = findViewById(R.id.addContactButton);

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(contactAdapter);

        addContactButton.setOnClickListener(v -> showAddContactDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTrustedContacts), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showAddContactDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_contact);

        EditText contactNameInput = dialog.findViewById(R.id.contactNameInput);
        EditText contactNumberInput = dialog.findViewById(R.id.contactNumberInput);
        Button saveContactButton = dialog.findViewById(R.id.saveContactButton);
        Button cancelContactButton = dialog.findViewById(R.id.cancelContactButton);

        cancelContactButton.setOnClickListener(v -> dialog.dismiss());
        saveContactButton.setOnClickListener(v -> {
            String name = contactNameInput.getText().toString();
            String number = contactNumberInput.getText().toString();
            if (!name.isEmpty() && !number.isEmpty()) {
                contactList.add(new Contact(name, number));
                contactAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private class ContactAdapter extends BaseAdapter {
        private Context context;
        private List<Contact> contacts;

        ContactAdapter(Context context, List<Contact> contacts) {
            this.context = context;
            this.contacts = contacts;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
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

            Contact contact = contacts.get(position);

            nameTextView.setText(contact.getName());
            callButton.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contact.getNumber()));
                context.startActivity(dialIntent);
            });

            sosButton.setOnClickListener(v -> sendSosMessage(contact.getNumber()));

            infoButton.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Contact Information")
                    .setMessage("Name: " + contact.getName() + "\nPhone: " + contact.getNumber())
                    .setPositiveButton("OK", null)
                    .show());

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

    private static class Contact {
        private final String name;
        private final String number;

        Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }
}
