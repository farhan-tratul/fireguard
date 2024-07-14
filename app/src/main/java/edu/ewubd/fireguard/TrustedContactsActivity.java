package edu.ewubd.fireguard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.ewubd.fireguard.ui.NotificationDatabaseHelper;

public class TrustedContactsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SEND_SMS = 123;

    private ListView contactListView;
    private FloatingActionButton addContactButton;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private NotificationDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        contactListView = findViewById(R.id.contactListView);
        addContactButton = findViewById(R.id.addContactButton);

        dbHelper = new NotificationDatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(contactAdapter);

        // Top color match
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorToolbar));

        loadContactsFromDatabase();

        addContactButton.setOnClickListener(v -> showAddContactDialog());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainTrustedContacts), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkAndRequestSmsPermission();
    }

    // Method to check and request SMS permission
    private void checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    // Handle the result of the SMS permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted to send SMS", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show the dialog to add a new contact
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

    // Show the dialog to edit an existing contact
    private void showEditContactDialog(Contact contact) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_contact);

        EditText contactNameInput = dialog.findViewById(R.id.contactNameInput);
        EditText contactNumberInput = dialog.findViewById(R.id.contactNumberInput);
        Button saveContactButton = dialog.findViewById(R.id.saveContactButton);
        Button cancelContactButton = dialog.findViewById(R.id.cancelContactButton);

        contactNameInput.setText(contact.getName());
        contactNumberInput.setText(contact.getNumber());

        cancelContactButton.setOnClickListener(v -> dialog.dismiss());
        saveContactButton.setOnClickListener(v -> {
            String newName = contactNameInput.getText().toString();
            String newNumber = contactNumberInput.getText().toString();
            if (isValidName(newName) && isValidBDPhoneNumber(newNumber)) {
                updateContactInDatabase(newName, newNumber, contact.getNumber());
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
        String sql = "INSERT INTO " + NotificationDatabaseHelper.TABLE_CONTACTS + " (" + NotificationDatabaseHelper.COLUMN_NAME + ", " + NotificationDatabaseHelper.COLUMN_NUMBER + ") VALUES (?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindString(2, number);
        statement.executeInsert();
    }

    private void updateContactInDatabase(String name, String number, String oldNumber) {
        String sql = "UPDATE " + NotificationDatabaseHelper.TABLE_CONTACTS + " SET " + NotificationDatabaseHelper.COLUMN_NAME + " = ?, " + NotificationDatabaseHelper.COLUMN_NUMBER + " = ? WHERE " + NotificationDatabaseHelper.COLUMN_NUMBER + " = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindString(2, number);
        statement.bindString(3, oldNumber);
        statement.executeUpdateDelete();
    }

    private void loadContactsFromDatabase() {
        contactList.clear();
        Cursor cursor = database.query(NotificationDatabaseHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(NotificationDatabaseHelper.COLUMN_NAME);
            int numberIndex = cursor.getColumnIndex(NotificationDatabaseHelper.COLUMN_NUMBER);

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
            ImageView callButton = convertView.findViewById(R.id.callButton);
            ImageView sosButton = convertView.findViewById(R.id.sosButton);
            ImageView infoButton = convertView.findViewById(R.id.infoButton);
            ImageView editIcon = convertView.findViewById(R.id.editIcon);

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

            editIcon.setOnClickListener(v -> showEditContactDialog(contact));

            return convertView;
        }
    }

    private void sendSosMessage(String phoneNumber) {
        String emergencyMessage = "Emergency! Need help immediately.";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestSmsPermission();
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, emergencyMessage, null, null);
                Toast.makeText(this, "SOS message sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send SOS message", Toast.LENGTH_SHORT).show();
            }
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
