package org.ligi.blexplorer.services

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_with_recycler.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.blexplorer.App
import org.ligi.blexplorer.R
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import org.ligi.snackengage.SnackEngage
import org.ligi.snackengage.snacks.DefaultRateSnack
import java.util.*


class DeviceServiceExploreActivity : AppCompatActivity() {

    private val serviceList = ArrayList<BluetoothGattService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_with_recycler)

        supportActionBar?.subtitle = DevicePropertiesDescriber.getNameOrAddressAsFallback(App.device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        SnackEngage.from(this).withSnack(DefaultRateSnack()).build().engageWhenAppropriate()

        content_list.layoutManager = LinearLayoutManager(this)
        val adapter = ServiceRecycler()
        content_list.adapter = adapter

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

    private inner class ServiceRecycler : RecyclerView.Adapter<ServiceViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_service, viewGroup, false)
            return ServiceViewHolder(v)
        }

        override fun onBindViewHolder(deviceViewHolder: ServiceViewHolder, i: Int) {
            val service = serviceList[i]
            deviceViewHolder.applyService(service)
        }

        override fun getItemCount() = serviceList.size

    }

}
