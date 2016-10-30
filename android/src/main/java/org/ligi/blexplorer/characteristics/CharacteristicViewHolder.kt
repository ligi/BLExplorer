package org.ligi.blexplorer.characteristics

import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import de.cketti.shareintentbuilder.ShareIntentBuilder
import kotlinx.android.synthetic.main.item_characteristic.view.*
import org.ligi.blexplorer.App
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import java.math.BigInteger

class CharacteristicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var characteristic: BluetoothGattCharacteristic? = null

    fun applyCharacteristic(characteristic: BluetoothGattCharacteristic) {
        this.characteristic = characteristic
        itemView.uuid.text = characteristic.uuid.toString()

        if (characteristic.value != null) {
            itemView.value.text = getValue(characteristic)
        } else {
            itemView.value.text = "no value read yet"
        }
        itemView.type.text = DevicePropertiesDescriber.getProperty(characteristic)
        itemView.permissions.text = DevicePropertiesDescriber.getPermission(characteristic) + "  " + characteristic.descriptors.size

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            itemView.notify.visibility = View.VISIBLE
            itemView.notify.isChecked = App.notifyingCharacteristicsUUids.contains(characteristic.uuid)
        } else {
            itemView.notify.visibility = View.GONE
        }

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            itemView.read.visibility = View.VISIBLE
        } else {
            itemView.read.visibility = View.GONE
        }

        itemView.read.setOnClickListener {
            App.gatt.readCharacteristic(characteristic)
        }

        itemView.share.setOnClickListener {
            val activity = itemView.context as Activity
            var text = "characteristic UUID: " + characteristic.uuid.toString() + "\n"
            text += "service UUID: " + characteristic.service.uuid.toString() + "\n"
            if (characteristic.value != null) {
                text += "value: " + getValue(characteristic)
            }
            activity.startActivity(ShareIntentBuilder.from(activity).text(text).build())

        }

        itemView.notify.setOnCheckedChangeListener { compoundButton, check ->
            if (check) {
                if (!App.notifyingCharacteristicsUUids.contains(characteristic.uuid)) {
                    App.notifyingCharacteristicsUUids.add(characteristic.uuid)
                }
            } else {
                App.notifyingCharacteristicsUUids.remove(characteristic.uuid)
            }

            if (!App.gatt.setCharacteristicNotification(characteristic, check)) {
                Toast.makeText(itemView.context, "setCharacteristicNotification returned false", Toast.LENGTH_LONG).show()
            } else {

                val descriptor = characteristic.descriptors[0]
                descriptor.value = if (check) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                if (!App.gatt.writeDescriptor(descriptor)) {
                    Toast.makeText(itemView.context, "Could not write descriptor for notification", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun getValue(characteristic: BluetoothGattCharacteristic): String {
        return BigInteger(1, characteristic.value).toString(16) +
                " = " +
                characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) +
                " = " +
                characteristic.getStringValue(0)
    }

}
