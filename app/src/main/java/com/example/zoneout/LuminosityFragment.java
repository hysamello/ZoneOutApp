package com.example.zoneout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class LuminosityFragment extends Fragment {

    private SeekBar seekBar;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_luminosity, container, false);

        seekBar = (SeekBar) view.findViewById(R.id.luminositySeekBar);

        textView = (TextView) view.findViewById(R.id.brightness_count);

        int brightness = Settings.System.getInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);

        textView.setText(brightness+"/255");

        seekBar.setProgress(brightness);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Context context = getContext();
                boolean canWrite = Settings.System.canWrite(context);
                if(canWrite){
                    int screenBrightness = progress*255/255;
                    textView.setText(screenBrightness + "/255");
                    Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,screenBrightness);
                }else{
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }
}
