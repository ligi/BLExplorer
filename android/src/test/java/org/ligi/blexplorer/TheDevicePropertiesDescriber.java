package org.ligi.blexplorer;

import android.bluetooth.BluetoothGattCharacteristic;
import org.junit.Test;
import org.ligi.blexplorer.util.DevicePropertiesDescriber;
import static android.bluetooth.BluetoothGattCharacteristic.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TheDevicePropertiesDescriber {

    @Test
    public void testGetPropertyStringWorks() {
        final BluetoothGattCharacteristic mock = mock(BluetoothGattCharacteristic.class);
        when(mock.getProperties()).thenReturn(PROPERTY_INDICATE | PROPERTY_READ );

        assertThat(DevicePropertiesDescriber.INSTANCE.getProperty(mock)).isEqualTo("indicate,read");
    }
}
