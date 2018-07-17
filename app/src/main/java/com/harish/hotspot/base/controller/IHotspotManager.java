package com.harish.hotspot.base.controller;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import com.harish.hotspot.base.interfaces.IHotspotStateListener;

/**
 * @author HARISH.
 *         <p>
 *         Interface provides the functionalities that HotspotManager does.
 * @since 16.07.2018.
 */
public interface IHotspotManager {
    void enableHotspot(Context context);

    void disableHotspot(Context context);

    int getHotspotState();

    void setHotspotState(int mHotspotState);

    void setHotspotStateListener(Context context, IHotspotStateListener mHotspotStateListener);

    IHotspotStateListener getHotspotStateListener();

    void removeHotspotStateListener(Context context);

    WifiConfiguration getHotspotConfiguration(Context context);

    void setWifiConfiguration(Context context, WifiConfiguration wifiConfiguration);
}
