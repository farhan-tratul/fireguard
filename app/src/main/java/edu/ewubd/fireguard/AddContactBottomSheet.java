package edu.ewubd.fireguard;// AddContactBottomSheet.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddContactBottomSheet extends BottomSheetDialogFragment {

    private EditText contactNameInput;
    private EditText contactNumberInput;
    private Button saveContactButton;
    private Button cancelContactButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_contact, container, false);

        contactNameInput = view.findViewById(R.id.contactNameInput);
        contactNumberInput = view.findViewById(R.id.contactNumberInput);
        saveContactButton = view.findViewById(R.id.saveContactButton);
        cancelContactButton = view.findViewById(R.id.cancelContactButton);

        cancelContactButton.setOnClickListener(v -> dismiss());
        saveContactButton.setOnClickListener(v -> {
            // Handle saving the contact here
            dismiss();
        });

        return view;
    }
}
