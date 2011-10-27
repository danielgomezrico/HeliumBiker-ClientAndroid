package biker.helium.Managers.Bluetooth;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;

public interface IBluetoothObserver {

	/**
	 * Called when the devices discovering ends
	 * @param devices
	 */
	void deviceDiscoveringFinished(ArrayList<BluetoothDevice> devices);
}
