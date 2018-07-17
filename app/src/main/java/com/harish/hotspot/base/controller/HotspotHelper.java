package com.harish.hotspot.base.controller;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;

import com.harish.hotspot.base.state.HotspotStates;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author HARISH.
 *         <p>
 *         Class helps us to exercise control over the Wifi AP related APIs.
 *         This class uses Reflection mechanism to access the underlying hidden APIs. So this may not work, if OEMs change the underlying framework.
 *         <p>
 *         NOTE:
 *         Works only for APIs below {@link android.os.Build.VERSION_CODES#N}.
 *         On devices Oreo and above, the following are NOT possible.
 *         1. Enabling/disabling the Hotspot state.
 *         This can be achieved only using the call {@link WifiManager#startLocalOnlyHotspot(WifiManager.LocalOnlyHotspotCallback, Handler)} and receiving the status as a callback.
 * @since 13.07.2018.
 */
public final class HotspotHelper {
    private static final String TAG = HotspotHelper.class.getSimpleName();

    /**
     * Helper method to retrieve the hotspot state.
     *
     * @return TRUE if the API is accessible and if AP enabled, FALSE otherwise.
     */
    public static int getHotspotState(WifiManager wifiManager) {
        int state = HotspotStates.WIFI_AP_STATE_UNKNOWN;
        try {
            state = getWifiApStateInternal(wifiManager);
            //This is just to adapt to the new values returned.
            if (state != HotspotStates.WIFI_AP_STATE_UNKNOWN
                    && state < 10) {
                state += 10;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return state;
    }

    /**
     * Helper method to enable hotspot.
     * Goes with default/pre-existing {@link WifiConfiguration}.
     */
    public static void enableHotspot(WifiManager wifiManager) {
        setWifiApStateInternal(wifiManager, getHotspotConfiguration(wifiManager), true);
    }

    /**
     * Helper method to enable hotspot.
     * Goes with the modified {@link WifiConfiguration}.
     */
    public static void enableHotspot(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        setWifiApStateInternal(wifiManager, wifiConfiguration, true);
    }

    /**
     * Helper method to disable hotspot.
     * Goes with default/pre-existing {@link WifiConfiguration}.
     */
    public static void disableHotspot(WifiManager wifiManager) {
        setWifiApStateInternal(wifiManager, getHotspotConfiguration(wifiManager), false);
    }

    /**
     * Helper method to fetch the {@link WifiConfiguration} instance.
     *
     * @return the {@link WifiConfiguration} instance.
     */
    public static WifiConfiguration getHotspotConfiguration(WifiManager wifiManager) {
        return getWifiApConfigurationInternal(wifiManager);
    }

    /**
     * Sets the modified configuration.
     *
     * @param mConfiguration the {@link WifiConfiguration} instance.
     */
    public static void setHotspotConfiguration(WifiManager wifiManager, WifiConfiguration mConfiguration) {
        setWifiApConfigurationInternal(wifiManager, mConfiguration);
    }

    /**
     * Calls the hidden method getWifiApState()
     *
     * @param wifiManager The {@link WifiManager} instance.
     * @return a valid integer if access to hidden API is successful, {@link HotspotStates#WIFI_AP_STATE_UNKNOWN} if failed.
     * @throws NoSuchMethodException when unable to access the hidden method.
     */
    private static int getWifiApStateInternal(WifiManager wifiManager) throws NoSuchMethodException {
        Method method = getMethodByName("getWifiApState", WifiManager.class);
        if (method != null) {
            try {
                return (int) invokeMethod(method, wifiManager);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return HotspotStates.WIFI_AP_STATE_UNKNOWN;
    }

    /**
     * Calls the hidden method setWifiApState()
     *
     * @param wifiManager The {@link WifiManager} instance.
     * @param apState     TRUE it has to be enabled, FALSE if it has to be disabled.
     */
    private static void setWifiApStateInternal(WifiManager wifiManager,
                                               WifiConfiguration wifiConfiguration,
                                               boolean apState) {
        Method method = getMethodByName("setWifiApEnabled", WifiManager.class);
        if (method != null) {
            try {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(wifiManager, wifiConfiguration, apState);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calls the hidden method getWifiApConfiguration()
     *
     * @param wifiManager The {@link WifiManager} instance.
     * @return {@link WifiConfiguration} object.
     */
    private static WifiConfiguration getWifiApConfigurationInternal(WifiManager wifiManager) {
        Method method = getMethodByName("getWifiApConfiguration", WifiManager.class);
        if (method != null) {
            try {
                return (WifiConfiguration) invokeMethod(method, wifiManager);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Calls the hidden method setWifiApConfiguration()
     *
     * @param wifiManager       The {@link WifiManager} instance.
     * @param wifiConfiguration the {@link WifiConfiguration} instance.
     */
    private static void setWifiApConfigurationInternal(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        Method method = getMethodByName("setWifiApConfiguration", WifiManager.class);
        if (method != null) {
            try {
                invokeMethod(method, wifiManager, wifiConfiguration);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Static method that helps in invoking the hidden methods.
     */
    private static Object invokeMethod(Method method, Object receiver, Object... args)
            throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        return method.invoke(receiver, args);
    }

    /**
     * Fetches the method object by its declaration name.
     *
     * @param methodName The method name.
     * @param className  The class name.
     * @return {@link Method} object.
     */
    private static Method getMethodByName(String methodName, Class className) {
        for (Method declaredMethod : className.getDeclaredMethods()) {
            if (declaredMethod.getName().equalsIgnoreCase(methodName)) {
                return declaredMethod;
            }
        }
        return null;
    }
}
