package com.harish.hotspot.base.interfaces;

/**
 * @author HARISH.
 *         <p>
 *         Interface providing various callbacks for hotspot state changes.
 * @since 13.07.2018.
 */
public interface IHotspotStateListener {
    void onEnabling();

    void onEnabled();

    void onDisabling();

    void onDisabled();

    void onFailed();
}
