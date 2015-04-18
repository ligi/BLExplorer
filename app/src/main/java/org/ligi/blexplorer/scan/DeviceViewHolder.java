package org.ligi.blexplorer.scan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.servicelist.DeviceServiceExploreActivity;
import static org.ligi.blexplorer.util.DevicePropertiesDescriber.describeBondState;
import static org.ligi.blexplorer.util.DevicePropertiesDescriber.describeType;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    public static final String EXTRA_KEY_DEVICE = "device";
    @InjectView(R.id.address)
    public TextView address;

    @InjectView(R.id.mac)
    public TextView mac;

    @InjectView(R.id.bondstate)
    public TextView bondstate;

    @InjectView(R.id.type)
    public TextView type;

    public BluetoothDevice device;

    public void applyDevice(BluetoothDevice device) {
        this.device = device;
        mac.setText(device.getName());
        address.setText(device.getAddress());

        type.setText(describeType(device));
        bondstate.setText(describeBondState(device));
    }

    public void installOnClickListener(final Activity activity) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(activity, DeviceServiceExploreActivity.class);
                intent.putExtra(EXTRA_KEY_DEVICE, device);
                activity.startActivity(intent);
            }
        });
    }

    public DeviceViewHolder(final View itemView) {
        super(itemView);

        ButterKnife.inject(this, itemView);
    }
}
