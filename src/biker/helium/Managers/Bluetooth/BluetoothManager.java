package biker.helium.Managers.Bluetooth;

import java.io.IOException;
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
	
	private IBluetoothObserver mainActivity;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket serverSocket;
	private ArrayList<BluetoothDevice> arrayListDevices;
	private OutputStream outputStream;
	
	private int connectionAttempts;//Counts the number of failure attempts to send a message

	
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
		
		connectionAttempts = 0;
		
		this.mainActivity = mainActivity;
		
		if (mBluetoothAdapter != null) {
						
			if (!mBluetoothAdapter.isEnabled()) {
			    enableBluetoothAdapter( (Activity) mainActivity);
			}
		}
	}
	
	/**
	 * Create the socket connected to the device
	 * @param device
	 * @return socket connected
	 * @throws IOException
	 */
	protected BluetoothSocket connectDevice(BluetoothDevice device) throws IOException{
    	BluetoothSocket serverSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(SERVER_UUID));
//    	Method m;
//    	BluetoothSocket serverSocket = null;
//		try {
//			m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
//			serverSocket = (BluetoothSocket) m.invoke(device, 1);
//		} catch (SecurityException e) {
//		} catch (NoSuchMethodException e) {
//		} catch (IllegalArgumentException e) {
//		} catch (IllegalAccessException e) {
//		} catch (InvocationTargetException e) {
//		}
		if(serverSocket != null){
			serverSocket.connect();
			return serverSocket;
		}else{
			return null;
		}
	}
	
	/**
	 * Sends a string message (with any size) to the connected device
	 * @param message
	 * @throws Exception
	 * @throws IOException
	 */
	protected boolean sendMessage(String message){
		//There will be different threads calling this method and outputStream change inside
		synchronized (this) {
			try{
				if(outputStream != null){ //If there's any outputStream with connection enabled
					byte[] buffer = message.getBytes();

					outputStream.write(buffer);
					connectionAttempts = 0;
					
					return true;
				}else{
					return false;
				}
			}catch (IOException e) {
				connectionAttempts++;//Add one connection attemp

				if(connectionAttempts == 5){//There are 5 attempts to be sure that the connection was lost
					outputStream = null; //Empty the outputStream becouse the connection is lost
					return false;
				}else{
					return true;
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
		
		if(mBluetoothAdapter.isEnabled()){
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
		
		if(serverSocket != null){
			try {
				serverSocket.close();
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
