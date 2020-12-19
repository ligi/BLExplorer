package org.ligi.blexplorer.services

import android.bluetooth.BluetoothGattService
import android.support.v7.widget.RecyclerView
import org.ligi.blexplorer.App
import org.ligi.blexplorer.characteristics.CharacteristicActivity
import org.ligi.blexplorer.databinding.ItemServiceBinding
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import org.ligi.kaxt.startActivityFromClass

class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {

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
