package org.ligi.blexplorer.characteristiclist.servicelist;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
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


    @OnClick(R.id.read)
    void onReadClick() {
        /*BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean writeDescriptorSuccess = App.gatt.writeDescriptor(descriptor);
        App.gatt.setCharacteristicNotification(characteristic,true);
*/

        App.gatt.readCharacteristic(characteristic);
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
            value.setText(new BigInteger(1, characteristic.getValue()).toString(16));
        } else {
            value.setText("no value read yet");
        }
        type.setText(DevicePropertiesDescriber.getProperty(characteristic));
        permissions.setText(DevicePropertiesDescriber.getPermission(characteristic) + "  " + characteristic.getDescriptors().size());
    }

}
