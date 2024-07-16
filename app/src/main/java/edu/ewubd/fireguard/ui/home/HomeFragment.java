package edu.ewubd.fireguard.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import edu.ewubd.fireguard.AboutUsActivity;
import edu.ewubd.fireguard.EmergencyContactsActivity;
import edu.ewubd.fireguard.R;
import edu.ewubd.fireguard.SafetyGuideActivity;
import edu.ewubd.fireguard.TrustedContactsActivity;
import edu.ewubd.fireguard.databinding.FragmentHomeBinding;
import okhttp3.OkHttpClient;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment
{
   RelativeLayout rl;
   TextView safetyTxt;
   Button checkBtn;
    DocumentReference docRef;
    double temp,gas,hum,co;
    AlertDialog progressDialog;
    public interface RetrofitApi {
        @FormUrlEncoded
        @POST("/predict")
        Call<PredictionResponse> createPost(
                @Field("temp") double temp,
                @Field("hum") double hum,
                @Field("gas") double gas,
                @Field("co") double co
        );
    }


    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

// Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        docRef = db.collection("Event_Status").document("Event_Occour001");

        //Emergency Card View Button
        CardView emergencyCardView = root.findViewById(R.id.emergencyButton);
        //Trusted Contacts Card View Button
        CardView trustedContactsCardView = root.findViewById(R.id.trustedContactsButton);
        //Safety Guide Card View Button
        CardView safetyGuideCardView = root.findViewById(R.id.safetyGuideButton);
        //About Us Card View Button
        CardView aboutUsCardView = root.findViewById(R.id.aboutUsButton);
        checkBtn=root.findViewById(R.id.predictBtn);
        rl =root.findViewById(R.id.rlLayoutOne);
        safetyTxt =root.findViewById(R.id.safetyTxt);

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


        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call postData method when the button is clicked
                ProgressBar progressBar = new ProgressBar(getContext());
                progressDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Loading")
                        .setView(progressBar)
                        .setCancelable(false)
                        .create();
                progressDialog.show();
                fetchData();
            }
        });





        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void postData(double temp, double hum,double co, double gas) {
        // Creating an OkHttpClient with increased timeout duration
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        // Creating Retrofit builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fireguard-flask-api.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        // Creating an instance for our Retrofit API class
        RetrofitApi retrofitAPI = retrofit.create(RetrofitApi.class);

        // Calling a method to create a post and passing our fields
        Call<PredictionResponse> call = retrofitAPI.createPost(temp, hum, co, gas);

        // Executing our method
        call.enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = new Gson().toJson(response.body());
                        Log.d("Response", "Raw response body: " + responseBody);

                        // Parse the response body as JSON
                        JSONObject jsonObject = new JSONObject(responseBody);

                        // Extract the "prediction" field
                        String prediction = jsonObject.getString("prediction");
                        Log.d("Response", "Prediction: " + prediction);
                        progressDialog.dismiss();
                        updateSafetyLevel(prediction);
                    } catch (Exception e) {
                        Log.d("Response Error", "Error parsing response: " + e.getMessage());
                    }
                } else {
                    Log.d("Response Error", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Log.d("Error found is:", t.getMessage());
            }
        });
    }
    private void fetchData() {


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                         temp = document.getDouble("temperature");
                         hum = document.getDouble("humidity");
                         gas = document.getDouble("gas_value");
                         co = document.getDouble("co_value");

                        postData(temp, hum, co,gas);


                    } else {
                        Log.d("firebase data error", "No such document");
                    }
                } else {
                    Log.d("firebase data", "get failed with ", task.getException());
                }
            }
        });
    }
    private void updateSafetyLevel(String prediction) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (prediction) {
                    case "2":
                        rl.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.good));
                        safetyTxt.setText("Good Air Quality");
                        checkBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_btn));
                        break;
                    case "1":
                        rl.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.moderate));
                        safetyTxt.setText("Moderate Air Quality");
                        checkBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_btn));
                        break;
                    case "0":
                        rl.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.poor));
                        safetyTxt.setText("Poor Air Quality");
                   checkBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_btn));;
                        break;

                }
            }
        });
    }


}