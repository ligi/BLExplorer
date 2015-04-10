package org.ligi.blexplorer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import static org.ligi.blexplorer.DevicePropertiesDescriber.describeBondState;
import static org.ligi.blexplorer.DevicePropertiesDescriber.describeType;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.content_list)
    RecyclerView recyclerView;

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.address)
        public TextView address;

        @InjectView(R.id.mac)
        public TextView mac;

        @InjectView(R.id.bondstate)
        public TextView bondstate;

        @InjectView(R.id.type)
        public TextView type;

        public DeviceViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    List<BluetoothDevice> deviceMap=new ArrayList<>();

    private class DeviceRecycler extends RecyclerView.Adapter<DeviceViewHolder> {
        @Override
        public DeviceViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.foo, viewGroup, false);
            return new DeviceViewHolder(v) ;
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder deviceViewHolder, final int i) {
            final BluetoothDevice device = deviceMap.get(i);
            
            deviceViewHolder.mac.setText(device.getName() + " " + device.getBondState());
            deviceViewHolder.address.setText(device.getAddress() + " " + device.getType());

            deviceViewHolder.type.setText(describeType(device));
            deviceViewHolder.bondstate.setText(describeBondState(device));
        }

        @Override
        public int getItemCount() {
            return deviceMap.size();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        final RecyclerView.Adapter adapter = new DeviceRecycler();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        BluetoothManager bluetooth = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetooth.getAdapter().startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                if (!deviceMap.contains(device)) {
                    deviceMap.add(device);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }

                Log.i("BLEXplorer","found " +  device.getName());
                device.connectGatt(MainActivity.this, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                        gatt.connect();
                        gatt.discoverServices();
                        super.onConnectionStateChange(gatt, status, newState);
                    }

                    @Override
                    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                        final List<BluetoothGattService> services = gatt.getServices();
                        for (final BluetoothGattService service : services) {
                            Log.i("BLEXplorer","found service" + service.getUuid());
                        }
                        super.onServicesDiscovered(gatt, status);
                    }

                    @Override
                    public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicWrite(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                    }

                    @Override
                    public void onDescriptorRead(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                        super.onDescriptorRead(gatt, descriptor, status);
                    }

                    @Override
                    public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                    }

                    @Override
                    public void onReliableWriteCompleted(final BluetoothGatt gatt, final int status) {
                        super.onReliableWriteCompleted(gatt, status);
                    }

                    @Override
                    public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                    }

                    @Override
                    public void onMtuChanged(final BluetoothGatt gatt, final int mtu, final int status) {
                        super.onMtuChanged(gatt, mtu, status);
                    }
                });
            }
        });
    }

}
