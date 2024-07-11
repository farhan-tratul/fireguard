package edu.ewubd.fireguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import java.util.regex.Pattern;

public class TrustedContactsActivity extends AppCompatActivity {

    private ListView contactListView;
    private FloatingActionButton addContactButton;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private ContactDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        contactListView = findViewById(R.id.contactListView);
        addContactButton = findViewById(R.id.addContactButton);

        dbHelper = new ContactDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(contactAdapter);

        loadContactsFromDatabase();

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
            if (isValidName(name) && isValidBDPhoneNumber(number)) {
                saveContactToDatabase(name, number);
                loadContactsFromDatabase();
                contactAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter valid name and phone number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z\\s]+$", name);
    }

    private boolean isValidBDPhoneNumber(String number) {
        return Pattern.matches("^(?:\\+88|88)?(01[3-9]\\d{8})$", number);
    }

    private void saveContactToDatabase(String name, String number) {
        String sql = "INSERT INTO " + ContactDatabaseHelper.TABLE_CONTACTS + " (" + ContactDatabaseHelper.COLUMN_NAME + ", " + ContactDatabaseHelper.COLUMN_NUMBER + ") VALUES (?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindString(2, number);
        statement.executeInsert();
    }

    private void loadContactsFromDatabase() {
        contactList.clear();
        Cursor cursor = database.query(ContactDatabaseHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NAME);
            int numberIndex = cursor.getColumnIndex(ContactDatabaseHelper.COLUMN_NUMBER);

            if (nameIndex >= 0 && numberIndex >= 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);
                    contactList.add(new Contact(name, number));
                }
            } else {
                Toast.makeText(this, "Error: Columns not found in database", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        }
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
