package org.ligi.blexplorer.scan;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.ligi.axt.listeners.ActivityFinishingOnClickListener;
import org.ligi.blexplorer.HelpActivity;
import org.ligi.blexplorer.R;
import org.ligi.tracedroid.sending.TraceDroidEmailSender;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DeviceListActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 2300;

    @Bind(R.id.content_list)
    RecyclerView recyclerView;

    public class DeviceExtras {
        public final byte[] scanRecord;
        public final int rssi;
        public final long last_seen;

        public DeviceExtras(final byte[] scanRecord, final int rssi) {
            this.rssi = rssi;
            this.scanRecord = scanRecord;
            last_seen = System.currentTimeMillis();
        }

    }

    Map<BluetoothDevice, DeviceExtras> devices = new HashMap<>();

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
            final BluetoothDevice bluetoothDevice = devices.keySet().toArray(new BluetoothDevice[devices.keySet().size()])[i];
            deviceViewHolder.applyDevice(bluetoothDevice, devices.get(bluetoothDevice));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this);

        setContentView(R.layout.activity_with_recycler);
        ButterKnife.bind(this);

        final RecyclerView.Adapter adapter = new DeviceRecycler();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final Handler timingsUpdateHandler = new Handler();

        timingsUpdateHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                timingsUpdateHandler.postDelayed(this, 500);
            }
        });

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
                devices.put(device, new DeviceExtras(scanRecord, rssi));
            }
        });
    }

    private BluetoothAdapter getBluetooth() {
        return ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getBluetooth() == null) {
            new AlertDialog.Builder(this)
                    .setMessage("Bluetooth is needed")
                    .setTitle("Error")
                    .setPositiveButton("Exit", new ActivityFinishingOnClickListener(this))
                    .show();
        } else if (!getBluetooth().isEnabled()) {
            final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
    }

    @Override
    protected void onPause() {
        if (getBluetooth()!=null) {
            getBluetooth().stopLeScan(null);
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        startActivity(new Intent(this, HelpActivity.class));
        return super.onOptionsItemSelected(item);
    }
}
