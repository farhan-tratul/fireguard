package edu.ewubd.fireguard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EmergencyContactAdapter extends ArrayAdapter<EmergencyContact> {

    public EmergencyContactAdapter(Context context, ArrayList<EmergencyContact> emergencyContacts) {
        super(context, 0, emergencyContacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmergencyContact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.emergency_contact_list_item, parent, false);
        }

        TextView contactName = convertView.findViewById(R.id.contactName);
        ImageView infoButton = convertView.findViewById(R.id.infoButton);
        ImageView callButton = convertView.findViewById(R.id.callButton);

        contactName.setText(contact.getName());

        infoButton.setOnClickListener(v -> {
            // Handle info button click
            Toast.makeText(getContext(), "Info: " + contact.getName(), Toast.LENGTH_SHORT).show();
        });

        callButton.setOnClickListener(v -> {
            // Handle call button click
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
