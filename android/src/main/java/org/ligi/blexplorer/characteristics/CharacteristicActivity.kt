package org.ligi.blexplorer.characteristics

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import org.ligi.blexplorer.App
import org.ligi.blexplorer.R
import org.ligi.blexplorer.databinding.ActivityWithRecyclerBinding
import org.ligi.blexplorer.databinding.ItemCharacteristicBinding
import org.ligi.blexplorer.util.DevicePropertiesDescriber
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
