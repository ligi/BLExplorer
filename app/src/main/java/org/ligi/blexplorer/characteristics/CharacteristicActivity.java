package org.ligi.blexplorer.characteristics;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;


public class CharacteristicActivity extends AppCompatActivity {

    @Bind(R.id.content_list)
    RecyclerView recycler;

    private List<BluetoothGattCharacteristic> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_recycler);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        final CharacteristicRecycler adapter = new CharacteristicRecycler();
        recycler.setAdapter(adapter);

        serviceList = App.service.getCharacteristics();

        App.device.connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {

                App.gatt = gatt;
                gatt.discoverServices();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String stateToString = DevicePropertiesDescriber.connectionStateToString(newState);
                        getSupportActionBar().setSubtitle(getServiceName() + " (" + stateToString + ")");
                    }
                });

                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                characteristicUpdate(characteristic, adapter);

            }

            @Override
            public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                characteristicUpdate(characteristic, adapter);
            }
        });
    }

    private void characteristicUpdate(final BluetoothGattCharacteristic characteristic, final CharacteristicRecycler adapter) {
        BluetoothGattCharacteristic found = null;
        for (final BluetoothGattCharacteristic bluetoothGattCharacteristic : serviceList) {
            if (bluetoothGattCharacteristic.getUuid().equals(characteristic.getUuid())) {
                found = bluetoothGattCharacteristic;
            }
        }

        if (found == null) {
            serviceList.add(characteristic);
            adapter.notifyDataSetChanged();
        } else {
            final int index = serviceList.indexOf(found);
            serviceList.set(index, characteristic);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemChanged(index);
                }
            });

        }
    }

    private String getServiceName() {
        return DevicePropertiesDescriber.getServiceName(CharacteristicActivity.this, App.service, App.service.getUuid().toString());
    }

    @Override
    protected void onPause() {
        if (App.gatt != null) {
            App.gatt.disconnect();
        }
        super.onPause();
    }


    private class CharacteristicRecycler extends RecyclerView.Adapter<CharacteristicViewHolder> {
        @Override
        public CharacteristicViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_characteristic, viewGroup, false);
            return new CharacteristicViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final CharacteristicViewHolder deviceViewHolder, final int i) {
            deviceViewHolder.applyCharacteristic(serviceList.get(i));
        }

        @Override
        public int getItemCount() {
            return serviceList.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
