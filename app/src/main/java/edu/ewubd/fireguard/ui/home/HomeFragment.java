package edu.ewubd.fireguard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import edu.ewubd.fireguard.AboutUsActivity;
import edu.ewubd.fireguard.EmergencyContactsActivity;
import edu.ewubd.fireguard.R;
import edu.ewubd.fireguard.SafetyGuideActivity;
import edu.ewubd.fireguard.TrustedContactsActivity;
import edu.ewubd.fireguard.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment
{

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));



        //Emergency Card View Button
        CardView emergencyCardView = root.findViewById(R.id.emergencyButton);
        //Trusted Contacts Card View Button
        CardView trustedContactsCardView = root.findViewById(R.id.trustedContactsButton);
        //Safety Guide Card View Button
        CardView safetyGuideCardView = root.findViewById(R.id.safetyGuideButton);
        //About Us Card View Button
        CardView aboutUsCardView = root.findViewById(R.id.aboutUsButton);

        //Emergency Button Card View Button
        emergencyCardView.setOnClickListener(v -> {
            // Start EmergencyActivity
            Intent intent = new Intent(getActivity(), EmergencyContactsActivity.class);
            startActivity(intent);
        });

        //Trusted Contacts Button Card View Button
        trustedContactsCardView.setOnClickListener(v -> {
            // Start EmergencyActivity
            Intent intent = new Intent(getActivity(), TrustedContactsActivity.class);
            startActivity(intent);
        });

        //Safety Guide Button Card View Button
        safetyGuideCardView.setOnClickListener(v -> {
            // Start EmergencyActivity
            Intent intent = new Intent(getActivity(), SafetyGuideActivity.class);
            startActivity(intent);
        });

        //About Us Button Card View Button
        aboutUsCardView.setOnClickListener(v -> {
            // Start EmergencyActivity
            Intent intent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intent);
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}