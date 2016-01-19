package org.ligi.blexplorer.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DevicePropertiesDescriber {
    public static String describeBondState(BluetoothDevice device) {
        switch (device.getBondState()) {
            case BluetoothDevice.BOND_NONE:
                return "not bonded";
            case BluetoothDevice.BOND_BONDING:
                return "bonding";
            case BluetoothDevice.BOND_BONDED:
                return "bonded";
            default:
                return "unknown bondstate";
        }
    }

    public static String describeType(BluetoothDevice device) {
        switch (device.getType()) {
            case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                return "classic";
            case BluetoothDevice.DEVICE_TYPE_DUAL:
                return "dual";
            case BluetoothDevice.DEVICE_TYPE_LE:
                return "LE";
            default:
            case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                return "unknown device type";
        }
    }

    public static String getNameOrAddressAsFallback(BluetoothDevice device) {
        return TextUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName();
    }

    public static String describeServiceType(BluetoothGattService service) {
        switch (service.getType()) {
            case BluetoothGattService.SERVICE_TYPE_PRIMARY:
                return "primary";
            case BluetoothGattService.SERVICE_TYPE_SECONDARY:
                return "secondary";
            default:
                return "unknown service type";
        }
    }

    public static String getPermission(BluetoothGattCharacteristic from) {
        switch (from.getPermissions()) {
            case BluetoothGattCharacteristic.PERMISSION_READ:
                return "read";

            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED:
                return "read encrypted";

            case BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM:
                return "read encrypted mitm";

            case BluetoothGattCharacteristic.PERMISSION_WRITE:
                return "write";

            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED:
                return "write encrypted";

            case BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM:
                return "write encrypted mitm";

            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED:
                return "write signed";

            case BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM:
                return "write signed mitm";

            default:
                return "unknown permission" + from.getPermissions();
        }
    }

    public static String getProperty(BluetoothGattCharacteristic from) {
        final int properties = from.getProperties();
        List<String> res = new ArrayList<>();

        if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) > 0) {
            res.add("boadcast");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) > 0) {
            res.add("extended");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            res.add("indicate");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            res.add("notify");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            res.add("read");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) > 0) {
            res.add("signed write");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            res.add("write");
        }

        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            res.add("write no response");
        }

        if (res.isEmpty()) {
            return "no property";
        }

        return TextUtils.join(",", res);
    }

    public static String getServiceName(Context ctx, BluetoothGattService service, String defaultString) {
        try {
            final String serviceKey = service.getUuid().toString().split("-")[0];
            final String cleanServiceKey = serviceKey.replaceFirst("^0+(?!$)", ""); // remove leading zeroes
            final JSONObject jsonObject = new JSONObject(AXT.at(ctx.getAssets().open("services.json")).readToString());
            return jsonObject.getJSONObject(cleanServiceKey).getString("name");
        } catch (IOException | JSONException e) {
            return defaultString;
        }
    }


    public static String connectionStateToString(int state) {
        switch (state) {
            case BluetoothProfile.STATE_DISCONNECTED:
                return "disconnected";
            case BluetoothProfile.STATE_CONNECTING:
                return "connecting";
            case BluetoothProfile.STATE_CONNECTED:
                return "connected";
            case BluetoothProfile.STATE_DISCONNECTING:
                return "disconnecting";
            default:
                return "unknown state:" + state;
        }
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
