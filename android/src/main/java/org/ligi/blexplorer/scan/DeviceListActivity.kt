package org.ligi.blexplorer.scan

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import org.ligi.blexplorer.HelpActivity
import org.ligi.blexplorer.R
import org.ligi.blexplorer.databinding.ActivityWithRecyclerBinding
import org.ligi.blexplorer.databinding.ItemDeviceBinding
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import java.util.*

class DeviceListActivity : AppCompatActivity() {


    inner class DeviceExtras(val scanRecord: ByteArray, val rssi: Int) {
        val last_seen: Long

        init {
            last_seen = System.currentTimeMillis()
        }

    }

    internal var devices: MutableMap<BluetoothDevice, DeviceExtras> = HashMap()
    private lateinit var binding : ActivityWithRecyclerBinding

    private inner class DeviceRecycler : androidx.recyclerview.widget.RecyclerView.Adapter<DeviceViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DeviceViewHolder {
            val layoutInflater = LayoutInflater.from(viewGroup.context)
            val binding = ItemDeviceBinding.inflate(layoutInflater, viewGroup, false)
            return DeviceViewHolder(binding)
                    .apply { installOnClickListener(this@DeviceListActivity) }
        }

        override fun onBindViewHolder(deviceViewHolder: DeviceViewHolder, i: Int) {
            val bluetoothDevice = devices.keys.toTypedArray()[i]
            deviceViewHolder.applyDevice(bluetoothDevice, devices[bluetoothDevice]!!)
        }

        override fun getItemCount(): Int {
            return devices.size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)

        binding = ActivityWithRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val adapter = DeviceRecycler()

        binding.contentList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.contentList.adapter = adapter

        val timingsUpdateHandler = Handler()

        timingsUpdateHandler.post(object : Runnable {
            override fun run() {
                adapter.notifyDataSetChanged()
                timingsUpdateHandler.postDelayed(this, 500)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        startScan()
    }

    private fun startScan() {
        bluetooth!!.startLeScan { device, rssi, scanRecord -> devices.put(device, DeviceExtras(scanRecord, rssi)) }
    }

    private val bluetooth: BluetoothAdapter?
        get() = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    override fun onResume() {
        super.onResume()
        if (bluetooth == null) {
            AlertDialog.Builder(this).setMessage("Bluetooth is needed").setTitle("Error").setPositiveButton("Exit",{ dialogInterface: DialogInterface, i: Int ->
                this@DeviceListActivity.finish()
            }).show()
        } else if (!bluetooth!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            startScan()
        }
    }

    override fun onPause() {
        if (bluetooth != null) {
            bluetooth!!.stopLeScan(null)
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, HelpActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private val REQUEST_ENABLE_BT = 2300
    }

}
