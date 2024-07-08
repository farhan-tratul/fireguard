package edu.ewubd.fireguard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EmergencyActivity extends AppCompatActivity {
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency);
        mainLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onSosButtonClick(View view) {
        changeBackgroundColor();
        new Handler().postDelayed(this::redirectToEmergencyContactList, 3000);
    }

    private void changeBackgroundColor() {
        mainLayout.setBackgroundColor(Color.RED);
    }

    private void redirectToEmergencyContactList() {
        Intent intent = new Intent(EmergencyActivity.this, EmergencyContactListActivity.class);
        startActivity(intent);
    }
}
