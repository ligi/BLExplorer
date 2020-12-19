package org.ligi.blexplorer

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import org.ligi.tracedroid.TraceDroid
import java.util.*

class App : Application() {
    override fun onCreate() {
        TraceDroid.init(this)
        super.onCreate()
    }

    companion object {
        lateinit var gatt: BluetoothGatt
        lateinit var service: BluetoothGattService
        lateinit var device: BluetoothDevice
        var notifyingCharacteristicsUUids: MutableList<UUID> = ArrayList()
    }
}