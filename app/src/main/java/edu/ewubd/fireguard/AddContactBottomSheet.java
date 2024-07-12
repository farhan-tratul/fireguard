package edu.ewubd.fireguard;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.regex.Pattern;

import edu.ewubd.fireguard.ui.NotificationDatabaseHelper;

public class AddContactBottomSheet extends BottomSheetDialogFragment {

    private EditText contactNameInput;
    private EditText contactNumberInput;
    private Button saveContactButton;
    private Button cancelContactButton;
    private NotificationDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_contact, container, false);

        contactNameInput = view.findViewById(R.id.contactNameInput);
        contactNumberInput = view.findViewById(R.id.contactNumberInput);
        saveContactButton = view.findViewById(R.id.saveContactButton);
        cancelContactButton = view.findViewById(R.id.cancelContactButton);
        dbHelper = new NotificationDatabaseHelper(getContext());

        cancelContactButton.setOnClickListener(v -> dismiss());
        saveContactButton.setOnClickListener(v -> {
            String name = contactNameInput.getText().toString();
            String number = contactNumberInput.getText().toString();
            if (isValidName(name) && isValidBDPhoneNumber(number)) {
                saveContactToDatabase(name, number);
                Toast.makeText(getContext(), "Contact saved", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter valid name and phone number", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-Z\\s]+$", name);
    }

    private boolean isValidBDPhoneNumber(String number) {
        return Pattern.matches("^(?:\\+88|88)?(01[3-9]\\d{8})$", number);
    }

    private void saveContactToDatabase(String name, String number) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + NotificationDatabaseHelper.TABLE_CONTACTS + " (" + NotificationDatabaseHelper.COLUMN_NAME + ", " + NotificationDatabaseHelper.COLUMN_NUMBER + ") VALUES (?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, name);
        statement.bindString(2, number);
        statement.executeInsert();
    }
}
