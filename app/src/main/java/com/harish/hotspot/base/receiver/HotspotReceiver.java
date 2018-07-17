package com.harish.hotspot.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.harish.hotspot.base.HotspotManager;
import com.harish.hotspot.base.HotspotManagerV26;
import com.harish.hotspot.base.state.HotspotStates;

/**
 * @author HARISH.
 *         <p>
 *         {@link BroadcastReceiver} responsible for listening to hotspot state changes.
 *         This works well with devices above Lollipop (API 21).
 * @since 13.07.2018.
 */
public final class HotspotReceiver extends BroadcastReceiver {
    private static final String TAG = HotspotReceiver.class.getSimpleName();

    //ACTION name.
    //This broadcast action is hidden in the SDK. Still broadcast works.
    public static final String ACTION_HOTSPOT_STATE_CHANGE = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    //EXTRA state key.
    //These keys are hidden in the SDK.
    public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_HOTSPOT_STATE_CHANGE.equalsIgnoreCase(intent.getAction())) {
            handleStateChangeInfo(context, intent.getIntExtra(EXTRA_PREVIOUS_WIFI_AP_STATE, -1),
                    intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1));
        }
    }

    /**
     * Handles the hotspot state change information.
     *
     * @param previousState The previous state.
     * @param nextState     The new state.
     */
    private synchronized void handleStateChangeInfo(Context context, int previousState, int nextState) {
        //Update the state.
        HotspotManager.getInstance(context).setHotspotState(nextState);
        //Call one of the callbacks based on the state.
        switch (nextState) {
            case HotspotStates.WIFI_AP_STATE_DISABLING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (HotspotManagerV26.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManagerV26.getInstance(context).getHotspotStateListener().onDisabling();
                    }
                } else {
                    if (HotspotManager.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManager.getInstance(context).getHotspotStateListener().onDisabling();
                    }
                }
                break;
            case HotspotStates.WIFI_AP_STATE_DISABLED:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (HotspotManagerV26.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManagerV26.getInstance(context).getHotspotStateListener().onDisabled();
                    }
                } else {
                    if (HotspotManager.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManager.getInstance(context).getHotspotStateListener().onDisabled();
                    }
                }
                break;
            case HotspotStates.WIFI_AP_STATE_ENABLING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (HotspotManagerV26.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManagerV26.getInstance(context).getHotspotStateListener().onEnabling();
                    }
                } else {
                    if (HotspotManager.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManager.getInstance(context).getHotspotStateListener().onEnabling();
                    }
                }
                break;
            case HotspotStates.WIFI_AP_STATE_ENABLED:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (HotspotManagerV26.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManagerV26.getInstance(context).getHotspotStateListener().onEnabled();
                    }
                } else {
                    if (HotspotManager.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManager.getInstance(context).getHotspotStateListener().onEnabled();
                    }
                }
                break;
            case HotspotStates.WIFI_AP_STATE_FAILED:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (HotspotManagerV26.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManagerV26.getInstance(context).getHotspotStateListener().onFailed();
                    }
                } else {
                    if (HotspotManager.getInstance(context).getHotspotStateListener() != null) {
                        HotspotManager.getInstance(context).getHotspotStateListener().onFailed();
                    }
                }
                break;
        }
    }
}
