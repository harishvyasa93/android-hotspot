# android-hotspot
Source code for managing hotspot on Android devices from Lollipop

INTRODUCTION:
This repository provides source code for managing Hotspot, i.e., Enabling/Disabling hotspot from a 3rd Party android application.

The Source code works well from Lollipop devices(API level 21)

From Lollipop till Nougat (21 <= API <= 25)
--------------------------
The hotspot is managed using Reflection APIs as the corresponding methods are hidden and not generally available for 3rd Party applications.

Above Oreo
----------
Android has come up with a new API called LocalOnlyHotspot 
1. https://developer.android.com/reference/android/net/wifi/WifiManager.LocalOnlyHotspotCallback
2. https://developer.android.com/reference/android/net/wifi/WifiManager.LocalOnlyHotspotReservation

These APIs let us to manage the hotspot if the hotspot is enabled by this app.
