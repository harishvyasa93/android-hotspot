package com.harish.hotspot.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.harish.hotspot.base.controller.HotspotHelper;
import com.harish.hotspot.base.controller.IHotspotManager;
import com.harish.hotspot.base.interfaces.IHotspotStateListener;
import com.harish.hotspot.base.receiver.HotspotReceiver;
import com.harish.hotspot.base.state.HotspotStates;

import static com.harish.hotspot.base.receiver.HotspotReceiver.ACTION_HOTSPOT_STATE_CHANGE;

/**
 * @author HARISH.
 *         <p>
 *         Manages enabling and disabling of hotspot connection.
 *         Follows singleton pattern.
 *         Implementation for Oreo(v26) devices and above.
 * @since 13.07.2018.
 */
@RequiresApi(Build.VERSION_CODES.O)
public final class HotspotManagerV26 implements IHotspotManager {
    //Constants.
    public static final int PERMISSION_LOCATION = 101;
    private static final String TAG = HotspotManagerV26.class.getSimpleName();
    //Singleton instance.
    private static HotspotManagerV26 mInstance;

    //Broadcast Receiver.
    private HotspotReceiver mHotspotReceiver;

    //Hotspot state listener object.
    private IHotspotStateListener mHotspotStateListener;

    //Holds the current hotspot state.
    private int mHotspotState = HotspotStates.WIFI_AP_STATE_UNKNOWN;

    //WifiManager instance.
    private WifiManager mWifiManager;

    //Required only for Oreo and above.
    //Obtained on trying to start hotspot and required to close if not required.
    private WifiManager.LocalOnlyHotspotReservation mHotSpotReservation;
    //This callback si required, to obtain the HotspotReservation object.
    private WifiManager.LocalOnlyHotspotCallback mCallback = new WifiManager.LocalOnlyHotspotCallback() {
        @Override
        public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
            mHotSpotReservation = reservation;
        }
    };

    //Constructor.
    private HotspotManagerV26() {
    }

    //Retrieves the singleton instance.
    public static HotspotManagerV26 getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HotspotManagerV26();
            //Read the initial state of hotspot.
            mInstance.setHotspotState(HotspotHelper
                    .getHotspotState(mInstance.getWifiManager(context)));
        }
        return mInstance;
    }

    /**
     * Getter for the current hotspot state.
     *
     * @return the current hotspot state as integer.
     * @see HotspotStates
     */
    public int getHotspotState() {
        return mHotspotState;
    }

    /**
     * Setter for the Hotspot state.
     *
     * @param mHotspotState The new hotspot state.
     * @see HotspotStates
     */
    public void setHotspotState(int mHotspotState) {
        this.mHotspotState = mHotspotState;
    }

    /**
     * Getter for {@link HotspotManagerV26#mHotspotStateListener} instance.
     */
    public IHotspotStateListener getHotspotStateListener() {
        return mHotspotStateListener;
    }

    /**
     * Setter for {@link #mHotspotStateListener} instance.
     *
     * @param mHotspotStateListener The {@link IHotspotStateListener} instance.
     */
    public void setHotspotStateListener(Context context,
                                        IHotspotStateListener mHotspotStateListener) {
        this.mHotspotStateListener = mHotspotStateListener;
        //Register internally for hotspot state changes.
        registerInternal(context);
    }

    /**
     * Removes the {@link #mHotspotStateListener} instance.
     */
    public void removeHotspotStateListener(Context context) {
        this.mHotspotStateListener = null;
        //Do an un-registration internally.
        unregisterInternal(context);
    }

    /**
     * Internally registers for the hotspot state changes.
     */
    private void registerInternal(Context context) {
        //Create a hotspot instance, if NULL.
        if (mHotspotReceiver == null) {
            mHotspotReceiver = new HotspotReceiver();
        }
        //Register for state change broadcasts.
        context.registerReceiver(mHotspotReceiver, new IntentFilter(ACTION_HOTSPOT_STATE_CHANGE));
    }

    /**
     * Internally unregisters for the hotspot state changes.
     */
    private void unregisterInternal(Context context) {
        if (mHotspotReceiver != null) {
            context.unregisterReceiver(mHotspotReceiver);
            mHotspotReceiver = null;
        }
    }

    /**
     * Enables the hotspot.
     */
    public void enableHotspot(Context context) {
        //Get the WifiManager instance, if NULL.
        if (mWifiManager == null) {
            mWifiManager = getWifiManager(context);
        }

        //Implementation for Lollipop, Marshmallow and Nougat.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            HotspotHelper.enableHotspot(mWifiManager);
        }
        //Implementation for Oreo and above versions.
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Check for runtime permission.
            if (checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                mWifiManager.startLocalOnlyHotspot(mCallback, null);
            }
            //Request permission from user.
            else {
                enforceSelfPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    /**
     * Disables the hotspot.
     */
    public void disableHotspot(Context context) {
        //Get the WifiManager instance, if NULL.
        if (mWifiManager == null) {
            mWifiManager = getWifiManager(context);
        }

        //Implementation for Lollipop, Marshmallow and Nougat.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            HotspotHelper.disableHotspot(mWifiManager);
        }
        //Implementation for Oreo and above versions.
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mHotSpotReservation != null) {
                mHotSpotReservation.close();
                mHotSpotReservation = null;
            } else {
                //On Oreo and above devices, Hotspot can disabled only if enabled by this app!
                Toast.makeText(context, "Hotspot can disabled only if enabled by this app!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Returns the Wifi hotspot configuration.
     *
     * @param context The context.
     * @return {@link WifiConfiguration} instance.
     */
    public WifiConfiguration getHotspotConfiguration(Context context) {
        //Get the WifiManager instance, if NULL.
        if (mWifiManager == null) {
            mWifiManager = getWifiManager(context);
        }

        //Implementation for Lollipop, Marshmallow and Nougat.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return HotspotHelper.getHotspotConfiguration(mWifiManager);
        }
        return null;
    }

    /**
     * Sets the Wifi hotspot configuration.
     *
     * @param context           The context.
     * @param wifiConfiguration {@link WifiConfiguration} instance.
     */
    public void setWifiConfiguration(Context context, WifiConfiguration wifiConfiguration) {

    }

    /**
     * Returns the {@link WifiManager} instance.
     *
     * @param context The context.
     * @return the instance of {@link WifiManager}.
     */
    private WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Checks for runtime permission.
     *
     * @param context    The context.
     * @param permission The permission name.
     * @return TRUE if already permitted, FALSE otherwise.
     */
    private boolean checkSelfPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Enforces user to provide the permission.
     *
     * @param context    The context.
     * @param permission The permission name.
     */
    private void enforceSelfPermission(Activity context, String permission) {
        ActivityCompat.requestPermissions(context, new String[]{permission}, PERMISSION_LOCATION);
    }
}
