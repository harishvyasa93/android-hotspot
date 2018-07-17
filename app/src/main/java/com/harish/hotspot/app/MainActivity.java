package com.harish.hotspot.app;

import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.harish.hotspot.R;
import com.harish.hotspot.base.HotspotManager;
import com.harish.hotspot.base.HotspotManagerV26;
import com.harish.hotspot.base.interfaces.IHotspotStateListener;
import com.harish.hotspot.base.state.HotspotStates;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IHotspotStateListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //Views.
    private EditText etSsid;
    private EditText etPassword;
    private Button btnHotspotState;

    //WifiConfiguration.
    private WifiConfiguration mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize the views.
        initViews();

        //Update the initial state.
        updateState();

        //Fetch the wifi configuration.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //TODO: Fetch the configuration.
        } else {
            mConfig = HotspotManager.getInstance(this)
                    .getHotspotConfiguration(this);
            if (mConfig != null) {
                etSsid.setText(mConfig.SSID);
                etPassword.setText(mConfig.preSharedKey);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HotspotManagerV26.getInstance(this).setHotspotStateListener(this, this);
        } else {
            HotspotManager.getInstance(this).setHotspotStateListener(this, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Unregister here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HotspotManagerV26.getInstance(this).removeHotspotStateListener(this);
        } else {
            HotspotManager.getInstance(this).removeHotspotStateListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_hotspot_state:
                //Make necessary calls.
                //If hotspot is OFF.
                if (HotspotManager.getInstance(this).getHotspotState()
                        == HotspotStates.WIFI_AP_STATE_DISABLED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //TODO: Set configuration before enabling hotspot.
                        HotspotManagerV26.getInstance(this).enableHotspot(this);
                    } else {
                        //Set configuration before enabling hotspot.
                        if (mConfig != null) {
                            mConfig.SSID = etSsid.getText().toString();
                            mConfig.preSharedKey = etPassword.getText().toString();
                            HotspotManager.getInstance(this).setWifiConfiguration(this, mConfig);
                        }
                        HotspotManager.getInstance(this).enableHotspot(this);
                    }
                }
                //If hotspot is ON.
                else if (HotspotManager.getInstance(this).getHotspotState()
                        == HotspotStates.WIFI_AP_STATE_ENABLED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        HotspotManagerV26.getInstance(this).disableHotspot(this);
                    } else {
                        HotspotManager.getInstance(this).disableHotspot(this);
                    }
                }
                break;
        }
    }

    //Initialize the views.
    private void initViews() {
        etSsid = findViewById(R.id.et_ssid);
        etPassword = findViewById(R.id.et_password);

        btnHotspotState = findViewById(R.id.btn_hotspot_state);
        btnHotspotState.setOnClickListener(this);
    }

    //Update the states.
    private void updateState() {
        switch (HotspotManager.getInstance(this).getHotspotState()) {
            case HotspotStates.WIFI_AP_STATE_DISABLED:
            case HotspotStates.WIFI_AP_STATE_DISABLING:
            case HotspotStates.WIFI_AP_STATE_ENABLING:
            case HotspotStates.WIFI_AP_STATE_FAILED:
            case HotspotStates.WIFI_AP_STATE_UNKNOWN:
                btnHotspotState.setText(R.string.text_turn_hotspot_on);
                break;
            case HotspotStates.WIFI_AP_STATE_ENABLED:
                btnHotspotState.setText(R.string.text_turn_hotspot_off);
                break;
        }
    }

    @Override
    public void onEnabling() {
        updateState();
    }

    @Override
    public void onEnabled() {
        updateState();
    }

    @Override
    public void onDisabling() {
        updateState();
    }

    @Override
    public void onDisabled() {
        updateState();
    }

    @Override
    public void onFailed() {
        updateState();
    }
}
