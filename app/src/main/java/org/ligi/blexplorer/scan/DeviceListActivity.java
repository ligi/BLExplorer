package org.ligi.blexplorer.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;
import org.ligi.blexplorer.R;


public class DeviceListActivity extends ActionBarActivity {

    private static final int REQUEST_ENABLE_BT = 2300;
    @InjectView(R.id.content_list)
    RecyclerView recyclerView;

    List<BluetoothDevice> deviceMap = new ArrayList<>();

    private class DeviceRecycler extends RecyclerView.Adapter<DeviceViewHolder> {
        @Override
        public DeviceViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_device, viewGroup, false);
            final DeviceViewHolder deviceViewHolder = new DeviceViewHolder(v);
            deviceViewHolder.installOnClickListener(DeviceListActivity.this);
            return deviceViewHolder;
        }

        @Override
        public void onBindViewHolder(final DeviceViewHolder deviceViewHolder, final int i) {
            deviceViewHolder.applyDevice(deviceMap.get(i));
        }

        @Override
        public int getItemCount() {
            return deviceMap.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_recycler);
        ButterKnife.inject(this);

        final RecyclerView.Adapter adapter = new DeviceRecycler();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (getBluetooth() == null || !getBluetooth().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startScan();
    }

    private void startScan() {
        getBluetooth().startLeScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                if (!deviceMap.contains(device)) {
                    deviceMap.add(device);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    private BluetoothAdapter getBluetooth() {
        return ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    protected void onPause() {
        getBluetooth().stopLeScan(null);
        super.onPause();
    }
}
