package com.example.android.hackbvp;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoodLightActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_light);
        ButterKnife.bind(this);

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

        mRedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mGreenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBlueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
