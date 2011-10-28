package biker.helium.Managers.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Have the implementation to connect to a device and send messages through bluetooth
 *
 */
public abstract class BluetoothManager {
	
	private final static int REQUEST_ENABLE_BT = 3;
	private final static String SERVER_UUID = "00112233-4455-6677-8899-aabbccddeeff";
//	private final static String SERVER_NAME = "CARLOS-PC";	
	
	private IBluetoothObserver mainActivity;
	
	private BluetoothAdapter mBluetoothAdapter;
	private ArrayList<BluetoothDevice> arrayListDevices;
	private BluetoothSocket serverSocket;
	private OutputStream outputStream;
	private int connectionTries;

	
	// Create a BroadcastReceiver for ACTION_FOUND and ACTION_DISCOVERY_FINISHED
	protected final BroadcastReceiver devicesSearcher = new BroadcastReceiver() {
			    public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        
			        // When discovery finds a device
			        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			        	
			            // Get the BluetoothDevice object from the Intent
			            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			            
			            // Add the name and address to an array adapter to show in a ListView
			            arrayListDevices.add(device);
			            
			        }
			        
			        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
			        	mBluetoothAdapter.cancelDiscovery();

			    		//Unregister the
			    		((Activity)mainActivity).unregisterReceiver(devicesSearcher);
			    		
			        	mainActivity.deviceDiscoveringFinished(arrayListDevices);
			        }
			    }
			};
	
	public BluetoothManager(IBluetoothObserver mainActivity) throws IOException{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		connectionTries = 0;
		this.mainActivity = mainActivity;
		
		if (mBluetoothAdapter != null) {
						
			if (!mBluetoothAdapter.isEnabled()) {
			    enableBluetoothAdapter( (Activity) mainActivity);
			}
		}
	}
	
	protected BluetoothSocket connectDevice(BluetoothDevice device) throws IOException{
    	BluetoothSocket serverSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID));
    	serverSocket.connect();
    	return serverSocket;
	}
	
	/**
	 * Sends a string message (with any size) to the connected device
	 * @param message
	 * @throws Exception
	 * @throws IOException
	 */
	protected void sendMessage(String message) throws IOException {
		synchronized (this) {
			try{
				if(outputStream != null){
					byte[] buffer = message.getBytes();

					outputStream.write(buffer);
					connectionTries = 0;
				}
			}catch (IOException e) {
				//closeConnection();
				connectionTries++;

				if(connectionTries == 5){
					outputStream = null;
				}else{
					//throw e;
				}
			}
		}
	}
	
	/**
	 * Search Bluetooth Devices asynchronously that will be received by the
	 * BluetoothObserver in deviceDiscoveringFinished
	 * @throws IOException
	 */
	public void searchDevices() throws IOException{
		arrayListDevices = new ArrayList<BluetoothDevice>();

		// Register the BroadcastReceiver to discover
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		((Activity)mainActivity).registerReceiver(devicesSearcher, filter); // Don't forget to unregister during onDestroy
	
		
		mBluetoothAdapter.startDiscovery();
	}
	
	/**
	 * Manage the bluetooth connection with the device
	 * only if the bluetooth adapter is enabled
	 * @throws IOException 
	 */
	public void manageConnection(BluetoothDevice server) throws IOException{
		if(outputStream == null && serverSocket == null && mBluetoothAdapter.isEnabled()){
			if(server != null){

				serverSocket = connectDevice(server);
				
				if(serverSocket != null){
				
					outputStream = serverSocket.getOutputStream();
					
				}else{
					//TODO: throw exceptions
					//throw new IOException("");
				}
			}else{
				//TODO: throw exceptions
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
	public void closeConnection(){
		
		if(outputStream != null){
			try {
				outputStream.close();
				outputStream = null;
			} catch (IOException e) {}
		}

//		if(inputStream != null){
//			try {
//				inputStream.close();
//				inputStream = null;
//			} catch (IOException e) {}
//		}
		
		if(serverSocket != null){
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {}
		}
		
	}

	/**
	 * Intent to enable the bluetooth adapter
	 * @param mainActivity
	 */
	public void enableBluetoothAdapter(Activity mainActivity){
		if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    
		    //It need it to call the onActivityEnd in mainActivity when bluetooth enable
		    enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
	
		    mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		  }
	}
	
	/**
	 * Check if the bluetooth adapter is enabled
	 * @return true if yes false if not
	 */
	public boolean adapterEnabled(){
		if(mBluetoothAdapter != null){
			return mBluetoothAdapter.isEnabled();
		}else{
			return false;
		}
	}
}
