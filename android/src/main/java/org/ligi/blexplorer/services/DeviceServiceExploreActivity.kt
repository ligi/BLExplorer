package org.ligi.blexplorer.services

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.blexplorer.App
import org.ligi.blexplorer.R
import org.ligi.blexplorer.characteristics.CharacteristicActivity
import org.ligi.blexplorer.databinding.ActivityWithRecyclerBinding
import org.ligi.blexplorer.databinding.ItemServiceBinding
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import org.ligi.kaxt.startActivityFromClass
import java.util.*


class DeviceServiceExploreActivity : AppCompatActivity() {

    private val serviceList = ArrayList<BluetoothGattService>()
    private lateinit var binding: ActivityWithRecyclerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWithRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.subtitle = DevicePropertiesDescriber.getNameOrAddressAsFallback(App.device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.contentList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = ServiceRecycler()
        binding.contentList.adapter = adapter

        val loadToast = LoadToast(this).setText(getString(R.string.connecting)).show()

        App.device.connectGatt(this@DeviceServiceExploreActivity, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                App.gatt = gatt
                gatt.discoverServices()
                runOnUiThread { loadToast.setText(getString(R.string.discovering)) }
                super.onConnectionStateChange(gatt, status, newState)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {

                val services = gatt.services
                serviceList.addAll(services)
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                    loadToast.success()
                }
                super.onServicesDiscovered(gatt, status)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        App.gatt?.disconnect()
        super.onPause()
    }

    private inner class ServiceRecycler : androidx.recyclerview.widget.RecyclerView.Adapter<ServiceViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemServiceBinding.inflate(layoutInflater, viewGroup, false)
            return ServiceViewHolder(binding)
        }

        override fun onBindViewHolder(deviceViewHolder: ServiceViewHolder, i: Int) {
            val service = serviceList[i]
            deviceViewHolder.applyService(service)
        }

        override fun getItemCount() = serviceList.size

    }

}

private class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {

    fun applyService(service: BluetoothGattService) {
        itemView.setOnClickListener { v ->
            App.service = service
            v.context.startActivityFromClass(CharacteristicActivity::class.java)
        }
        binding.uuid.text = service.uuid.toString()
        binding.type.text = DevicePropertiesDescriber.describeServiceType(service)
        binding.name.text = DevicePropertiesDescriber.getServiceName(itemView.context, service, "unknown")
    }
}