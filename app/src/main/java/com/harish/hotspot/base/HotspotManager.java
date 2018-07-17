package com.harish.hotspot.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.harish.hotspot.R;
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
 *         Implementation for devices from Lollipop(v21) to Nougat(v25).
 * @since 13.07.2018.
 */
public final class HotspotManager implements IHotspotManager {
    private static final String TAG = HotspotManager.class.getSimpleName();

    //Singleton instance.
    private static HotspotManager mInstance;

    //Broadcast Receiver.
    private HotspotReceiver mHotspotReceiver;

    //Hotspot state listener object.
    private IHotspotStateListener mHotspotStateListener;

    //Holds the current hotspot state.
    private int mHotspotState = HotspotStates.WIFI_AP_STATE_UNKNOWN;

    //WifiManager instance.
    private WifiManager mWifiManager;

    //Constructor.
    private HotspotManager() {
    }

    //Retrieves the singleton instance.
    public static HotspotManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HotspotManager();
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
     * Getter for {@link HotspotManager#mHotspotStateListener} instance.
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
        //Check for write-settings permission here.
        if (checkSelfPermission(context)) {
            //Get the WifiManager instance, if NULL.
            if (mWifiManager == null) {
                mWifiManager = getWifiManager(context);
            }

            //Implementation for Lollipop, Marshmallow and Nougat.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                HotspotHelper.enableHotspot(mWifiManager);
            }
        } else {
            //Show appropriate message.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                enforceSelfPermission(context);
            } else {
                Toast.makeText(context, "Unable to modify system settings", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Disables the hotspot.
     */
    public void disableHotspot(Context context) {
        //Check for write-settings permission here.
        if (checkSelfPermission(context)) {
            //Get the WifiManager instance, if NULL.
            if (mWifiManager == null) {
                mWifiManager = getWifiManager(context);
            }

            //Implementation for Lollipop, Marshmallow and Nougat.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                HotspotHelper.disableHotspot(mWifiManager);
            }
        } else {
            //Show appropriate message.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                enforceSelfPermission(context);
            } else {
                Toast.makeText(context, "Unable to modify system settings", Toast.LENGTH_LONG).show();
            }
        }
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
     * Checks for WRITE_SETTINGS permission.
     *
     * @param context The context.
     * @return TRUE if OS version is <= Lollipop(v21) or permission already granted.
     */
    private boolean checkSelfPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || Settings.System.canWrite(context);
    }

    /**
     * Enforces the user to provide the permission.
     *
     * @param context The context.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private void enforceSelfPermission(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.text_settings_permission);
        builder.setMessage(R.string.text_settings_permission_description);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}
