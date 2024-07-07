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
import androidx.fragment.app.Fragment;


import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.ewubd.fireguard.R;
import edu.ewubd.fireguard.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private static final String TAG = "Firestore";
    private FragmentDashboardBinding binding;

    private double sensorValue;
    private Handler handler = new Handler(Looper.getMainLooper());
    private DonutProgress humidityProgress;
    private DonutProgress gasProgress;
    private TextView temperatureView;
    private CollectionReference eventRef;
    DocumentReference docRef;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        humidityProgress = root.findViewById(R.id.Donut_Humidity_Progress);
        gasProgress = root.findViewById(R.id.Donut_Gas_Progress);
        temperatureView = root.findViewById(R.id.temparature);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

         docRef = db.collection("Event_Status").document("Event_Occour001");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               fetchData();
                handler.postDelayed(this, 5000);
            }


        }, 5000);


        return root;
    }



    private void fetchData() {


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Double temperature = document.getDouble("temperature");
                        Double humidity = document.getDouble("humidity");
                        Double gas_value = document.getDouble("gas_value");
                        Double co_value = document.getDouble("co_value");

                      if(temperature!=null){
                          temperatureView.setText(String.valueOf(temperature) + " °C");
                      }
                      if(humidity!=null){
                          humidityProgress.setProgress(humidity.floatValue());
                      }
                      if(co_value!=null){
                           // humidityProgress.setProgress(humidity.floatValue());
                      }
                      if(gas_value!=null){
                            double perc = (gas_value * 100) / 4095;
                            double floorValue = Math.ceil(perc);
                            gasProgress.setProgress((float) floorValue);
                      }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }





}
