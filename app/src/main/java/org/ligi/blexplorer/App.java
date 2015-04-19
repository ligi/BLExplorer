package org.ligi.blexplorer;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class App extends Application {
    public static BluetoothGatt gatt;
    public static BluetoothGattService service;
    public static BluetoothDevice device;

    public static List<UUID> notifyingCharacteristicsUUids = new ArrayList<>();
}
