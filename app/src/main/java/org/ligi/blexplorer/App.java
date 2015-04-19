package org.ligi.blexplorer;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

public class App extends Application {
    public static BluetoothGatt gatt;
    public static BluetoothGattService service;
    public static BluetoothDevice device;
}
