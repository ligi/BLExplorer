package org.ligi.blexplorer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.List;


public class DeviceServiceExploreActivity extends ActionBarActivity {

    @InjectView(R.id.device_card)
    View deviceCard;

    @InjectView(R.id.extras)
    TextView extras;

    private BluetoothDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.item_device);
        ButterKnife.inject(this);

        final DeviceViewHolder deviceViewHolder = new DeviceViewHolder(deviceCard);
        device = getIntent().getParcelableExtra(DeviceViewHolder.EXTRA_KEY_DEVICE);
        deviceViewHolder.applyDevice(device);

        device.connectGatt(DeviceServiceExploreActivity.this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                gatt.connect();
                gatt.discoverServices();
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                final List<BluetoothGattService> services = gatt.getServices();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String res = "";
                        for (final BluetoothGattService service : services) {
                            res += "found service\nuuid:" + service.getUuid() + "\n";
                            res += "type:" + DevicePropertiesDescriber.describeServiceType(service) + "\n";
                        }
                        extras.setText(res);
                    }
                });

                super.onServicesDiscovered(gatt, status);
            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
