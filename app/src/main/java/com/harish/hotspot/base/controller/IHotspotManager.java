package com.harish.hotspot.base.controller;

import android.content.Context;

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

    void setHotspotState(int mHotspotState);

    int getHotspotState();

    void setHotspotStateListener(Context context, IHotspotStateListener mHotspotStateListener);

    IHotspotStateListener getHotspotStateListener();

    void removeHotspotStateListener(Context context);
}
