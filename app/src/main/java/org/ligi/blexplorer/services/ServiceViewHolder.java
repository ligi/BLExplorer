package org.ligi.blexplorer.services;

import android.bluetooth.BluetoothGattService;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.ligi.axt.AXT;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.characteristics.CharacteristicActivity;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;

public class ServiceViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.name)
    TextView name;

    @InjectView(R.id.uuid)
    TextView uuid;

    @InjectView(R.id.type)
    TextView type;

    public ServiceViewHolder(final View itemView) {
        super(itemView);

        ButterKnife.inject(this, itemView);
    }

    public void applyService(final BluetoothGattService service) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                App.service = service;
                AXT.at(v.getContext()).startCommonIntent().activityFromClass(CharacteristicActivity.class);
            }
        });
        uuid.setText(service.getUuid().toString());
        type.setText(DevicePropertiesDescriber.describeServiceType(service));

        name.setText(DevicePropertiesDescriber.getServiceName(name.getContext(), service, "unknown"));
    }
}
