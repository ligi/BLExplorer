package org.ligi.blexplorer.scan;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import java.math.BigInteger;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.services.DeviceServiceExploreActivity;
import org.ligi.blexplorer.util.ManufacturerRecordParserFactory;
import org.ligi.blexplorer.util.from_lollipop.ScanRecord;
import static org.ligi.blexplorer.util.DevicePropertiesDescriber.describeBondState;
import static org.ligi.blexplorer.util.DevicePropertiesDescriber.describeType;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.address)
    public TextView address;

    @Bind(R.id.name)
    public TextView name;

    @Bind(R.id.bondstate)
    public TextView bondstate;

    @Bind(R.id.type)
    public TextView type;

    @Bind(R.id.last_seen)
    public TextView last_seen;

    @Bind(R.id.rssi)
    public TextView rssi;

    @Bind(R.id.scan_record)
    public TextView scan_record;

    public BluetoothDevice device;

    public void applyDevice(BluetoothDevice newDevice, DeviceListActivity.DeviceExtras extras) {
        device = newDevice;
        name.setText(TextUtils.isEmpty(device.getName()) ? "no name" : device.getName());
        rssi.setText("" + extras.rssi + "db");
        last_seen.setText("" + (System.currentTimeMillis() - extras.last_seen) / 1000 + "s");
        address.setText(device.getAddress());

        final ScanRecord scanRecord = ScanRecord.parseFromBytes(extras.scanRecord);
        String scanRecordStr = "";
        if (scanRecord.getServiceUuids() != null) {
            for (final ParcelUuid parcelUuid : scanRecord.getServiceUuids()) {
                scanRecordStr += parcelUuid.toString() + "\n";
            }
        }

        final SparseArray<byte[]> manufacturerSpecificData = scanRecord.getManufacturerSpecificData();

        for (int i = 0; i < manufacturerSpecificData.size(); i++) {
            final int key = manufacturerSpecificData.keyAt(i);
            ManufacturerRecordParserFactory.ManufacturerParserBase p = ManufacturerRecordParserFactory.parse(key, manufacturerSpecificData.get(key));
            if (p == null) {
                scanRecordStr += key + "=" + new BigInteger(1, manufacturerSpecificData.get(key)).toString(16) + "\n";
            } else {
                scanRecordStr += p.getKeyDescriptor() + " = {\n" + p.toString() + "}\n";
            }
        }

        for (final ParcelUuid parcelUuid : scanRecord.getServiceData().keySet()) {
            scanRecordStr += parcelUuid + "=" + new BigInteger(1, scanRecord.getServiceData().get(parcelUuid)).toString(16) + "\n";
        }

        scan_record.setText(scanRecordStr);

        type.setText(describeType(device));
        bondstate.setText(describeBondState(device));
    }

    public void installOnClickListener(final Activity activity) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Intent intent = new Intent(activity, DeviceServiceExploreActivity.class);
                App.device = device;
                activity.startActivity(intent);
            }
        });
    }

    public DeviceViewHolder(final View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
