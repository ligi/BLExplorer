package org.ligi.blexplorer.characteristiclist.servicelist;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import java.math.BigInteger;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;

public class CharacteristicViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.permissions)
    TextView permissions;

    @InjectView(R.id.uuid)
    TextView uuid;

    @InjectView(R.id.type)
    TextView type;

    @InjectView(R.id.value)
    TextView value;

    @InjectView(R.id.notify)
    Switch notify;

    @OnClick(R.id.read)
    void onReadClick() {
        App.gatt.readCharacteristic(characteristic);
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
        ButterKnife.inject(this, itemView);
    }

    public void applyCharacteristic(final BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
        uuid.setText(characteristic.getUuid().toString());

        if (characteristic.getValue() != null) {
            value.setText(new BigInteger(1, characteristic.getValue()).toString(16) +
                          " = " +
                          characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0));
        } else {
            value.setText("no value read yet");
        }
        type.setText(DevicePropertiesDescriber.getProperty(characteristic));
        permissions.setText(DevicePropertiesDescriber.getPermission(characteristic) + "  " + characteristic.getDescriptors().size());

        notify.setChecked(App.notifyingCharacteristicsUUids.contains(characteristic.getUuid()));
    }

}
