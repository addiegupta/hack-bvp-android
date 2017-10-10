package com.example.android.hackbvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerprintDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FingerprintDialog.Callback {

    private static final int DOOR_REQUEST_CODE = 1;
    private static final int AC_REQUEST_CODE = 2;
    private String KEY_NAME = "scan_fingerprint";
    @BindView(R.id.btn_main_open_door)
    Button mOpenDoorButton;
    @BindView(R.id.btn_main_turn_on_ac)
    Button mTurnOnAcButton;
    private boolean mIsOpenDoorClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mOpenDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOpenDoorClick = true;
                new FingerprintDialog.Builder()
                        .with(MainActivity.this)    // context, must call
                        .setKeyName(KEY_NAME)       // String key name, must call
                        .setRequestCode(DOOR_REQUEST_CODE)         // request code identifier, must call
                        .show();                    // show the dialog
            }
        });
        mTurnOnAcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOpenDoorClick = false;
                new FingerprintDialog.Builder()
                        .with(MainActivity.this)    // context, must call
                        .setKeyName(KEY_NAME)       // String key name, must call
                        .setRequestCode(AC_REQUEST_CODE)         // request code identifier, must call
                        .show();                    // show the dialog
            }
        });

    }

    @Override
    public void onFingerprintDialogAuthenticated() {

        if (mIsOpenDoorClick) {

            Toast.makeText(this, "Opening door", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Turning on AC", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onFingerprintDialogVerifyPassword(FingerprintDialog fingerprintDialog, String s) {
        Toast.makeText(this, "onFPDverifyPassword", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFingerprintDialogStageUpdated(FingerprintDialog fingerprintDialog, FingerprintDialog.Stage stage) {
        Toast.makeText(this, "onFPDSUpdated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFingerprintDialogCancelled() {
        Toast.makeText(this, "onFpDCanceled", Toast.LENGTH_SHORT).show();
    }
}
