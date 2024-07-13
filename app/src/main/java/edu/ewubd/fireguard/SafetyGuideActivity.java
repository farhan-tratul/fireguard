package edu.ewubd.fireguard;


import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SafetyGuideActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE = 2;
    private ScrollView scrollView;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_guide);

        scrollView = findViewById(R.id.scrollView);
        contentLayout = findViewById(R.id.contentLayout);

        //top color match
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(scrollView, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        displaySafetyTips();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPdf();
            } else {
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(scrollView.getWidth(), scrollView.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        scrollView.draw(page.getCanvas());
        document.finishPage(page);

        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(directoryPath, "SafetyGuide.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF created successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create PDF", Toast.LENGTH_SHORT).show();
        }

        document.close();
    }

    private void displaySafetyTips() {
        addSection("Kitchen Safety Tips", new String[]{
                "Keep your cooking area clean and free of grease buildup.",
                "Never leave cooking unattended.",
                "Use a timer to remind you of cooking times.",
                "Keep flammable objects away from the stove.",
                "Wear appropriate clothing to avoid loose clothing catching fire.",
                "Ensure child safety by keeping children away from the cooking area.",
                "Check appliances regularly to ensure they are in good working condition."
        });

        addSection("Fire Safety Tips", new String[]{
                "Install and maintain smoke alarms. Test alarms monthly and replace batteries annually.",
                "Know how to use a fire extinguisher. Learn the PASS technique (Pull, Aim, Squeeze, Sweep).",
                "Plan and practice an escape route. Have a clear escape plan and practice it regularly.",
                "Keep a fire blanket in the kitchen to smother small fires.",
                "Never use water on a grease fire. Use a fire extinguisher or cover the pan with a lid.",
                "Be cautious with candles. Keep candles away from flammable objects and never leave candles unattended."
        });

        addSection("Gas Leak Prevention Tips", new String[]{
                "Regularly check for gas leaks. Inspect gas lines and connections for leaks. Use soapy water to check for bubbles on gas lines.",
                "Install a gas detector near gas appliances.",
                "Ensure proper ventilation around gas appliances.",
                "Turn off gas appliances properly. Ensure all knobs and valves are turned off after use.",
                "Know how to turn off the gas supply. Familiarize yourself with the main gas valve."
        });

        addSection("Smoke Safety Tips", new String[]{
                "Ensure proper ventilation throughout the home. Use exhaust fans and open windows to ventilate smoke.",
                "Install smoke detectors in key areas like the kitchen, hallway, and bedrooms.",
                "Avoid smoking indoors. Smoke outside to reduce the risk of indoor smoke buildup.",
                "Check and clean chimneys regularly to ensure they are clear of blockages and debris.",
                "Use air purifiers to reduce smoke particles.",
                "Keep emergency numbers handy. Have emergency contact numbers easily accessible."
        });
    }

    private void addSection(String title, String[] tips) {
        TextView titleTextView = new TextView(this);
        titleTextView.setText(title);
        titleTextView.setTextSize(20);
        titleTextView.setTextColor(getResources().getColor(android.R.color.white));
        titleTextView.setTypeface(titleTextView.getTypeface(), android.graphics.Typeface.BOLD);
        contentLayout.addView(titleTextView);

        for (String tip : tips) {
            TextView tipTextView = new TextView(this);
            tipTextView.setText(tip);
            tipTextView.setTextSize(15);
            tipTextView.setTextColor(getResources().getColor(android.R.color.white));
            tipTextView.setPadding(0, 8, 0, 40);
            contentLayout.addView(tipTextView);
        }
    }
}
