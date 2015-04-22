package org.ligi.blexplorer.servicelist;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;


public class DeviceServiceExploreActivity extends AppCompatActivity {

    @InjectView(R.id.content_list)
    RecyclerView recycler;

    private List<BluetoothGattService> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_with_recycler);
        ButterKnife.inject(this);

        getSupportActionBar().setSubtitle(DevicePropertiesDescriber.getNameOrAddressAsFallback(App.device));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        final ServiceRecycler adapter = new ServiceRecycler();
        recycler.setAdapter(adapter);

        App.device.connectGatt(DeviceServiceExploreActivity.this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                App.gatt = gatt;
                gatt.discoverServices();
                super.onConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                final List<BluetoothGattService> services = gatt.getServices();
                serviceList.addAll(services);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                super.onServicesDiscovered(gatt, status);
            }

        });

    }

    @Override
    protected void onPause() {
        if (App.gatt != null) {
            App.gatt.disconnect();
        }
        super.onPause();
    }


    private class ServiceRecycler extends RecyclerView.Adapter<ServiceViewHolder> {
        @Override
        public ServiceViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            final View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_service, viewGroup, false);
            return new ServiceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ServiceViewHolder deviceViewHolder, final int i) {
            final BluetoothGattService service = serviceList.get(i);
            deviceViewHolder.applyService(service);
        }

        @Override
        public int getItemCount() {
            return serviceList.size();
        }
    }

}
