package org.ligi.blexplorer.scan

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import org.ligi.blexplorer.App
import org.ligi.blexplorer.databinding.ItemDeviceBinding
import org.ligi.blexplorer.services.DeviceServiceExploreActivity
import org.ligi.blexplorer.util.DevicePropertiesDescriber.describeBondState
import org.ligi.blexplorer.util.DevicePropertiesDescriber.describeType
import org.ligi.blexplorer.util.ManufacturerRecordParserFactory
import org.ligi.blexplorer.util.from_lollipop.ScanRecord
import java.math.BigInteger

class DeviceViewHolder(private val binding: ItemDeviceBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

    lateinit var device: BluetoothDevice

    fun applyDevice(newDevice: BluetoothDevice, extras: DeviceListActivity.DeviceExtras) {
        device = newDevice
        binding.name.text = if (TextUtils.isEmpty(device.name)) "no name" else device.name
        binding.rssi.text = "${extras.rssi}db"
        binding.lastSeen.text = "" + (System.currentTimeMillis() - extras.last_seen) / 1000 + "s"
        binding.address.text = device.address

        val scanRecord = ScanRecord.parseFromBytes(extras.scanRecord)
        var scanRecordStr = ""
        if (scanRecord.serviceUuids != null) {
            for (parcelUuid in scanRecord.serviceUuids) {
                scanRecordStr += parcelUuid.toString() + "\n"
            }
        }

        val manufacturerSpecificData = scanRecord.manufacturerSpecificData

        (0 until manufacturerSpecificData.size())
                .map { manufacturerSpecificData.keyAt(it) }
                .forEach {key ->
                    val p = ManufacturerRecordParserFactory.parse(key, manufacturerSpecificData.get(key), device)
                    if (p == null) {
                        scanRecordStr += "$key=" + BigInteger(1, manufacturerSpecificData.get(key)).toString(16) + "\n"
                    } else {
                        scanRecordStr += p.keyDescriptor + " = {\n" + p.toString() + "}\n"
                        if (!TextUtils.isEmpty(p.getName(device))) {
                            binding.name.text = p.getName(device)
                        }
                    }
                }

        for (parcelUuid in scanRecord.serviceData.keys) {
            scanRecordStr += "$parcelUuid=" + BigInteger(1, scanRecord.serviceData[parcelUuid]).toString(16) + "\n"
        }

        binding.scanRecord.text = scanRecordStr

        binding.type.text = describeType(device)
        binding.bondstate.text = describeBondState(device)
    }

    fun installOnClickListener(activity: Activity) {
        itemView.setOnClickListener {
            val intent = Intent(activity, DeviceServiceExploreActivity::class.java)
            App.device = device
            activity.startActivity(intent)
        }
    }

}
