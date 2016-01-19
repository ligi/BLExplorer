package org.ligi.blexplorer.characteristics;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;

import java.math.BigInteger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.cketti.shareintentbuilder.ShareIntentBuilder;

public class CharacteristicViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.permissions)
    TextView permissions;

    @Bind(R.id.uuid)
    TextView uuid;

    @Bind(R.id.type)
    TextView type;

    @Bind(R.id.value)
    TextView value;

    @Bind(R.id.notify)
    Switch notify;

    @Bind(R.id.read)
    Button read;

    @OnClick(R.id.read)
    void onReadClick() {
        App.gatt.readCharacteristic(characteristic);
    }

    @OnClick(R.id.share)
    void onShare() {
        final Activity activity = (Activity) itemView.getContext();
        String text = "characteristic UUID: " + characteristic.getUuid().toString() + "\n";
        text += "service UUID: " + characteristic.getService().getUuid().toString() + "\n";
        if (characteristic.getValue() != null) {
            text += "value: " + getValue(characteristic);
        }
        activity.startActivity(ShareIntentBuilder.from(activity).text(text).build());
    }

    @OnCheckedChanged(R.id.notify)
    void chedChanged(boolean check) {
        if (check) {
            if (!App.notifyingCharacteristicsUUids.contains(characteristic.getUuid())) {
                App.notifyingCharacteristicsUUids.add(characteristic.getUuid());
            }
        } else {
            App.notifyingCharacteristicsUUids.remove(characteristic.getUuid());
        }

        if (!App.gatt.setCharacteristicNotification(characteristic, check)) {
            Toast.makeText(itemView.getContext(), "setCharacteristicNotification returned false", Toast.LENGTH_LONG).show();
            return;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
        descriptor.setValue(check ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        if (!App.gatt.writeDescriptor(descriptor)) {
            Toast.makeText(itemView.getContext(), "Could not write descriptor for notification", Toast.LENGTH_LONG).show();
        }
    }

    private BluetoothGattCharacteristic characteristic;

    public CharacteristicViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void applyCharacteristic(final BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
        uuid.setText(characteristic.getUuid().toString());

        if (characteristic.getValue() != null) {
            value.setText(getValue(characteristic));
        } else {
            value.setText("no value read yet");
        }
        type.setText(DevicePropertiesDescriber.getProperty(characteristic));
        permissions.setText(DevicePropertiesDescriber.getPermission(characteristic) + "  " + characteristic.getDescriptors().size());

        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            notify.setVisibility(View.VISIBLE);
            notify.setChecked(App.notifyingCharacteristicsUUids.contains(characteristic.getUuid()));
        } else {
            notify.setVisibility(View.GONE);
        }

        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            read.setVisibility(View.VISIBLE);
        } else {
            read.setVisibility(View.GONE);
        }
    }

    private String getValue(final BluetoothGattCharacteristic characteristic) {
        return new BigInteger(1, characteristic.getValue()).toString(16) +
                " = " +
                characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0) +
                " = " +
                characteristic.getStringValue(0);
    }

}
