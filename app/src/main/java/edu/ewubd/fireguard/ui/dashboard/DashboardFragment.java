package edu.ewubd.fireguard.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.ewubd.fireguard.R;
import edu.ewubd.fireguard.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    double sensorValue;
    private Handler handler = new Handler(Looper.getMainLooper());
    private DonutProgress humidityProgress;
    private DonutProgress gasProgress;
    private TextView temperature;
    private String savedTemperatureValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        humidityProgress = root.findViewById(R.id.Donut_Humidity_Progress); // Replace with your actual DonutProgress ID
        gasProgress = root.findViewById(R.id.Donut_Gas_Progress); // Replace with your actual DonutProgress ID
        temperature = root.findViewById(R.id.temparature); // Replace with your actual DonutProgress ID

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child("Sensor");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData(ref); // calling fetch data function from thread for getting update data 10 seca after
                // Repeat the operation every 10 seconds
                handler.postDelayed(this, 5000);
            }
        }, 5000);

        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        root = binding.getRoot();

        // Hide the action bar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        if (savedInstanceState != null) {
            savedTemperatureValue = savedInstanceState.getString("temperatureValue");
        }
        return root;

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current temperature value
        outState.putString("temperatureValue", temperature.getText().toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the temperature value if it was previously saved
        if (savedTemperatureValue != null) {
            temperature.setText(savedTemperatureValue);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacksAndMessages(null);
    }

    public void fetchData(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through each child node
                    String key = childSnapshot.getKey();
                    Object value = childSnapshot.getValue(); // The value can be of any type

                    // Check the type of the value before using it and get the data

                    if (value instanceof Long) {
                        Long longValue = (Long) value;
                        sensorValue = Double.valueOf(longValue);
                    } else if (value instanceof Double) {
                        sensorValue = (Double) value;

                    }

                    if (key.equals( "Humidity")) {

                        Log.d("humPercentage", String.valueOf(sensorValue));
                        humidityProgress.setProgress((float) sensorValue);

                    }
                    if (key.equals( "gas_status")) {
                        double perc = (sensorValue * 100) / 4095;
                        Log.d("gasPercentage", String.valueOf(perc));
                        double    floorValue= Math.ceil(perc);
                        gasProgress.setProgress((float) floorValue);

                    }

                    if (key .equals( "temperature")) {

                        //Log.d("tempPercentage", String.valueOf(perc));
                        temperature.setText(String.valueOf(sensorValue)+"  °C");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("data", "The read failed: " + databaseError.getCode());

            }
        });
    }


}
