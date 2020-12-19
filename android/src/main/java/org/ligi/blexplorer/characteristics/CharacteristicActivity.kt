package org.ligi.blexplorer.characteristics

import android.app.Activity
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import de.cketti.shareintentbuilder.ShareIntentBuilder
import org.ligi.blexplorer.App
import org.ligi.blexplorer.databinding.ActivityWithRecyclerBinding
import org.ligi.blexplorer.databinding.ItemCharacteristicBinding
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import java.math.BigInteger
import java.util.*


class CharacteristicActivity : AppCompatActivity() {

    private var serviceList: MutableList<BluetoothGattCharacteristic> = ArrayList()
    private lateinit var binding : ActivityWithRecyclerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWithRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.contentList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = CharacteristicRecycler()
        binding.contentList.adapter = adapter

        serviceList = App.service.characteristics

        App.device.connectGatt(this, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

                App.gatt = gatt
                gatt.discoverServices()
                runOnUiThread {
                    val stateToString = DevicePropertiesDescriber.connectionStateToString(newState)
                    supportActionBar?.subtitle = "$serviceName ($stateToString)"
                }

                super.onConnectionStateChange(gatt, status, newState)
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                super.onCharacteristicRead(gatt, characteristic, status)

                characteristicUpdate(characteristic, adapter)

            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                super.onCharacteristicChanged(gatt, characteristic)

                characteristicUpdate(characteristic, adapter)
            }
        })
    }

    private fun characteristicUpdate(characteristic: BluetoothGattCharacteristic, adapter: CharacteristicRecycler) {
        var found: BluetoothGattCharacteristic? = null
        for (bluetoothGattCharacteristic in serviceList) {
            if (bluetoothGattCharacteristic.uuid == characteristic.uuid) {
                found = bluetoothGattCharacteristic
            }
        }

        if (found == null) {
            serviceList.add(characteristic)
            adapter.notifyDataSetChanged()
        } else {
            val index = serviceList.indexOf(found)
            serviceList[index] = characteristic
            runOnUiThread { adapter.notifyItemChanged(index) }

        }
    }

    private val serviceName: String
        get() = DevicePropertiesDescriber.getServiceName(this@CharacteristicActivity, App.service, App.service.uuid.toString())

    override fun onPause() {
        App.gatt?.disconnect()
        super.onPause()
    }


    private inner class CharacteristicRecycler : androidx.recyclerview.widget.RecyclerView.Adapter<CharacteristicViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CharacteristicViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemCharacteristicBinding.inflate(layoutInflater, viewGroup, false)
            return CharacteristicViewHolder(binding)
        }

        override fun onBindViewHolder(deviceViewHolder: CharacteristicViewHolder, i: Int) {
            deviceViewHolder.applyCharacteristic(serviceList[i])
        }

        override fun getItemCount(): Int {
            return serviceList.size
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}

private class CharacteristicViewHolder(private val binding: ItemCharacteristicBinding) : RecyclerView.ViewHolder(binding.root) {

    private var characteristic: BluetoothGattCharacteristic? = null

    fun applyCharacteristic(characteristic: BluetoothGattCharacteristic) {
        this.characteristic = characteristic
        binding.uuid.text = characteristic.uuid.toString()

        if (characteristic.value != null) {
            binding.value.text = getValue(characteristic)
        } else {
            binding.value.text = "no value read yet"
        }
        binding.type.text = DevicePropertiesDescriber.getProperty(characteristic)
        binding.permissions.text = DevicePropertiesDescriber.getPermission(characteristic) + "  " + characteristic.descriptors.size

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            binding.notify.visibility = View.VISIBLE
            binding.notify.isChecked = App.notifyingCharacteristicsUUids.contains(characteristic.uuid)
        } else {
            binding.notify.visibility = View.GONE
        }

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            binding.read.visibility = View.VISIBLE
        } else {
            binding.read.visibility = View.GONE
        }

        binding.read.setOnClickListener {
            App.gatt.readCharacteristic(characteristic)
        }

        binding.share.setOnClickListener {
            val activity = binding.root.context as Activity
            var text = "characteristic UUID: " + characteristic.uuid.toString() + "\n"
            text += "service UUID: " + characteristic.service.uuid.toString() + "\n"
            if (characteristic.value != null) {
                text += "value: " + getValue(characteristic)
            }
            activity.startActivity(ShareIntentBuilder.from(activity).text(text).build())

        }

        binding.notify.setOnCheckedChangeListener { compoundButton, check ->
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