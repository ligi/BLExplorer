package org.ligi.blexplorer;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.ligi.tracedroid.TraceDroid;

public class App extends Application {
    public static BluetoothGatt gatt;
    public static BluetoothGattService service;
    public static BluetoothDevice device;

    public static List<UUID> notifyingCharacteristicsUUids = new ArrayList<>();

    @Override
    public void onCreate() {
        TraceDroid.init(this);
        super.onCreate();
    }
}
