package org.ligi.blexplorer.services

import android.bluetooth.BluetoothGattService
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_service.view.*
import org.ligi.blexplorer.App
import org.ligi.blexplorer.characteristics.CharacteristicActivity
import org.ligi.blexplorer.util.DevicePropertiesDescriber
import org.ligi.kaxt.startActivityFromClass

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun applyService(service: BluetoothGattService) {
        itemView.setOnClickListener { v ->
            App.service = service
            v.context.startActivityFromClass(CharacteristicActivity::class.java)
        }
        itemView.uuid.text = service.uuid.toString()
        itemView.type.text = DevicePropertiesDescriber.describeServiceType(service)

        itemView.name.text = DevicePropertiesDescriber.getServiceName(itemView.context, service, "unknown")
    }
}
