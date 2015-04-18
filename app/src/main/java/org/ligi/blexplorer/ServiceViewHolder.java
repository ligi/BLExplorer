package org.ligi.blexplorer;

import android.bluetooth.BluetoothGattService;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.ligi.axt.AXT;

class ServiceViewHolder extends RecyclerView.ViewHolder {

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
        uuid.setText(service.getUuid().toString());
        type.setText(DevicePropertiesDescriber.describeServiceType(service));

        final String serviceKey = service.getUuid().toString().split("-")[0];
        final String cleanServiceKey = serviceKey.replaceFirst("^0+(?!$)", ""); // remove leading zeroes

        try {
            final JSONObject jsonObject = new JSONObject(AXT.at(name.getContext().getAssets().open("services.json")).readToString());
            name.setText(jsonObject.getJSONObject(cleanServiceKey).getString("name"));
        } catch (IOException | JSONException e) {
            name.setText("unknown");
        }
    }
}
