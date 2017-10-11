package com.example.android.hackbvp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoodLightActivity extends AppCompatActivity {

    private static String BASE_URL;
    private static String CHANGE_URL;


    @BindView(R.id.switch_mood_light)
    Switch mMoodLightSwitch;
    @BindView(R.id.seek_bar_red)
    SeekBar mRedSeekBar;
    @BindView(R.id.seek_bar_green)
    SeekBar mGreenSeekBar;
    @BindView(R.id.seek_bar_blue)
    SeekBar mBlueSeekBar;
    @BindView(R.id.tv_mood_adjust_label)
    TextView mAdjustLabel;
    @BindView(R.id.tv_mood_red_label)
    TextView mRedLabel;
    @BindView(R.id.tv_mood_green_label)
    TextView mGreenLabel;
    @BindView(R.id.tv_mood_blue_label)
    TextView mBlueLabel;
    @BindView(R.id.btn_adjust_light)
    Button mAdjustLightButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_light);
        ButterKnife.bind(this);

        BASE_URL = getString(R.string.api_base_url);
        CHANGE_URL = BASE_URL + "changemeapi/currentbool?status=true";


        initialiseSeekBars();

        mMoodLightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableViews(true);
                } else {
                    enableViews(false);
                }
            }
        });

        mAdjustLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MoodLightActivity.this, "Adjusting Lights", Toast.LENGTH_SHORT).show();
                adjustLights();
            }
        });
    }
    private void adjustLights(){
        String redValue = String.valueOf(mRedSeekBar.getProgress());
        String greenValue = String.valueOf(mGreenSeekBar.getProgress());
        String blueValue = String.valueOf(mBlueSeekBar.getProgress());
        String state = String.valueOf(mMoodLightSwitch.isChecked());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue = QueryUtils.addVolleyHttpRequest(queue,false,CHANGE_URL);
        String url = BASE_URL + "lightapi/change?red=" + redValue +"&blue=" + blueValue + "&green="+greenValue+"&status="+state;
        queue = QueryUtils.addVolleyHttpRequest(queue,false,url);





    }

    private void enableViews(boolean value) {

        mRedSeekBar.setEnabled(value);
        mBlueSeekBar.setEnabled(value);
        mGreenSeekBar.setEnabled(value);
        mAdjustLabel.setEnabled(value);
        mRedLabel.setEnabled(value);
        mGreenLabel.setEnabled(value);
        mBlueLabel.setEnabled(value);
    }

    private void initialiseSeekBars(){

        mRedSeekBar.setMax(255);
        mGreenSeekBar.setMax(255);
        mBlueSeekBar.setMax(255);

        if (mMoodLightSwitch.isChecked()){
            enableViews(true);
        }
        else {
            enableViews(false);
        }

        mRedSeekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        mRedSeekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);

        mGreenSeekBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        mGreenSeekBar.getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

        mBlueSeekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        mBlueSeekBar.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
    }
}
