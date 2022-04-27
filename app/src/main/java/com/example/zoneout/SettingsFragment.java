package com.example.zoneout;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment implements SensorEventListener {

    Button logoutBtn,deleteBtn;
    Switch switchBtn;
    SensorManager sensorManager;
    Sensor sensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_settings, container, false);


        switchBtn = (Switch) view.findViewById(R.id.luminositySwitch);

        logoutBtn = (Button) view.findViewById(R.id.logoutButton);

        deleteBtn = (Button) view.findViewById(R.id.deleteAccountButton);

        sensorManager = (SensorManager) getActivity().getSystemService(Service.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorManager.unregisterListener(this,sensor);

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchBtn.isChecked()){
                    System.out.println("ta ativado");
                    onResume();
                }else{
                    System.out.println("DESATIVOU BOI");
                    onPause();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.auth.signOut();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot snap : snapshot.getChildren()){
                                if(snap.getChildren().iterator().next().getValue().equals(MainActivity.auth.getCurrentUser().getEmail())){
                                    for (DataSnapshot s : snap.getChildren()){
                                        if(s.getKey().equals("name")){
                                            String username = s.getValue().toString();
                                            MainActivity.auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        MainActivity.reference.child("Users").child(username).removeValue();
                                                        Intent intent = new Intent(getContext(),MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        switchBtn.setChecked(false);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.System.putInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        int newValue = (int) (255f * value * 10/sensor.getMaximumRange());
        System.out.println("O VALUE É " + newValue);
        Settings.System.putInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,Math.round(newValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
