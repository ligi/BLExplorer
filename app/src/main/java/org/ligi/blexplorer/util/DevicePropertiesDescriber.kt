package org.ligi.blexplorer.util

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import org.ligi.axt.AXT
import java.io.IOException
import java.util.*

object DevicePropertiesDescriber {

    fun describeBondState(device: BluetoothDevice) = when (device.bondState) {
        BluetoothDevice.BOND_NONE -> "not bonded"
        BluetoothDevice.BOND_BONDING -> "bonding"
        BluetoothDevice.BOND_BONDED -> "bonded"
        else -> "unknown bondstate"
    }


    fun describeType(device: BluetoothDevice) = when (device.type) {
        BluetoothDevice.DEVICE_TYPE_CLASSIC -> "classic"
        BluetoothDevice.DEVICE_TYPE_DUAL -> "dual"
        BluetoothDevice.DEVICE_TYPE_LE -> "LE"
        BluetoothDevice.DEVICE_TYPE_UNKNOWN -> "unknown device type"
        else -> "unknown device type"
    }


    fun getNameOrAddressAsFallback(device: BluetoothDevice) = if (TextUtils.isEmpty(device.name)) device.address else device.name


    fun describeServiceType(service: BluetoothGattService) = when (service.type) {
        BluetoothGattService.SERVICE_TYPE_PRIMARY -> "primary"
        BluetoothGattService.SERVICE_TYPE_SECONDARY -> "secondary"
        else -> "unknown service type"
    }


    fun getPermission(from: BluetoothGattCharacteristic) = when (from.permissions) {
        BluetoothGattCharacteristic.PERMISSION_READ -> "read"

        BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED -> "read encrypted"

        BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM -> "read encrypted mitm"

        BluetoothGattCharacteristic.PERMISSION_WRITE -> "write"

        BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED -> "write encrypted"

        BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM -> "write encrypted mitm"

        BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED -> "write signed"

        BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM -> "write signed mitm"

        else -> "unknown permission" + from.permissions
    }


    fun getProperty(from: BluetoothGattCharacteristic): String {
        val properties = from.properties
        val res = ArrayList<String>()

        if (properties and BluetoothGattCharacteristic.PROPERTY_BROADCAST > 0) {
            res.add("boadcast")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS > 0) {
            res.add("extended")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
            res.add("indicate")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
            res.add("notify")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_READ > 0) {
            res.add("read")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE > 0) {
            res.add("signed write")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0) {
            res.add("write")
        }

        if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
            res.add("write no response")
        }

        if (res.isEmpty()) {
            return "no property"
        }

        return TextUtils.join(",", res)
    }

    fun getServiceName(ctx: Context, service: BluetoothGattService, defaultString: String): String {
        try {
            val serviceKey = service.uuid.toString().split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val cleanServiceKey = serviceKey.replaceFirst("^0+(?!$)".toRegex(), "") // remove leading zeroes
            val jsonObject = JSONObject(AXT.at(ctx.assets.open("services.json")).readToString())
            return jsonObject.getJSONObject(cleanServiceKey).getString("name")
        } catch (e: IOException) {
            return defaultString
        } catch (e: JSONException) {
            return defaultString
        }

    }


    fun connectionStateToString(state: Int) = when (state) {
        BluetoothProfile.STATE_DISCONNECTED -> "disconnected"
        BluetoothProfile.STATE_CONNECTING -> "connecting"
        BluetoothProfile.STATE_CONNECTED -> "connected"
        BluetoothProfile.STATE_DISCONNECTING -> "disconnecting"
        else -> "unknown state:" + state

    }
    /*
    public static String statusToString(int status) {
        switch (status){
            case BluetoothGatt.GATT_SUCCESS:
                return "GATT_SUCCESS";
            case BluetoothGatt.GATT_READ_NOT_PERMITTED:
                return "GATT_READ_NOT_PERMITTED";
            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
                return "GATT_WRITE_NOT_PERMITTED";
            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
                return "GATT_REQUEST_NOT_SUPPORTED";
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
                return "GATT_INSUFFICIENT_AUTHENTICATION";
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                return "GATT_INSUFFICIENT_ENCRYPTION";
            case BluetoothGatt.GATT_INVALID_OFFSET:
                return "GATT_INVALID_OFFSET";
            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
                return "GATT_INVALID_ATTRIBUTE_LENGTH";
            case BluetoothGatt.GATT_FAILURE:
                return "GATT_FAILURE";
            default:
                return "unknown state:" + status;
        }
    }
*/
}
