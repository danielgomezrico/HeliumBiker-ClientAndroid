package biker.helium.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

/**
 * Have the implementation to connect to a device and send messages through bluetooth
 *
 */
public abstract class BluetoothManager {
	
	private final static int REQUEST_ENABLE_BT = 3;
	private final static String SERVER_UUID = "cbc995d8-9eca-4a0e-b32d-781e2c314dcf";
	private final static String SERVER_NAME = "DANIEL-PC";	
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket serverSocket;
	private OutputStream outputStream;
	
	public BluetoothManager(Activity mainActivity) throws IOException{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.isEnabled()) {
				
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    
			    //It need it to call the onActivityEnd in mainActivity when bluetooth enable
			    enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);

			    mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    
			}else{
				manageConnection();
			}
		}
	}
	
	protected BluetoothDevice seachServerDevice() throws IOException{
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
		    	if(device.getName().equals(SERVER_NAME)){
		    		return device;
		    	}
		    }
		}
		
		return null;
	}
	
	protected BluetoothSocket connectDevice(BluetoothDevice device) throws IOException{
    	BluetoothSocket serverSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID));
    	serverSocket.connect();
    	return serverSocket;
	}
	
	protected void sendMessage(String message) throws Exception, IOException {
				
		if(outputStream != null){
			
			byte[] buffer = message.getBytes();
			outputStream.write(buffer);
			
		}else{
			throw new Exception("OutputStream is null, the message is not sended");
		}
	}

	/**
	 * Manage the bluetooth connection with the device
	 * only if the bluetooth adapter is enabled
	 * @throws IOException 
	 */
	protected void manageConnection() throws IOException{
		if(outputStream == null && serverSocket == null && mBluetoothAdapter.isEnabled()){

			BluetoothDevice server = this.seachServerDevice();
			
			if(server != null){
				serverSocket = connectDevice(server);
				
				if(serverSocket != null){
					
					outputStream = serverSocket.getOutputStream();
					
				}else{
					//TODO: trhow exceptions
					//throw new IOException("");
				}
			}else{
				//TODO: trhow exceptions
				//throw new IOException("");
			}
		}
		else{
			throw new IOException("The bluetooth adpater is not enabled or there was an error connecting with the device");
		}
	}

	/**
	 * Close the connection
	 */
	protected void closeConnection(){
		
		if(outputStream != null){
			try {
				outputStream.close();
				outputStream = null;
			} catch (IOException e) {}
		}
		
		if(serverSocket != null){
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {}
		}
		
	}
}
