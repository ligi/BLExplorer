package org.ligi.blexplorer.services;

import android.bluetooth.BluetoothGattService;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.ligi.axt.AXT;
import org.ligi.blexplorer.App;
import org.ligi.blexplorer.R;
import org.ligi.blexplorer.characteristics.CharacteristicActivity;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ServiceViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.uuid)
    TextView uuid;

    @Bind(R.id.type)
    TextView type;

    public ServiceViewHolder(final View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
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
