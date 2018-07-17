package com.harish.hotspot.base.state;

/**
 * @author HARISH.
 *         <p>
 *         The various states of Wifi AP.
 *         <p>
 *         In the source code of {@link android.net.wifi.WifiManager}, these states are present but annotated as hidden.
 *         So externally present
 *         <p>
 *         When the broadcast is received, the states are incremented by 10.
 *         For instance, {@link HotspotStates#WIFI_AP_STATE_DISABLING} in the source code actually holds a value 0, but the broadcast gives us a value of 10.
 * @since 13.07.2018.
 */
public class HotspotStates {
    public static final int WIFI_AP_STATE_DISABLING = 10;
    public static final int WIFI_AP_STATE_DISABLED = 11;
    public static final int WIFI_AP_STATE_ENABLING = 12;
    public static final int WIFI_AP_STATE_ENABLED = 13;
    public static final int WIFI_AP_STATE_FAILED = 14;
    public static final int WIFI_AP_STATE_UNKNOWN = -1;

}
